package study.querydsl_study.dto;

import lombok.*;

import javax.annotation.processing.Generated;

@Getter @Setter
@NoArgsConstructor
@ToString(of = {"userName", "age"})
public class MemberDto {

    private String userName;
    private int age;

    @Builder
    public MemberDto(String userName, int age) {
        this.userName = userName;
        this.age = age;
    }
}
