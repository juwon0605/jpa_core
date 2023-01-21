package jpabook.jpashop.domain;

import javax.persistence.Embeddable;

import lombok.Getter;

@Embeddable
@Getter
// @Setter // 값 타입은 변경이 불가능하게 막아야 좋음
public class Address {

	private String city;
	private String street;
	private String zipcode;

	//값 타입이니 빈 생성자를 public하게 두지 말기
	//그러나 JPA에서 리플렉션이나 다이나믹 프록시 사용하려면 참조할 수 있어야함
	//JPA 스펙상 protected까지 허용가능하니 protected 권장
	protected Address() {
	}

	public Address(String city, String street, String zipcode) {
		this.city = city;
		this.street = street;
		this.zipcode = zipcode;
	}
}
