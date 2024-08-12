package study.querydsl_study.repository;

import study.querydsl_study.dto.MemberSearchCond;
import study.querydsl_study.dto.MemberTeamDto;

import java.util.List;

public interface MemberRepositoryCustom {
    List<MemberTeamDto> search(MemberSearchCond cond);
}
