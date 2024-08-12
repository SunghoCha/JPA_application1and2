package study.querydsl_study.repository;

import jakarta.persistence.EntityManager;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl_study.entity.Member;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberJpaRepositoryTest {

    @Autowired
    EntityManager em;

    @Autowired
    MemberJpaRepository memberJpaRepository;

    @Test
    @DisplayName("save, findById 테스트")
    void save() {
        // given
        Member member1 = Member.builder()
                .userName("member1")
                .age(10)
                .build();

        memberJpaRepository.save(member1);
        // when
        Member findMember = memberJpaRepository.findById(member1.getId()).orElseThrow(IllegalArgumentException::new);
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
        memberJpaRepository.save(member1);
        memberJpaRepository.save(member2);

        // when
        List<Member> members = memberJpaRepository.findAll();
        // then
        assertThat(members).hasSize(2)
                .extracting(Member::getUserName, Member::getAge)
                .containsExactlyInAnyOrder(
                        Tuple.tuple("member1", 10),
                        Tuple.tuple("member2", 20)
                );
    }

    @Test
    @DisplayName("findAll_Querydsl 테스트")
    void findAll_Querydsl() {
        // given
        Member member1 = Member.builder()
                .userName("member1")
                .age(10)
                .build();

        Member member2 = Member.builder()
                .userName("member2")
                .age(20)
                .build();
        memberJpaRepository.save(member1);
        memberJpaRepository.save(member2);

        // when
        List<Member> members = memberJpaRepository.findAll_Querydsl();
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

        memberJpaRepository.save(member1);

        // when
        List<Member> members = memberJpaRepository.findByUserName("member1");
        // then
        assertThat(members).hasSize(1)
                .extracting(Member::getUserName, Member::getAge)
                .containsExactlyInAnyOrder(
                        Tuple.tuple("member1", 10)
                );
    }

    @Test
    @DisplayName("findByName_Querydsl 테스트")
    void findByName_Querydsl() {
        // given
        Member member1 = Member.builder()
                .userName("member1")
                .age(10)
                .build();

        memberJpaRepository.save(member1);

        // when
        List<Member> members = memberJpaRepository.findByUserName_Querydsl("member1");
        // then
        assertThat(members).hasSize(1)
                .extracting(Member::getUserName, Member::getAge)
                .containsExactlyInAnyOrder(
                        Tuple.tuple("member1", 10)
                );
    }
}