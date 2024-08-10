package study.datajpa.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    TeamRepository teamRepository;

    @PersistenceContext
    EntityManager em;

    @BeforeEach
    void setup() {
        memberRepository.deleteAll();
        teamRepository.deleteAll();
    }

    @Test
    @DisplayName("member 등록 테스트")
    void test() {
        // given
        Member member = Member.builder()
                .userName("memberA")
                .build();
        Member savedMember = memberRepository.save(member);

        // when
        Member findMember = memberRepository.findById(savedMember.getId()).orElseThrow(IllegalArgumentException::new);

        // then
        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUserName()).isEqualTo(member.getUserName());
        assertThat(findMember).isEqualTo(member);
    }

    //=== 순수 JpaRepositoryTest 복사해서 테스트 ===//
    @Test
    @DisplayName("member 등록")
    void findMember() {
        // given
        Member member = Member.builder()
                .userName("memberA")
                .build();
        Member savedMember = memberRepository.save(member);

        // when
        Member findMember = memberRepository.findById(savedMember.getId()).orElseThrow(IllegalArgumentException::new);

        // then
        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUserName()).isEqualTo(member.getUserName());
        assertThat(findMember).isEqualTo(member);
    }

    @Test
    @DisplayName("쿼리 힌트 테스트")
    void queryHint () {
        // given
        Member member = Member.builder()
                .userName("memberA")
                .build();
        Member savedMember = memberRepository.save(member);
        em.flush();
        em.clear();

        // when
        Member findMember = memberRepository.findReadOnlyByUserName("memberA");
        findMember.setUserName("changedMemberA");

        em.flush();
        em.clear();
        Member savedMember2 = memberRepository.save(member);
        // then
        assertThat(savedMember2.getUserName()).isEqualTo("memberA");
    }

    @Test
    @DisplayName("단건 조회 검증")
    void findById() {
        // given
        Member member1 = Member.builder()
                .userName("member1")
                .build();

        Member member2 = Member.builder()
                .userName("member2")
                .build();

        memberRepository.save(member1);
        memberRepository.save(member2);

        // when
        Member findMember1 = memberRepository.findById(member1.getId()).orElseThrow(IllegalArgumentException::new);
        Member findMember2 = memberRepository.findById(member2.getId()).orElseThrow(IllegalArgumentException::new);

        // then
        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);
    }

    @Test
    @DisplayName("리스트 조회 검증")
    void  findAllMember() {
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
    @DisplayName("삭제 검증")
    void deleteMember() {
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
        long memberCountBeforeDeleted = memberRepository.count();
        memberRepository.delete(member1);
        memberRepository.delete(member2);

        long memberCountAfterDeleted = memberRepository.count();

        // then
        assertThat(memberCountBeforeDeleted).isEqualTo(2);
        assertThat(memberCountAfterDeleted).isEqualTo(0);
    }
    
    @Test
    @DisplayName("이름_나이조건_검색")
    void findByUserNameAndAgeGreaterThen() {
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
        List<Member> result = memberRepository.findByUserNameAndAgeGreaterThan("member2", 15);

        // then
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("MemberDto 조회 검증")
    void findMemberDto() {
        // given
        Team team1 = new Team("team1");
        Team team2 = new Team("team2");

        teamRepository.saveAll(List.of(team1, team2));

        Member member1 = Member.builder()
                .team(team1)
                .userName("member1")
                .age(10)
                .build();

        Member member2 = Member.builder()
                .team(team2)
                .userName("member2")
                .age(20)
                .build();

        memberRepository.save(member1);
        memberRepository.save(member2);
        // when
        List<MemberDto> memberDto = memberRepository.findMemberDto();

        // then
        assertThat(memberDto).hasSize(2)
                .extracting(MemberDto::getTeamName, MemberDto::getUserName)
                .containsExactlyInAnyOrder(
                        Tuple.tuple("team1", "member1"),
                        Tuple.tuple("team2", "member2")
                );
    }

    @Test
    @DisplayName("paging 테스트")
    void paging() {
        // given
        for (int i = 1; i < 30; i++) {
            Member member = Member.builder()
                    .age(i)
                    .userName("member" + i)
                    .build();
            memberRepository.save(member);
        }

        Pageable pageRequest = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "age"));
        // when
        Page<Member> pageWithSameAge = memberRepository.findByAge(10, pageRequest);
        Page<Member> pageGreaterThanAge = memberRepository.findByAgeGreaterThan(10, pageRequest);

        pageWithSameAge.map(MemberDto::of); // page에서 mpa으로 entity -> dto 변환 가능

        List<Member> membersSameAge = pageWithSameAge.getContent();
        long totalElementsWithSameAge = pageWithSameAge.getTotalElements();

        List<Member> membersGreaterThanAge = pageGreaterThanAge.getContent();
        long totalElementsGreaterThanAGe = pageGreaterThanAge.getTotalElements();

        // then
        assertThat(pageWithSameAge).hasSize(1);
        assertThat(membersSameAge).hasSize(1);

        assertThat(pageGreaterThanAge).hasSize(5);
        assertThat(membersGreaterThanAge).hasSize(5);
        assertThat(totalElementsGreaterThanAGe).isEqualTo(19);
    }

    @Test
    @DisplayName("bulk data 수정 테스트")
    void bulkUpdate() {
        // given
        for (int i = 1; i < 30; i++) {
            Member member = Member.builder()
                    .age(i)
                    .userName("member" + i)
                    .build();
            memberRepository.save(member);
        }
        // when
        int resultCount = memberRepository.bulkAgePlus(20);
        long totalCount = memberRepository.countByAgeGreaterThan(100);
        // then
        assertThat(resultCount).isEqualTo(10);
        assertThat(totalCount).isEqualTo(10);
    }

    @Test
    @DisplayName("fetch join 테스트")
    void findMemberLazy() {
        // given
        Team team1 = new Team("team1");
        Team team2 = new Team("team2");

        teamRepository.saveAll(List.of(team1, team2));

        Member member1 = Member.builder()
                .team(team1)
                .userName("member1")
                .age(10)
                .build();

        Member member2 = Member.builder()
                .team(team2)
                .userName("member2")
                .age(20)
                .build();

        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

        // when
        List<Member> members = memberRepository.findAll();

        // then
        for (Member member : members) {
            System.out.println("member.getUserName() = " + member.getUserName());
            System.out.println("member.teamClass = " + member.getTeam().getClass());
            System.out.println("member.team = " + member.getTeam());
        }
    }

    @Test
    @DisplayName("Lock 테스트(쿼리 확인용)")
    void lock() {
        // given
        Member member1 = Member.builder()
                .userName("member1")
                .age(10)
                .build();

        memberRepository.save(member1);

        // when
        List<Member> result = memberRepository.findLockByUserName("member1");

        // then
    }
    
    @Test
    @DisplayName("커스텀 repository")
    void callCustom() {
        // given
        memberRepository.findMemberCustom();
        // when
        
        // then
    }
}