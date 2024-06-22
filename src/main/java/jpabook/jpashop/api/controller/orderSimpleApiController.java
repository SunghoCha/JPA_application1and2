package jpabook.jpashop.api.controller;

import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/*
    xToOne(ManyToOne, OneToOne) CASE
    Order
    Order -> Member
    Order -> Delivery
 */
@RestController
@RequiredArgsConstructor
public class orderSimpleApiController {

    private final OrderRepository orderRepository;

    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1() {
        OrderSearch orderSearch = new OrderSearch();
        orderSearch.setMemberName("userA");
        orderSearch.setOrderStatus(OrderStatus.ORDER);
        List<Order> all = orderRepository.findAll(orderSearch);
        return all;
    }

    @GetMapping("/api/v1/simple-orders2")
    public List<Order> ordersV1_2() {
        OrderSearch orderSearch = new OrderSearch();
        List<Order> all = orderRepository.findAll2(orderSearch);
        for (Order order : all) {
            //order.getMember() 여기까진 프록시로 대체하고 실제로 member 쿼리 조회 안함
            order.getMember().getName(); // 실제 name 정보가 필요하므로 member 조회 쿼리 날림
            order.getDelivery().getAddress();
        }
        return all;
    }
}
