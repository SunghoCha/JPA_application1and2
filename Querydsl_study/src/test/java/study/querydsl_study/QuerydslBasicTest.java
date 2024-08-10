package study.querydsl_study;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl_study.entity.Member;
import study.querydsl_study.entity.QMember;
import study.querydsl_study.entity.Team;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class QuerydslBasicTest {

    @Autowired
    EntityManager em;

    JPAQueryFactory queryFactory;

    @BeforeEach
    public void setUp() {
        queryFactory = new JPAQueryFactory(em);

        Team teamA = Team.builder()
                .name("teamA")
                .build();

        Team teamB = Team.builder()
                .name("teamB")
                .build();

        em.persist(teamA);
        em.persist(teamB);

        Member member1 = createMember("member1", 10, teamA);
        Member member2 = createMember("member2", 20, teamA);
        Member member3 = createMember("member3", 30, teamB);
        Member member4 = createMember("member4", 40, teamB);

        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);

        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("JPQL 기본테스트")
    void startJPQL() {
        // given
        // when
        Member findMember = em.createQuery("select m from Member m where m.userName = :userName", Member.class)
                .setParameter("userName", "member1")
                .getSingleResult();

        // then
        assertThat(findMember.getUserName()).isEqualTo("member1");
    }

    @Test
    @DisplayName("Querydsl 테스트")
    void startQuerydsl() {
        // given
        QMember qMember = new QMember("member");

        // when
        Member findMember = queryFactory
                .select(qMember)
                .from(qMember)
                .where(qMember.userName.eq("member1"))
                .fetchOne();

        // then
        assertThat(findMember.getUserName()).isEqualTo("member1");
    }

    private static Member createMember(String name, int age, Team team) {
        return Member.builder()
                .userName(name)
                .age(age)
                .team(team)
                .build();
    }
}
