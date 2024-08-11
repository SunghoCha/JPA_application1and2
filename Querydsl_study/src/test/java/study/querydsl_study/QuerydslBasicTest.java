package study.querydsl_study;

import com.querydsl.core.QueryResults;
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
import study.querydsl_study.entity.QTeam;
import study.querydsl_study.entity.Team;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static study.querydsl_study.entity.QMember.*;
import static study.querydsl_study.entity.QTeam.*;

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
                .select(member)
                .from(member)
                .where(member.userName.eq("member1"))
                .fetchOne();

        // then
        assertThat(findMember.getUserName()).isEqualTo("member1");
    }

    @Test
    @DisplayName("검색 조건 쿼리")
    void search() {
        // given
        // when
        Member findMember = queryFactory
                .selectFrom(member)
                .where(member.userName.eq("member1")
                        .and(member.age.eq(10)))
                .fetchOne();

        // then
        assertThat(findMember.getUserName()).isEqualTo("member1");
        assertThat(findMember.getAge()).isEqualTo(10);
    }

    @Test
    @DisplayName("검색 조건 쿼리 and 대신 ',' 사용가능")
    void searchAndParam() {
        // given
        // when
        Member findMember = queryFactory
                .selectFrom(member)
                .where(
                        member.userName.eq("member1"),
                        member.age.eq(10)
                )
                .fetchOne();

        // then
        assertThat(findMember.getUserName()).isEqualTo("member1");
        assertThat(findMember.getAge()).isEqualTo(10);
    }

    @Test
    @DisplayName("fetch 테스트")
    void resultFetch() {
        // given
        List<Member> fetch = queryFactory
                .selectFrom(member)
                .fetch();

        Member fetchOne = queryFactory
                .selectFrom(member)
                .fetchOne();

        Member fetchFirst = queryFactory
                .selectFrom(member)
                .fetchFirst();

        // 추천되지 않는방법. 성능이슈, 서브쿼리에서 사용 어려움 등..
        QueryResults<Member> results = queryFactory
                .selectFrom(member)
                .fetchResults();

        // 추천되지 않는방법. 성능이슈, 서브쿼리에서 사용 어려움 등..
        long count = queryFactory
                .selectFrom(member)
                .fetchCount();
        // when

        // then
    }

    /*
        회원 정렬 순서
        1. 회원 나이 내림차순(desc)
        2. 회원 이름 올림차순(asc)
        3. 만약 2에서 회원 이름이 없으면 마지막에 출력(nulls last)
     */

    @Test
    @DisplayName("sort 테스트")
    void sort() {
        // given
        em.persist(createMember(null, 101, null));
        em.persist(createMember("member5", 101, null));
        em.persist(createMember("member6", 102, null));
        em.persist(createMember("member7", 102, null));
        // when
        List<Member> result = queryFactory
                .selectFrom(member)
                .where(member.age.goe(100))
                .orderBy(member.age.desc(), member.userName.asc().nullsLast())
                .fetch();

        // then
        assertThat(result).hasSize(4)
                .extracting(Member::getAge, Member::getUserName)
                .containsExactly(
                        Tuple.tuple(102, "member6"),
                        Tuple.tuple(102, "member7"),
                        Tuple.tuple(101, "member5"),
                        Tuple.tuple(101, null)
                );
    }

    @Test
    @DisplayName("paging 테스트")
    void paging() {
        // given
        // when
        List<Member> members = queryFactory
                .selectFrom(member)
                .orderBy(member.userName.desc())
                .offset(1)
                .limit(2)
                .fetch();

        // then
        assertThat(members).hasSize(2)
                .extracting(Member::getUserName, Member::getAge)
                .containsExactlyInAnyOrder(
                        Tuple.tuple("member2", 20),
                        Tuple.tuple("member3", 30)
                );
    }

    @Test
    @DisplayName("집계 함수 테스트")
    void aggregation() {
        // given
        // when
        List<com.querydsl.core.Tuple> result = queryFactory
                .select(
                        member.count(),
                        member.age.sum(),
                        member.age.avg(),
                        member.age.max(),
                        member.age.min()
                )
                .from(member)
                .fetch();

        com.querydsl.core.Tuple tuple = result.get(0);

        // then
        assertThat(tuple.get(member.count())).isEqualTo(4);
        assertThat(tuple.get(member.age.sum())).isEqualTo(100);
        assertThat(tuple.get(member.age.avg())).isEqualTo(25);
        assertThat(tuple.get(member.age.max())).isEqualTo(40);
        assertThat(tuple.get(member.age.min())).isEqualTo(10);
    }

    /*
        팀의 이름과 각 팀의 평균 연령 구하기
     */
    @Test
    @DisplayName("그룹 함수")
    void group() {
        // given
        // when
        List<com.querydsl.core.Tuple> result = queryFactory
                .select(team.name, member.age.avg())
                .from(member)
                .join(member.team, team)
                .groupBy(team.name)
                .fetch();

        com.querydsl.core.Tuple teamA = result.get(0);
        com.querydsl.core.Tuple teamB = result.get(1);

        // then
        assertThat(teamA.get(team.name)).isEqualTo("teamA");
        assertThat(teamA.get(member.age.avg())).isEqualTo(15);

        assertThat(teamB.get(team.name)).isEqualTo("teamB");
        assertThat(teamB.get(member.age.avg())).isEqualTo(35);
    }
    private static Member createMember(String name, int age, Team team) {
        return Member.builder()
                .userName(name)
                .age(age)
                .team(team)
                .build();
    }
}
