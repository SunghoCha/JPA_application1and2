package study.querydsl_study.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import study.querydsl_study.dto.MemberSearchCond;
import study.querydsl_study.dto.MemberTeamDto;
import study.querydsl_study.repository.MemberJpaRepository;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberJpaRepository memberJpaRepository;

    @GetMapping("/v1/members")
    public List<MemberTeamDto> searchMemberV1(MemberSearchCond cond) {
        return memberJpaRepository.search(cond);
    }
}
