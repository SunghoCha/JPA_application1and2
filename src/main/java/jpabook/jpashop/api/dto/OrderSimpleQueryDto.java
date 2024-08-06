package jpabook.jpashop.api.dto;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Slf4j
@Data
public class OrderSimpleQueryDto {
    private Long orderId;
    private String name;
    private LocalDateTime orderDate;
    private OrderStatus orderStatus;
    private Address address;

    public OrderSimpleQueryDto(Long orderId, String name, LocalDateTime orderDate, OrderStatus orderStatus, Address address) {
        this.orderId = orderId;
        this.name = name;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
        this.address = address;
    }
    public OrderSimpleQueryDto(Order order) { // 좋은 코드는 아닌데 학습이 목적이라 다양한 케이스 테스트 해봐야하므로 놔둠
        orderId = order.getId();
        name = order.getMember().getName(); // LAZY 초기화 (memberId를 가지고 영속성컨텍스트에서 해당 데이터 찾기 시도 후 없으면 DB에 쿼리 날림
        orderDate = order.getOrderDate();
        orderStatus = order.getStatus();
        address = order.getDelivery().getAddress();
        /*
           order들을 조회하는 findAll 쿼리 1번 +  order 개수인 2 * LAZY 초기화가 2번씩 4번 = 5번의 쿼리 -> 1 + N 문제
           1번의 쿼리에 더해 N번의 쿼리가 추가로 실행됨 (1 + 회원 N + 배송 N)
         */
    }
}
