package jpabook.jpashop.api;

import jakarta.validation.Valid;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

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
