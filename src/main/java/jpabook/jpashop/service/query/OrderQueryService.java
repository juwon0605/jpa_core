package jpabook.jpashop.service.query;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jpabook.jpashop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderQueryService {

	private final OrderRepository orderRepository;

	public List<OrderDto> ordersV3() {
		return orderRepository.findAllWithItem().stream()
			.map(OrderDto::new)
			.collect(Collectors.toList());
	}
}
