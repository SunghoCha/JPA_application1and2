package jpabook.jpashop;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jpabook.jpashop.domain.*;
import jpabook.jpashop.domain.item.Book;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class InitDb {

    private final InitService initService;

    @PostConstruct
    public void init() { // 애플리케이션 로딩시점에 생성된 후 실행됨
        initService.dbInit1();
        initService.dbInit2();
    }

    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitService {

        private final EntityManager em;

        public void dbInit1() {
            log.info("==== dbInit1 start ====");
            log.info("==== Init1 : {} ====", this.getClass());
            Member member1 = createMember("userA", "서울", "1", "1111");
            em.persist(member1);

            Book book1 = createBook("JPA1 BOOK", 10000, 100);
            em.persist(book1);

            Book book2 = createBook("JPA2 BOOK", 20000, 200);
            em.persist(book2);

            OrderItem orderItem1 = OrderItem.createOrderItem(book1, 10000, 1);
            OrderItem orderItem2 = OrderItem.createOrderItem(book2, 20000, 2);

            Delivery delivery = new Delivery();
            delivery.setAddress(member1.getAddress());
            Order order = Order.createOrder(member1, delivery, orderItem1, orderItem2);
            em.persist(order);
        }

        public void dbInit2() {
            log.info("==== dbInit2 start ====");
            log.info("==== Init2 : {} ====", this.getClass());

            Member member2 = createMember("userB", "인천", "2", "2222");
            em.persist(member2);

            Book book1 = createBook("SPRING1 BOOK1", 30000, 300);
            em.persist(book1);

            Book book2 = createBook("SPRING2 BOOK", 40000, 400);
            em.persist(book2);

            OrderItem orderItem1 = OrderItem.createOrderItem(book1, 30000, 3);
            OrderItem orderItem2 = OrderItem.createOrderItem(book2, 40000, 4);

            Delivery delivery = new Delivery();
            delivery.setAddress(member2.getAddress());
            Order order = Order.createOrder(member2, delivery, orderItem1, orderItem2);
            em.persist(order);
        }

        private static Member createMember(String name, String city, String street, String zipcode) {
            Member member = new Member();
            member.setName(name);
            member.setAddress(new Address(city, street, zipcode));
            return member;
        }

        private static Book createBook(String name, int price, int stockQuantity) {
            Book book = new Book();
            book.setName(name);
            book.setPrice(price);
            book.setStockQuantity(stockQuantity);
            return book;
        }
    }
}


