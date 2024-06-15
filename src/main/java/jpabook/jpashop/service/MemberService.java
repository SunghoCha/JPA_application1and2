package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor // 생성자 autowired
public class MemberService {

    private final MemberRepository memberRepository;

    //회원 가입
    @Transactional
    public Long join(Member member) {
        validateDuplicateMember(member);
        memberRepository.save(member); // repository에서 em.persist에서 id값이 존재하는게 보장됨. 영속성컨텍스트에서 값을 저장할때 key값을 id로 하기때문. 아직 db에 들어간 시점이 아니더라도 가능한 이유
        return member.getId();
    }
    /*  
        repository에서 em.persist에서 id값이 존재하는게 보장됨. 
        영속성컨텍스트에서 값을 저장할때 key값을 id로 하기때문. 
        아직 db에 들어간 시점이 아니더라도 가능한 이유.
        member객체 안에 id값을 넣어주는건가? 확인필요
        맞다면 member에 id값을 세팅하고 (key&value)로 (member.getId()&member) 형태를 취하는 걸지도
     */
    private void validateDuplicateMember(Member member) { // 멀티쓰레드인 상황을 고려하여 최후의 방어선으로 DB에서 해당 속성을 유니크속성으로 할 것
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if (!findMembers.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    //회원 전체 조회
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    public Member findOne(Long memberId) {
        return memberRepository.findOne(memberId);
    }
}
