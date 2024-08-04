package jpabook.jpashop.api.controller;

import jpabook.jpashop.api.dto.OrderSimpleQueryDto;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.repository.order.OrderRepository;
import jpabook.jpashop.repository.order.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static java.util.stream.Collectors.*;

/*
    xToOne(ManyToOne, OneToOne) CASE
    Order
    Order -> Member
    Order -> Delivery
 */
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;

    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        for (Order order : all) {
            //order.getMember() 여기까진 프록시로 대체하고 실제로 member 쿼리 조회 안함
            order.getMember().getName(); // 실제 name 정보가 필요하므로 member 조회 쿼리 날림
            order.getDelivery().getAddress();
        }
        return all;
    }

    @GetMapping("/api/v2/simple-orders")
    public List<OrderSimpleQueryDto> ordersV2() {
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());
        return orders.stream()
                .map(OrderSimpleQueryDto::new)
                .collect(toList());
    }

    @GetMapping("/api/v3/simple-orders")
    public List<OrderSimpleQueryDto> ordersV3() {
        List<Order> orders = orderRepository.findAllWithMemberDelivery(); // v2와 다른 쿼리
        return orders.stream()
                .map(OrderSimpleQueryDto::new)
                .collect(toList());
    }

    @GetMapping("/api/v4/simple-orders")
    public List<OrderSimpleQueryDto> ordersV4() {
        return orderRepository.findOrderDtos();
        /*
            쿼리자체는 fetch조인에 비해 적게나가지만 성능상 이점이 그렇게 크지 않음
            (조인문에 들어가는 테이블 개수가 영향을 주며, select절에 컬럼 몇 개 차이는 성능에 영향 미비).
            fit하게 데이터를 가져온만큼 유연성이 떨어짐.
            API 스펙에 맞춘코드가 Repository까지 들어가는 단점 -> 물리적으로는 계층이 나눠져 있지만 논리적으로는 계층이 결합된 상태
            (API 스펙이 "변경"되면 Repository의 코드도 같이 "변경"되는 상황 발생)
            *** 조회전용으로 화면와 깊게 연관되어 있고 복잡한 쿼리가 필요한 경우(ex 통계) 전용 repository를 따로 만들어서 놓는 것이 좋음
         */
    }
}
