package jpabook.jpashop.domain;

import static javax.persistence.FetchType.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED) //static 생성 메서드 사용 강제를 위해 설정
public class Order {

	@Id
	@GeneratedValue
	@Column(name = "order_id")
	private Long id;

	@ManyToOne(fetch = LAZY) //default가 EAGER
	@JoinColumn(name = "member_id")
	private Member member;

	//JPQL select o From order o; -> SQL select * from order (order를 조회할 때 멤버를 중복 조회하면 n+1 문제!)

	@OneToMany(mappedBy = "order", // default가 LAZY
		cascade = CascadeType.ALL) // 관련된 update(persist) 전파
	private List<OrderItem> orderItems = new ArrayList<>();

	@OneToOne(fetch = LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "delivery_id")
	private Delivery delivery;

	private LocalDateTime orderDate; //주문시간

	@Enumerated(EnumType.STRING)
	private OrderStatus status; //주문상태 [ORDER, CANCEL]

	//==연관관계 편의 메서드==//
	//DB에서는 연관관계 주인 기준으로 단방향 매핑되지만, 어플리케이션에서 논리적으로 양방향 연관관계 매핑해기 위해 정의
	//항상 직접 비즈니스 로직에서 매핑해주기보다 Entity에서 정의
	//두 연관관계 중 자주 조회하는 기준으로 정의
	public void setMember(Member member) {
		this.member = member;
		member.getOrders().add(this);
	}

	public void addOrderItem(OrderItem orderItem) {
		orderItems.add(orderItem);
		orderItem.setOrder(this);
	}

	public void setDelivery(Delivery delivery) {
		this.delivery = delivery;
		delivery.setOrder(this);
	}

	//==생성 메서드==//
	//객체끼리 연관되어 있는 객체의 생성은 묶어서 별도로 만들어주면 좋다
	public static Order createOrder(Member member, Delivery delivery, OrderItem... orderItems) {
		Order order = new Order();
		order.setMember(member);
		order.setDelivery(delivery);
		Arrays.stream(orderItems).forEach(order::addOrderItem);
		order.setStatus(OrderStatus.ORDER);
		order.setOrderDate(LocalDateTime.now());
		return order;
	}

	//==비즈니스 로직==//

	/**
	 * 주문 취소
	 */
	public void cancel() {
		if (this.delivery.getStatus() == DeliveryStatus.COMP) {
			throw new IllegalStateException("이미 배송완료된 상품은 취소가 불가능합니다.");
		}
		this.setStatus(OrderStatus.CANCEL);
		this.orderItems.forEach(OrderItem::cancel);
	}

	//==조회 로직==//

	/**
	 * 전체 주문 가격 조회
	 */
	public int getTotalPrice() {
		return this.orderItems.stream()
			.mapToInt(OrderItem::getTotalPrice)
			.sum();
	}
}
