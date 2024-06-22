package jpabook.jpashop.api.controller;

import jakarta.validation.Valid;
import jpabook.jpashop.api.dto.*;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    @GetMapping("/api/v1/members")
    public List<Member> memberV1() {
        return memberService.findMembers();
    }
    /*
        엔티티가 직접 노출되버리면 API스펙에 맞게 변경되어야 하고 결국 프레젠테이션 계층에 의존하게 되면서 프레젠테이션계층과 엔티티간에 양방향 의존관계가 생겨버림
        엔티티의 순수성이 깨지고 결국 여러 API가 원하는 스펙을 엔티티로는 감당 못하게되어 변경이 어려워짐
        API 응답 스펙에 맞춰 별도의 DTO를 만들어서 반환해야 한다.
        그리고 단순히 리스트형식으로 반환되면 다른 타입의 여러 데이터들을 추가하기가 어려워져 유연성이 떨어짐
     */

    @GetMapping("/api/v2/members")
    public Result memberV2() {
        List<Member> findMembers = memberService.findMembers();
        List<MemberDto> memberDtos = findMembers.stream()
                .map(member -> new MemberDto(member.getName()))
                .collect(Collectors.toList());
        return new Result(memberDtos); //리스트를 바로 반환하는 것이 아니고 객체에 담아서 해야 추후 변경에서 유연함 생김
    }

    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member) {

        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    @PostMapping("/api/v2/members") // 별도의 DTO를 사용하면 API의 스펙이 바뀌지 않음
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest createMemberRequest) {

        Member member = new Member();
        member.setName(createMemberRequest.getName());

        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    @PutMapping("/api/v2/members/{id}")
    public UpdateMemberResponse updateMemberV2(
            @PathVariable("id") Long id,
            @RequestBody @Valid UpdateMemberRequest updateMemberRequest) {

        memberService.update(id, updateMemberRequest);
        Member findMember = memberService.findOne(id);
        return new UpdateMemberResponse(findMember.getId(), findMember.getName());
        /*
            update를 하면서 member 객체를 반환한다는 것은 command(update하는 행동)와 query(memer를 조회)를 분리한다는 정책에 위배됨(그리고 영속성컨텍스트도 아닌 member 반환)
            명령과 조회는 분리되어야함 (가독성이 떨어지고 다른사람에게 혼선을 줄 가능성이 높아져 나중에 큰 버그의 요인이 됨(클린 코드 참고))
            Member findMember = memberService.findOne(id);를 따로하면 일단 명령과 조회는 분리된건데 그래도 영속성 컨텍스트가 아닌건 마찬가지일듯?
         */
    }
}
