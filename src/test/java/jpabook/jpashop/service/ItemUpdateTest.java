package jpabook.jpashop.service;

import javax.persistence.EntityManager;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import jpabook.jpashop.domain.item.Book;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ItemUpdateTest {

	@Autowired
	EntityManager em;

	@Test
	public void updateTest() throws Exception {
		Book book = em.find(Book.class, 1L);

		//트랜잭션
		book.setName("asdfasdf");

		//트랜잭션 commit
		//변경 감지 == dirty checking
	}
}
