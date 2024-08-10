package study.datajpa.entity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.repository.MemberRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberTest {

    @PersistenceContext
    EntityManager em;

    @Autowired
    MemberRepository memberRepository;

    @Test
    @DisplayName("Member 엔티티 생성")
    void createMemberEntity() {
        // given
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = Member.builder()
                .userName("member1")
                .age(10)
                .team(teamA)
                .build();

        Member member2 = Member.builder()
                .userName("member2")
                .age(20)
                .team(teamA)
                .build();

        Member member3 = Member.builder()
                .userName("member3")
                .age(30)
                .team(teamB)
                .build();

        Member member4 = Member.builder()
                .userName("member4")
                .age(40)
                .team(teamB)
                .build();

        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);
        em.flush();
        em.clear();

        // when
        List<Member> members = em.createQuery("select m from Member m", Member.class).getResultList();

        // then
        assertThat(members).hasSize(4);
//                .extracting(Member::getUserName,Member::getAge, Member::getTeam)
//                .containsExactlyInAnyOrder(
//                        Tuple.tuple("member1", 10, teamA),
//                        Tuple.tuple("member2", 20, teamA),
//                        Tuple.tuple("member3", 30, teamB),
//                        Tuple.tuple("member4", 40, teamB)
//                );
        System.out.println("members.get(0).getTeam() = " + members.get(0).getTeam());
        System.out.println("members.get(0).getTeam() = " + members.get(0).getTeam().getClass());
        System.out.println("members.get(0).getTeam() = " + members.get(0).getTeam().getName());
    }
    
    @Test
    @DisplayName("Auditing 테스트")
    void JpaEventBaseEntity() throws Exception {
        // given
        Member member1 = Member.builder()
                .userName("member1")
                .age(10)
                .build();

        memberRepository.save(member1); // @PrePersist

        Thread.sleep(100); //테스트용... update시 최소 100ms나게 해서 확인하기 위한 용도로 임시로 사용
        member1.setUserName("member2");

        em.flush(); //@PreUpdate
        em.clear();

        // when
        Member findMember = memberRepository.findById(member1.getId()).orElseThrow(IllegalArgumentException::new);

        // then
        System.out.println("findMember.getCreatedDate = " + findMember.getCreatedDate());
        System.out.println("findMember.getLastModifiedDate = " + findMember.getLastModifiedDate());
        System.out.println("findMember.getCreatedBy = " + findMember.getCreatedBy());
        System.out.println("findMember.getLastModifiedBy = " + findMember.getLastModifiedBy());
    }
}