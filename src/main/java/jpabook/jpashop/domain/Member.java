package jpabook.jpashop.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
@Builder
@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Member {


    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @NotBlank
    private String name;

    @Embedded
    private Address address;

    @JsonIgnore // 무한루프 방지
    @OneToMany(mappedBy = "member") //컬렉션은 필드에서 초기화하는게 안전
    private List<Order> orders = new ArrayList<>(); // null 문제에서 안전. 하이버네이트가 엔티티를 영속화할 때, 컬렉션을 감싸서 내장 컬렉션으로 변경하는 과정에서 문제 생길 위험 방지
}
