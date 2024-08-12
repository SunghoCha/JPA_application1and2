package study.querydsl_study.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.querydsl_study.entity.Member;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

    List<Member> findByUserName(String userName);
}
