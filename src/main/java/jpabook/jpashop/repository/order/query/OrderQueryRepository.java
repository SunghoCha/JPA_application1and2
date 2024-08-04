package jpabook.jpashop.repository.order.query;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {

    private final EntityManager em;

    public List<OrderQueryDto> findOrderQueryDtos() {
        List<OrderQueryDto> result = findOrders();
        result.forEach(o -> {
            List<OrderItemQueryDto> orderItems = findOrderItems(o.getOrderId());
            o.setOrderItems(orderItems);
        });
        return result;
    }

    // oi.order.id로 표기했지만 실제 테이블에선 oi.order에서 order_id 찾을 수 있으므로 order 참조하지않고 id값 가져올 수 있음
    private List<OrderItemQueryDto> findOrderItems(Long orderId) {
        return em.createQuery(
                        "select new jpabook.jpashop.repository.order.query.OrderItemQueryDto(oi.order.id ,i.name, oi.orderPrice, oi.count)" +
                                " from OrderItem oi" +
                                " join oi.item i " +
                                " where oi.order.id = :orderId", OrderItemQueryDto.class)
                .setParameter("orderId", orderId)
                .getResultList();
    }

    private List<OrderQueryDto> findOrders() {
        // select 절에 데이터를 flat하게 넣을 수 밖에 없음. OrderItemQuertDto하고는 일대다 관계라서 데이터가 뻥튀기되므로 생성자에 바로 못 넣어줌
        return em.createQuery(
                "select new jpabook.jpashop.repository.order.query.OrderQueryDto(o.id, m.name, o.orderDate, o.status, d.address) " +
                        " from Order o" +
                        " join o.member m" +
                        " join o.delivery d", OrderQueryDto.class
        ).getResultList();
    }
}
