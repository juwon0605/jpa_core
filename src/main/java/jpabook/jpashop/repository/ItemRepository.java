package jpabook.jpashop.repository;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.stereotype.Repository;

import jpabook.jpashop.domain.item.Item;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ItemRepository {

	private final EntityManager em;

	public void save(Item item) {
		if (item.getId() == null) {
			em.persist(item);
		} else {
			em.merge(item);
			//준영속 상태 객체의 값을 영속 상태 객체에 업데이트하고 반환
			// Item merge = em.merge(item);
			//앞의 merge는 영속성 객체, 뒤의 item은 준영속 상태 객체
			//주의 사항
			//원하는 속성만 선택해서 변경하는 게 아니라 모든 속성이 변경
			//값이 없으면 null이 되어버림 !!(실무에서 쓰기 위험성 있음)
		}
	}

	public Item findOne(Long id) {
		return em.find(Item.class, id);
	}

	public List<Item> findAll() {
		return em.createQuery("select i from Item i", Item.class)
			.getResultList();
	}
}
