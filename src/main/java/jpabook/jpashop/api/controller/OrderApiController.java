package jpabook.jpashop.api.controller;

import jpabook.jpashop.api.dto.OrderDto;
import jpabook.jpashop.repository.order.query.OrderQueryDto;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.repository.order.OrderRepository;
import jpabook.jpashop.repository.order.OrderSearch;
import jpabook.jpashop.repository.order.query.OrderQueryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static java.util.stream.Collectors.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderRepository orderRepository;
    private final OrderQueryRepository orderQueryRepository;

    @GetMapping("/api/v1/orders")
    public List<Order> orderV1() {
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());
        for (Order order : orders) {
            order.getMember().getName();
            order.getDelivery().getAddress();
            List<OrderItem> orderItems = order.getOrderItems();
            orderItems.stream().forEach(orderItem -> orderItem.getItem().getName());
        }
        return orders;
        /*
            엔티티를 직접 노출하는 방식이므로 사용하지 말 것. 벌써부터 엔티티에 순환참조 막으려고 @JsonIgnore 사용해야하고 지저분해짐
         */
    }

    @GetMapping("/api/v2/orders")
    public List<OrderDto> orderV2() {
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());
        return orders.stream()
                .map(OrderDto::of)
                .collect(toList());
    }

    @GetMapping("/api/v3/orders")
    public List<OrderDto> ordersV3() {
        List<Order> orders = orderRepository.findAllWithItem();
        log.info("orders size : {}", orders.size());
        for (Order order : orders) {
            log.info("order : {}, id : {}" ,order, order.getId());
        }
        return orders.stream().map(OrderDto::of).collect(toList());
    }

    @GetMapping("/api/v3.1/orders")
    public List<OrderDto> ordersV3_page(
            @RequestParam(name = "offset", defaultValue = "0") int offset,
            @RequestParam(name = "limit", defaultValue = "0") int limit
    ) {
        List<Order> orders = orderRepository.findAllWithMemberDelivery(offset, limit);

        return orders.stream().map(OrderDto::of).collect(toList());
    }

    @GetMapping("/api/v4/orders")
    public List<OrderQueryDto> ordersV4() {
        return orderQueryRepository.findOrderQueryDtos();
    }
}
