package jpabook.jpashop.api;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * xToOne (ManyToTOne, OneToOne)
 * Order
 * Order -> Member (ManyToOne)
 * Order -> Delivery (OneToOne)
 */
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

	private final OrderRepository orderRepository;
	private final OrderSimpleQueryRepository orderSimpleQueryRepository;

	@GetMapping("/api/v1/simple-orders")
	public List<Order> ordersV1() {
		List<Order> all = orderRepository.findAllByString(new OrderSearch());
		for (Order order : all) {
			order.getMember().getName(); //Lazy 강제 초기화
			order.getDelivery().getAddress();
		}
		return all;
	}

	@GetMapping("/api/v2/simple-orders")
	public List<SimpleOrderDto> ordersV2() {
		return orderRepository.findAllByString(new OrderSearch()).stream()
			.map(SimpleOrderDto::new)
			.collect(Collectors.toList());
	}

	// 장점
	// 리포지토리에서 엔티티를 조회하는 방법이라 재사용성이 높다
	// 코드가 짧다
	// 단점
	// DB에서 불필요한 컬럼을 조회해야한다
	@GetMapping("/api/v3/simple-orders")
	public List<SimpleOrderDto> ordersV3() {
		return orderRepository.findAllWithMemberDelivery().stream()
			.map(SimpleOrderDto::new)
			.collect(Collectors.toList());
	}

	// 장점
	// API 맞춰서 DB 조회 성능을 최적화할 수 있다
	// 단점
	// API에 맞춰져 있어서 재사용성이 떨어진다
	// select절에서 컬럼 몇 개 더 들어간다고 해서 성능이 크게 차이 나지 않는다
	// 코드가 길어진다
	// 결론 V4
	@GetMapping("/api/v4/simple-orders")
	public List<OrderSimpleQueryDto> ordersV4() {
		return orderSimpleQueryRepository.findOrderDtos();
	}

	@Data
	static public class SimpleOrderDto {
		private Long orderId;
		private String name;
		private LocalDateTime orderDate;
		private OrderStatus orderStatus;
		private Address address;

		public SimpleOrderDto(Order order) {
			orderId = order.getId();
			name = order.getMember().getName(); // LAZY 초기화
			orderDate = order.getOrderDate();
			orderStatus = order.getStatus();
			address = order.getDelivery().getAddress(); // LAZY 초기화
		}
	}
}
