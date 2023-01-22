package jpabook.jpashop.service;

import static org.junit.Assert.*;

import javax.persistence.EntityManager;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.repository.OrderRepository;

//단위테스트가 좋지만 JPA 강의니까 DB와 연동해서 테스트 코드 작성
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class OrderServiceTest {

	@Autowired
	EntityManager em;

	@Autowired
	OrderService orderService;
	@Autowired
	OrderRepository orderRepository;

	@Test
	public void 상품주문() throws Exception {
		//given
		final int ORDER_COUNT = 2;
		final int BOOK_PRICE = 10000;
		final int BOOK_STOCK_QUANTITY = 10;

		Member member = createMember();
		Book book = createBook("JPA", BOOK_PRICE, BOOK_STOCK_QUANTITY);

		//when
		Long orderId = orderService.order(member.getId(), book.getId(), ORDER_COUNT);

		//then
		Order getOrder = orderRepository.findOne(orderId);

		assertEquals("상품 주문시 상태는 ORDER", OrderStatus.ORDER, getOrder.getStatus());
		assertEquals("주문한 상품 종류 수가 정확해야 한다.", 1, getOrder.getOrderItems().size());
		assertEquals("주문 가격은 가격 * 수량이다.", BOOK_PRICE * ORDER_COUNT, getOrder.getTotalPrice());
		assertEquals("주문 수량만큼 재고가 줄어야 한다.", BOOK_STOCK_QUANTITY - ORDER_COUNT, book.getStockQuantity());
	}

	@Test(expected = NotEnoughStockException.class)
	public void 상품주문_재고수량초과() throws Exception {
		//given
		final int ORDER_COUNT = 11;
		final int BOOK_PRICE = 10000;
		final int BOOK_STOCK_QUANTITY = 10;

		Member member = createMember();
		Item item = createBook("JPA", BOOK_PRICE, BOOK_STOCK_QUANTITY);

		//when
		orderService.order(member.getId(), item.getId(), ORDER_COUNT);

		//then
		fail("재고 수량 부족 예외가 발생해야 한다.");
	}

	@Test
	public void 주문취소() throws Exception {
		//given
		final int ORDER_COUNT = 2;
		final int BOOK_PRICE = 10000;
		final int BOOK_STOCK_QUANTITY = 10;

		Member member = createMember();
		Item item = createBook("JPA", BOOK_PRICE, BOOK_STOCK_QUANTITY);
		Long orderId = orderService.order(member.getId(), item.getId(), ORDER_COUNT);

		//when
		orderService.cancelOrder(orderId);

		//then
		Order getOrder = orderRepository.findOne(orderId);

		assertEquals("주문 취소시 상태는 CANCLE이다", OrderStatus.CANCEL, getOrder.getStatus());
		assertEquals("주문이 취소된 상품은 그만큼 재고가 증가해야 한다.", BOOK_STOCK_QUANTITY, item.getStockQuantity());
	}

	private Book createBook(String name, int price, int stockQuantity) {
		Book book = new Book();
		book.setName(name);
		book.setPrice(price);
		book.setStockQuantity(stockQuantity);
		em.persist(book);
		return book;
	}

	private Member createMember() {
		Member member = new Member();
		member.setName("회원1");
		member.setAddress(new Address("수원", "영통", "100"));
		em.persist(member);
		return member;
	}
}

