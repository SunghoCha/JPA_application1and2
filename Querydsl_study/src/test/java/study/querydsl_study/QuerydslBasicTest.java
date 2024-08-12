package study.querydsl_study;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnit;
import org.assertj.core.groups.Tuple;
import org.hibernate.dialect.TiDBDialect;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl_study.dto.MemberDto;
import study.querydsl_study.dto.QMemberDto;
import study.querydsl_study.dto.UserDto;
import study.querydsl_study.entity.Member;
import study.querydsl_study.entity.QMember;
import study.querydsl_study.entity.QTeam;
import study.querydsl_study.entity.Team;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
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
//        List<Member> fetch = queryFactory
//                .selectFrom(member)
//                .fetch();

//        Member fetchOne = queryFactory
//                .selectFrom(member)
//                .fetchOne();

//        Member fetchFirst = queryFactory
//                .selectFrom(member)
//                .fetchFirst();

        // 추천되지 않는방법. 성능이슈, 서브쿼리에서 사용 어려움 등..
//        QueryResults<Member> results = queryFactory
//                .selectFrom(member)
//                .fetchResults();

        // 추천되지 않는방법. 성능이슈, 서브쿼리에서 사용 어려움 등..
//        long count = queryFactory
//                .selectFrom(member)
//                .fetchCount();
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

    /*
        팀 A에 소속된 모든 회원
     */
    @Test
    @DisplayName("join 테스트")
    void join() {
        // given

        // when
        List<Member> result = queryFactory
                .selectFrom(member)
                .join(member.team, team)
                .where(team.name.eq("teamA"))
                .fetch();

        // then
        assertThat(result).hasSize(2)
                .extracting(Member::getUserName, Member::getAge)
                .containsExactlyInAnyOrder(
                        Tuple.tuple("member1", 10),
                        Tuple.tuple("member2", 20)
                );
    }

    /*
        세타 조인
        회원의 이름이 팀 이름과 같은 회원 조회
     */
    @Test
    @DisplayName("세타 조인(카르테시안 곱) 테스트(이 방식은 outer join 불가능)")
    void theta_join() {
        // given
        em.persist(createMember("teamA", 10, null));
        em.persist(createMember("teamB", 20, null));
        // when
        List<Member> result = queryFactory
                .select(member)
                .from(member, team)
                .where(member.userName.eq(team.name))
                .fetch();
        // then
        assertThat(result).hasSize(2)
                .extracting(Member::getUserName, Member::getAge)
                .containsExactlyInAnyOrder(
                        Tuple.tuple("teamA", 10),
                        Tuple.tuple("teamB", 20)
                );

        assertThat(result)
                .extracting("userName")
                .containsExactly("teamA", "teamB");
    }

    /*
        회원과 팀을 조인하면서 팀 이름이 teamA인 팀만 조인, 회원은 모두 조회
        JPQL: select m, t from Member m left join Team t on t.name = 'teamA'
     */
    @Test
    @DisplayName("join-on 테스트")
    void join_on_filtering() {
        // 지금처럼 외부조인아니고 내부조인이면 굳이 on안쓰고 where로 해결
        List<com.querydsl.core.Tuple> result = queryFactory
                .select(member, team)
                .from(member)
                .join(member.team, team).on(team.name.eq("teamA"))
                .fetch();
        // when

        // then
        for (com.querydsl.core.Tuple tuple : result) {
            System.out.println("tuple = " + tuple);
        }
    }

    /*
        연관관계가 없는 엔티티 외부 조인
        회원의 이름이 팀 이름과 같은 회원 외부 조인
    */
    @Test
    @DisplayName("on 외부조인")
    void join_on_no_relation() {
        // given
        em.persist(createMember("teamA", 10, null));
        em.persist(createMember("teamB", 20, null));
        // when
        List<com.querydsl.core.Tuple> result = queryFactory
                .select(member, team)
                .from(member)
                .leftJoin(team).on(member.userName.eq(team.name))
                .fetch();
        // then
        for (com.querydsl.core.Tuple tuple : result) {
            System.out.println("tuple = " + tuple);
        }
    }

    @PersistenceUnit
    EntityManagerFactory emf;

    @Test
    @DisplayName("페치조인 없는 버전")
    void fetchJoinNo() {
        // given
        em.flush();
        em.clear();

        // when
        Member findMember = queryFactory
                .selectFrom(member)
                .where(member.userName.eq("member1"))
                .fetchOne();

        boolean isLoaded = emf.getPersistenceUnitUtil().isLoaded(findMember.getTeam());

        // then
        assertThat(isLoaded).as("패치 조인 미적용").isFalse();
    }

    @Test
    @DisplayName("페치조인 없는 버전")
    void fetchJoin() {
        // given
        em.flush();
        em.clear();

        // when
        Member findMember = queryFactory
                .selectFrom(member)
                .join(member.team, team).fetchJoin()
                .where(member.userName.eq("member1"))
                .fetchOne();

        boolean isLoaded = emf.getPersistenceUnitUtil().isLoaded(findMember.getTeam());

        // then
        assertThat(isLoaded).as("패치 조인 적용").isTrue();
    }

    /*
        나이가 가장 많은 회원 조회
     */
    @Test
    @DisplayName("서브쿼리(최대값) 테스트")
    void subQuery_aboutMax() {
        // given
        QMember memberSub = new QMember("memberSub");

        // when
        List<Member> result = queryFactory
                .selectFrom(member)
                .where(member.age.eq(
                        JPAExpressions
                                .select(memberSub.age.max())
                                .from(memberSub)
                ))
                .fetch();

        // then
        assertThat(result).extracting("age")
                .containsExactly(40);
    }

    /*
       나이가 가장 평균 이상인 회원 조회
    */
    @Test
    @DisplayName("서브쿼리(평균) 테스트")
    void subQuery_aboutAvg() {
        // given
        QMember memberSub = new QMember("memberSub");

        // when
        List<Member> result = queryFactory
                .selectFrom(member)
                .where(member.age.goe(
                        JPAExpressions
                                .select(memberSub.age.avg())
                                .from(memberSub)
                ))
                .fetch();

        // then
        assertThat(result).hasSize(2)
                .extracting("age")
                .containsExactly(30, 40);
    }

    /*
   나이가 가장 평균 이상인 회원 조회
*/
    @Test
    @DisplayName("서브쿼리(In query) 테스트")
    void subQuery_aboutIn_query() {
        // given
        QMember memberSub = new QMember("memberSub");

        // when
        List<Member> result = queryFactory
                .selectFrom(member)
                .where(member.age.in(
                        JPAExpressions
                                .select(memberSub.age)
                                .from(memberSub)
                                .where(memberSub.age.gt(10))
                ))
                .fetch();

        // then
        assertThat(result).hasSize(3)
                .extracting("age")
                .containsExactly(20, 30, 40);
    }

    @Test
    @DisplayName("스칼라 서브쿼리 테스트")
    void selectSubQuery() {
        // given
        QMember memberSub = new QMember("memberSub");

        // when
        List<com.querydsl.core.Tuple> result = queryFactory
                .select(member.userName,
                        JPAExpressions
                                .select(memberSub.age.avg())
                                .from(memberSub))
                .from(member)
                .fetch();

        // then
        for (com.querydsl.core.Tuple tuple : result) {
            System.out.println("tuple = " + tuple);
        }
    }
    
    /*
        < from 절의 서브쿼리 한계 >
        JPA JPQL 서브쿼리의 한계점으로 from절의 서브쿼리(인라인 뷰)는 지원하지 않는다.
        당연히 Querydsl도 지원하지 않음. 하이버네이트 구현체를 사용하면 select절의 서브쿼리는 지원함.
        (Querydsl도 하이버네이트 구현체 사용하면 select 절의 서브쿼리 지원)
        
        < from절 의 서브쿼리 한계 해결방안>
        1. 서브쿼리를 join으로 변경(불가능한 경우도 있음)
        2. 애플리케이션에서 쿼리를 분리해서 2번 실행 (성능이 엄청 중요하지 않은 경우에 한정)
        3. nativeSQL 사용
        
        **
        보통 인라인 뷰의 필요성은 쿼리에서 모든걸 해결하려고 시도하면서 생기는 경우가 많음
        되도록 DB는 데이터를 퍼올리는 용도로만 사용하자
     */



    /*
        Case문으로 DB에서 처리하지말고 데이터를 가져와서 애플리케이션에서 처리하자
     */
    @Test
    @DisplayName("Case Basic 테스트")
    void basicCase() {
        // given

        // when
        List<String> result = queryFactory
                .select(member.age
                        .when(10).then("열살")
                        .when(20).then("스무살")
                        .otherwise("기타"))
                .from(member)
                .fetch();
        // then
        for (String string : result) {
            System.out.println("string = " + string);
        }
    }

    @Test
    @DisplayName("Case Complex 테스트")
    void complexCase() {
        // given

        // when
        List<com.querydsl.core.Tuple> result = queryFactory
                .select(member.age, new CaseBuilder()
                        .when(member.age.between(0, 20)).then("0 ~ 20살")
                        .when(member.age.between(21, 30)).then("21 ~ 30삶")
                        .otherwise("기타"))
                .from(member)
                .fetch();

        // then
        for (com.querydsl.core.Tuple tuple : result) {
            System.out.println("tuple = " + tuple);
        }
    }

    @Test
    @DisplayName("상수 테스트")
    void constant() {
        // given

        // when
        List<com.querydsl.core.Tuple> result = queryFactory
                .select(member.userName, Expressions.constant("CONSTANT"))
                .from(member)
                .fetch();
        // then
        for (com.querydsl.core.Tuple tuple : result) {
            System.out.println("tuple = " + tuple);
        }
    }

    @Test
    @DisplayName("concat 테스트")
    void concat() {
        // given

        // when
        List<String> result = queryFactory
                .select(member.userName.concat("_").concat(member.age.stringValue()))
                .from(member)
                .where(member.userName.eq("member1"))
                .fetch();
        // then
        for (String s : result) {
            System.out.println("s = " + s);

        }
    }

    @Test
    @DisplayName("simple Projection 테스트")
    void simpleProjection() {
        // given

        // when
        List<String> result = queryFactory
                .select(member.userName)
                .from(member)
                .fetch();

        // then
        for (String s : result) {
            System.out.println("s = " + s);
        }
    }

    @Test
    @DisplayName("tuple Projection 테스트")
    void tupleProjection() {
        // given

        // when
        List<com.querydsl.core.Tuple> result = queryFactory
                .select(member.userName, member.age)
                .from(member)
                .fetch();

        // then
        for (com.querydsl.core.Tuple tuple : result) {

            String userName = tuple.get(member.userName);
            Integer age = tuple.get(member.age);

            System.out.println("userName = " + userName);
            System.out.println("age = " + age);

        }
    }

    @Test
    @DisplayName("JPQL로 DTO조회 테스트")
    void findDtoByJPQL() {
        // given

        // when
        List<MemberDto> result = em.createQuery("select new study.querydsl_study.dto.MemberDto(m.userName, m.age) from Member m", MemberDto.class)
                .getResultList();

        // then
        for (MemberDto memberDto : result) {
            System.out.println("memberDto = " + memberDto);
        }
    }

    @Test
    @DisplayName("Querydsl Setter로 Dto 조회하기")
    void findDtoBySetter() {
        // given

        // when
        List<MemberDto> result = queryFactory
                .select(Projections.bean(MemberDto.class,
                        member.userName,
                        member.age))
                .from(member)
                .fetch();

        // then
        for (MemberDto memberDto : result) {
            System.out.println("memberDto = " + memberDto);
        }
    }

    @Test
    @DisplayName("Querydsl Field로 Dto 조회하기")
    void findDtoByField() {
        // given

        // when
        List<MemberDto> result = queryFactory
                .select(Projections.fields(MemberDto.class,
                        member.userName,
                        member.age))
                .from(member)
                .fetch();

        // then
        for (MemberDto memberDto : result) {
            System.out.println("memberDto = " + memberDto);
        }
    }

    @Test
    @DisplayName("Querydsl 생성자로 Dto 조회하기")
    void findDtoByConstructor() {
        // given

        // when
        List<MemberDto> result = queryFactory
                .select(Projections.constructor(MemberDto.class,
                        member.userName,
                        member.age))
                .from(member)
                .fetch();

        // then
        for (MemberDto memberDto : result) {
            System.out.println("memberDto = " + memberDto);
        }
    }

    @Test
    @DisplayName("Querydsl 별칭 + Field로 CustomDto 조회하기")
    void findCustomDtoByFieldAndAlias() {
        // given
        QMember memberSub = new QMember("memberSub");
        // when
        List<UserDto> result = queryFactory
                .select(Projections.fields(UserDto.class,
                        member.userName.as("name"),
                        ExpressionUtils.as(JPAExpressions
                                .select(memberSub.age.max())
                                .from(memberSub), "age")))
                .from(member)
                .fetch();

        // then
        for (UserDto userDto : result) {
            System.out.println("userDto = " + userDto);
        }
    }

    @Test
    @DisplayName("Querydsl 별칭 + Constructor로 CustomDto 조회하기")
    void findCustomDtoByConstructorAndAlias() {
        // given
        QMember memberSub = new QMember("memberSub");
        // when
        List<UserDto> result = queryFactory
                .select(Projections.constructor(UserDto.class,
                        member.userName,
                        JPAExpressions
                                .select(memberSub.age.max())
                                .from(memberSub)))
                .from(member)
                .fetch();

        // then
        for (UserDto userDto : result) {
            System.out.println("userDto = " + userDto);
        }
    }

    // Projection + constructor 는 인자를 잘못전달해도 컴파일단계에서 에러를 잡지못하고 런타임에서 에러 발생
    // @QueryProjection은 컴파일 단계에서 인자 검증가능
    // 단점은 애플리케이션의 넓은 영역에서 사용되는 dto가 Querydsl에 의존적. 하부 기술이 바뀔 경우 같이 변경되어야함..
    @Test
    @DisplayName("QueryProjection으로 Dto 조회하기")
    void findDtoByQueryProjection() {
        // given

        // when
        List<MemberDto> result = queryFactory
                .select(new QMemberDto(member.userName, member.age))
                .from(member)
                .fetch();

        // then
        for (MemberDto memberDto : result) {
            System.out.println("memberDto = " + memberDto);
        }

    }
    private static Member createMember(String name, int age, Team team) {
        return Member.builder()
                .userName(name)
                .age(age)
                .team(team)
                .build();
    }
}
