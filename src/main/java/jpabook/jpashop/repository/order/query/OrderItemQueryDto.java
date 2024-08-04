package jpabook.jpashop.repository.order.query;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class OrderItemQueryDto {

    @JsonIgnore // json에 id값 전달하고 싶지 않을떄 (entity아니고 dto이므로 이런식으로 막는것도 나쁘지 않음)
    private Long orderId;
    private String itemName;
    private int orderPrice;
    private int count;

    public OrderItemQueryDto(Long orderId, String itemName, int orderPrice, int count) {
        this.orderId = orderId;
        this.itemName = itemName;
        this.orderPrice = orderPrice;
        this.count = count;
    }
}

