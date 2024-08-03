package jpabook.jpashop.api.dto;

import jpabook.jpashop.domain.OrderItem;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class OrderItemDto {

    private String itemName;
    private int orderPrice;
    private int count;

    @Builder
    public OrderItemDto(String itemName, int orderPrice, int count) {
        this.itemName = itemName;
        this.orderPrice = orderPrice;
        this.count = count;
    }

    public static OrderItemDto of(OrderItem orderItem) {
        return OrderItemDto.builder()
                .itemName(orderItem.getItem().getName())
                .orderPrice(orderItem.getOrderPrice())
                .count(orderItem.getCount())
                .build();
    }
}
