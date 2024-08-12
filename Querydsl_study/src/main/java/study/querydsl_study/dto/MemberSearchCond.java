package study.querydsl_study.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MemberSearchCond {
    // 회원명, 팀명, 나이(ageGoe, ageLoe)

    private String userName;
    private String teamName;
    private Integer ageGoe;
    private Integer ageLoe;
}
