package jpabook.jpashop.repository.order;

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

    // 재사용성 떨어짐. 성능최적화지만 미비한수준. repository가 view에 의존하는것과 마찬가지. api 스펙바뀌면 같이 바뀜. dto에 의존하게 하지말고 entity 수준에서 해결하자
    public List<OrderSimpleQueryDto> findOrderDtos() {
        return em.createQuery( // new OrderSimpleQueryDto() 파라미터로 order 전달하면 orderId가 전달되어서 따로 써줘야함. d.address는 값타입이라 제대로 인식가능
                "select new jpabook.jpashop.api.dto.OrderSimpleQueryDto(o.id, m.name, o.orderDate, o.status, d.address) from Order o" +
                        " join o.member m" +
                        " join o.delivery d", OrderSimpleQueryDto.class) // 기본적으로 JPA는 ENTITY나 VO만 반환가능하기 때문에 직접 설정 필요..
                .getResultList();

        /*   쿼리 방식 선택 권장 순서
             1. 우선 엔티티를 조회 후 DTO로 변환하는 방법을 선택
             2. 필요하면 페치 조인으로 성능 최적화 (대부분의 성능 이슈 해결됨)
             3. 그래도 안되면 DTO로 직접 조회하는 방법 선택 (별도의 전용 Repository 생성)
             4. 최후의 방법은 JPA가 제공하는 네이티브 SQL이나 스프링 JDBC Template를 사용해서 SQL을 직접 사용한다.
         */
    }

    // JPA의 distinct : order(root =(from "entity"))의 Id가 같으면 중복 제거해줌 (DB의 distinct는 row 데이터 전체가 일치해야만 적용됨)
    // DB에서 관련 데이터를 전부 가져와 메모리에 로딩한다음에 order(root)를 기준으로 distinct 적용.
    // 페이징 시 위험할 수 있음.
    // 일대다 관계에서 '다'에 맞춰서 데이터 늘어나서 원하는 페이징 어려움. 일단 메모리에 전체 데이터 불러와서 페이징처리 -> out of memory
    // 그런데 페이징 전에도 어차피 전체 데이터 가져와서 메모리에 로딩하는건 똑같은거 아닌가?


    public List<Order> findAllWithItem() {
        return em.createQuery(
                "select distinct o from Order o" +
                        " join fetch o.member m" +
                        " join fetch o.delivery d" +
                        " join fetch o.orderItems oi" +
                        " join fetch oi.item i", Order.class)
                .getResultList();
    }

    // --> ToOne 관계는 fetch join (row 수를 증가시키지 않으므로 페이징 쿼리에 영향 x)
    // 컬렉션은 지연로딩 + batchSize 조절로 N+1 문제 예방 ceil(N/BatchSize) + 1 로 바뀜
    // 배치사이즈는 100~1000 사이. 애플리케이션 입장에서 결국 전체 데이터를 로딩해야 하므로 메모리사용량은 같으니 순간 부하가 다름
    // WAS와 DB가 버틸수 있는 정도 내에서 최대로 선택. 부하가 걸릴 수 있지만 쿼리수를 줄이는 트레이드오프?
    public List<Order> findAllWithMemberDelivery(int offset, int limit) {
        return em.createQuery(
                        "select o from Order o " +
                                " join fetch o.member m " +
                                " join fetch o.delivery d ", Order.class)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }
}
