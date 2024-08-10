package study.datajpa.controller;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import study.datajpa.entity.Member;
import study.datajpa.repository.MemberRepository;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;

    @GetMapping("/members/{id}")
    public String findMember(@PathVariable("id") Long id) {
        Member member = memberRepository.findById(id).orElseThrow(IllegalArgumentException::new);
        return member.getUserName();
    }

    @GetMapping("/membersV2/{id}")
    public String findMemberV2(@PathVariable("id") Member member) {
        return member.getUserName();
    }

    @GetMapping("/members")
    public Page<Member> list(Pageable pageable) {
        return memberRepository.findAll(pageable);
    }

    @PostConstruct
    public void init() {
        for (int i = 1; i < 30; i++) {
            Member member = Member.builder()
                    .age(i)
                    .userName("member" + i)
                    .build();
            memberRepository.save(member);
        }
    }
}
