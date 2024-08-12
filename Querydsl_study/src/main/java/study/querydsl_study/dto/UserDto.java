package study.querydsl_study.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@ToString(of = {"name", "age"})
public class UserDto {

    private String name;
    private int age;

    @Builder
    public UserDto(String name, int age) {
        this.name = name;
        this.age = age;
    }
}
