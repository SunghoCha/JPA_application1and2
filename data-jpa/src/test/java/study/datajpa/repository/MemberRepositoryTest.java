package study.datajpa.repository;

import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @BeforeEach
    void setup() {
        memberRepository.deleteAll();
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
}