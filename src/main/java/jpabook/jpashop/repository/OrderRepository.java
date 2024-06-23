package jpabook.jpashop.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jpabook.jpashop.api.dto.OrderSimpleQueryDto;
import jpabook.jpashop.domain.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepository {

    private final EntityManager em;

    public void save(Order order) {
        em.persist(order);
    }

    public Order findOne(Long id) {
        return em.find(Order.class, id);
    }

    public List<Order> findAll(OrderSearch orderSearch) {
        return em.createQuery("select o from Order o join o.member m" +
                        " where o.status = :status " +
                        " and m.name like :name", Order.class)
                        .setParameter("status", orderSearch.getOrderStatus())
                        .setParameter("name", orderSearch.getMemberName())
                        .setMaxResults(1000) // 최대 1000건
                        .getResultList();
    }

    public List<Order> findAll2(OrderSearch orderSearch) {
        return em.createQuery("select o from Order o", Order.class)
                .getResultList();
    }

    // 이 쿼리는 복붙으로 가져옴... 차라리 MyBatis 쿼리가 나은듯
    public List<Order> findAllByString(OrderSearch orderSearch) {

        String jpql = "select o from Order o join o.member m";
        boolean isFirstCondition = true;

        //주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " o.status = :status";
        }

        //회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " m.name like :name";
        }

        TypedQuery<Order> query = em.createQuery(jpql, Order.class)
                .setMaxResults(1000);

        if (orderSearch.getOrderStatus() != null) {
            query = query.setParameter("status", orderSearch.getOrderStatus());
        }
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            query = query.setParameter("name", orderSearch.getMemberName());
        }

        return query.getResultList();
    }

    public List<Order> findAllWithMemberDelivery() {
        return em.createQuery(
                "select o from Order o " +
                        " join fetch o.member m " +
                        " join fetch o.delivery d ", Order.class // LAZY 무시하고 관련된 테이블 다 조인해서 한 번에 가져옴 (fetch join)
                        ).getResultList();
    }

    public List<OrderSimpleQueryDto> findOrderDtos() {
        return em.createQuery( // new OrderSimpleQueryDto() 파라미터로 order 전달하면 orderId가 전달되어서 따로 써줘야함. d.address는 값타입이라 제대로 인식가능
                "select new jpabook.jpashop.api.dto.OrderSimpleQueryDto(o.id, m.name, o.orderDate, o.status, d.address) from Order o" +
                        " join o.member m" +
                        " join o.delivery d", OrderSimpleQueryDto.class) // 기본적으로 JPA는 ENTITY나 VO만 반환가능하기 때문에 직접 설정 필요..
                .getResultList();
    }
}
