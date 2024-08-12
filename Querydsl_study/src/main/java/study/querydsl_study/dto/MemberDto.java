package study.querydsl_study.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

import javax.annotation.processing.Generated;

@Getter @Setter
@NoArgsConstructor
@ToString(of = {"userName", "age"})
public class MemberDto {

    private String userName;
    private int age;

    @QueryProjection
    @Builder
    public MemberDto(String userName, int age) {
        this.userName = userName;
        this.age = age;
    }
}
