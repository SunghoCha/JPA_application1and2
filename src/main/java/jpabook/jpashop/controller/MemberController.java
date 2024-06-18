package jpabook.jpashop.controller;

import jakarta.validation.Valid;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.dto.MemberForm;
import jpabook.jpashop.service.MemberService;
import jpabook.jpashop.util.MemberFormMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping(value = "/members/new")
    public String createForm(Model model) {
        model.addAttribute("memberForm", new MemberForm());
        return "members/createMemberForm";
    }

    @PostMapping("/members/new")
    public String createMember(@Valid @ModelAttribute MemberForm memberForm, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            log.info("bindingResult : {}",bindingResult);
            return "members/createMemberForm";
        }
        Address address = MemberFormMapper.INSTANCE.toAddress(memberForm);
        log.info("address : {}", address);
        Member member = Member.builder()
                                .name(memberForm.getName())
                                .address(address)
                                .build();
        memberService.join(member);
        return "redirect:/";
    }

    @GetMapping("/members")
    public String list(Model model) {
        List<Member> members = memberService.findMembers(); // 일단 서버단에서 뷰로 렌더링하는거라 member로 보내긴했는데 restAPI에선 절대 하지 말것
        model.addAttribute("members", members);
        return "members/memberList";
    }
}
