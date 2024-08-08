package study.datajpa.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class MemberDto {

    private Long id;
    private String userName;
    private String teamName;

    @Builder
    public MemberDto(Long id, String userName, String teamName) {
        this.id = id;
        this.userName = userName;
        this.teamName = teamName;
    }
}
