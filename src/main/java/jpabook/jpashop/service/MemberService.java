package jpabook.jpashop.service;

import jpabook.jpashop.api.UpdateMemberRequest;
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

    @Transactional
    public void update(Long id, UpdateMemberRequest updateMemberRequest) {
        Member member = memberRepository.findOne(id); // 트랜잭션 안에서 영속성 컨텍스트인 member 객체 얻음
        member.setName(updateMemberRequest.getName());  // 변경감지 적용
        /*
            새로 find한 member라서 영속성컨텍스트의 1차캐시에 없을 것이고 DB에서 새로 가져오면서 1차 캐시에 저장 후 member 반환해줌
            member.setName()을 하면 1차 캐시의 스냅샷과 다른 결과 생김
            setName()완료하면 핵심 로직이 끝난 것이므로 @Transactional 관련 AOP가 작동하면서 commit을 하게됨
            이 떄 JPA는 flush()를 하게 되는데 스냅샷에 변경이 일어났으므로 쓰기지연저장소에 변경사항을 update문으로 저장하고 flush()를 함
            flush()를 하면 쓰기지연저장소의 모든 쿼리문이 DB에 명령어로 전달되고 비워짐(1차캐시는 남아있는 상태)
            그 후 DB의 commit이 일어나며 DB에 모든 변경사항이 반영됨
         */
    }
}
