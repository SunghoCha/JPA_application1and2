package jpabook.jpashop.api.dto;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Getter @Setter
@NoArgsConstructor
public class OrderDto {

    private Long orderId;
    private String name;
    private LocalDateTime orderDate;
    private OrderStatus orderStatus;
    private Address address;
    private List<OrderItemDto> orderItems;

    @Builder
    public OrderDto(Long orderId, String name, LocalDateTime orderDate, OrderStatus orderStatus, Address address, List<OrderItemDto> orderItems) {
        this.orderId = orderId;
        this.name = name;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
        this.address = address;
        this.orderItems = orderItems;
    }

    public static OrderDto of(Order order) {
        return OrderDto.builder()
                .orderId(order.getId())
                .name(order.getMember().getName())
                .orderDate(order.getOrderDate())
                .orderStatus(order.getStatus())
                .address(order.getDelivery().getAddress())
                .orderItems(order.getOrderItems().stream()
                        .map(OrderItemDto::of)
                        .collect(Collectors.toList()))
                .build();
    }
}
