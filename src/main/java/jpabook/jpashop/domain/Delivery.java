package jpabook.jpashop.domain;

import static javax.persistence.FetchType.*;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Delivery {

	@Id
	@GeneratedValue
	@Column(name = "delivery_id")
	private Long id;

	@OneToOne(mappedBy = "delivery", fetch = LAZY)
	private Order order;

	@Embedded
	private Address address;

	@Enumerated(EnumType.STRING) //주의!! ORDINAL은 숫자로 들어가는데 중간에 새로 값이 추가되면 번호가 밀림
	private DeliveryStatus status; //READY, COMP
}
