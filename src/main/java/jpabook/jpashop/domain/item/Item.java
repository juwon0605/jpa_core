package jpabook.jpashop.domain.item;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToMany;

import jpabook.jpashop.domain.Category;
import jpabook.jpashop.exception.NotEnoughStockException;
import lombok.Getter;
import lombok.Setter;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")
@Getter
@Setter
public abstract class Item {

	@Id
	@GeneratedValue
	@Column(name = "item_id")
	private Long id;

	private String name;
	private int price;
	private int stockQuantity;

	@ManyToMany(mappedBy = "items")
	private List<Category> categories = new ArrayList<>();

	//==비즈니스 로직==//
	//도메인 객체가 자신의 변수에 관련된 함수를 가지고 있는 것이 좀 더 응집도가 높고 객체지향적이다

	/**
	 * stock 증가
	 */
	public void addStock(int quantity) {
		this.stockQuantity += quantity;
	}

	/**
	 * stock 감소
	 */
	public void removeStock(int quantity) {
		int restStock = this.stockQuantity - quantity;
		if (restStock < 0) {
			throw new NotEnoughStockException("need more stock");
		}
		this.stockQuantity = restStock;
	}
}
