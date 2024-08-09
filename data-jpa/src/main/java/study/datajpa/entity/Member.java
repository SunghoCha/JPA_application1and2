package study.datajpa.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "userName", "age"})
public class Member {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;
    private String userName;
    private int age;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @Builder // build() 할 때 생성자 통해서 만드니까 여기서 연관관계 편의 메서드 활용하면 될 듯
    private Member(Long id, String userName, int age, Team team) {
        this.id = id;
        this.userName = userName;
        this.age = age;
        if (team != null) {
            changeTeam(team);
        }
    }

    // == 연관관계 편의 메서드 == // 보통 비지니스적으로 중심이 되는 엔티티에 설정
    public void changeTeam(Team team) {
        this.team = team;
        team.getMembers().add(this);
    }

}
