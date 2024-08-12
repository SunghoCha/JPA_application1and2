package study.querydsl_study.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl_study.dto.MemberSearchCond;
import study.querydsl_study.dto.MemberTeamDto;
import study.querydsl_study.entity.Member;
import study.querydsl_study.entity.Team;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@SpringBootTest
@Transactional
class MemberRepositoryTest {

    @Autowired
    EntityManager em;

    JPAQueryFactory queryFactory;

    @Autowired
    MemberRepository memberRepository;

    @BeforeEach
    public void setUp() {
        queryFactory = new JPAQueryFactory(em);
        memberRepository.deleteAll();
    }

    private static Member createMember(String name, int age, Team team) {
        return Member.builder()
                .userName(name)
                .age(age)
                .team(team)
                .build();
    }

    @Test
    @DisplayName("save, findById 테스트")
    void save() {
        // given
        Member member1 = Member.builder()
                .userName("member1")
                .age(10)
                .build();

        memberRepository.save(member1);
        // when
        Member findMember = memberRepository.findById(member1.getId()).orElseThrow(IllegalArgumentException::new);
        // then
        assertThat(findMember).isEqualTo(member1);
    }

    @Test
    @DisplayName("findAll 테스트")
    void findAll() {
        // given
        Member member1 = Member.builder()
                .userName("member1")
                .age(10)
                .build();

        Member member2 = Member.builder()
                .userName("member2")
                .age(20)
                .build();
        memberRepository.save(member1);
        memberRepository.save(member2);

        // when
        List<Member> members = memberRepository.findAll();
        // then
        assertThat(members).hasSize(2)
                .extracting(Member::getUserName, Member::getAge)
                .containsExactlyInAnyOrder(
                        Tuple.tuple("member1", 10),
                        Tuple.tuple("member2", 20)
                );
    }

    @Test
    @DisplayName("findByName 테스트")
    void findByName() {
        // given
        Member member1 = Member.builder()
                .userName("member1")
                .age(10)
                .build();

        memberRepository.save(member1);

        // when
        List<Member> members = memberRepository.findByUserName("member1");
        // then
        assertThat(members).hasSize(1)
                .extracting(Member::getUserName, Member::getAge)
                .containsExactlyInAnyOrder(
                        Tuple.tuple("member1", 10)
                );
    }

    @Test
    @DisplayName("동적 쿼리 테스트(Where 사용)")
    void DynamicQuery_Where() {
        // given
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

        // when
        MemberSearchCond cond = new MemberSearchCond();
        cond.setAgeGoe(35);
        cond.setAgeLoe(40);
        cond.setTeamName("teamB");

        List<MemberTeamDto> result = memberRepository.search(cond);

        // then
        assertThat(result).hasSize(1)
                .extracting("userName", "age")
                .containsExactlyInAnyOrder(
                        tuple("member4", 40)
                );
    }
}