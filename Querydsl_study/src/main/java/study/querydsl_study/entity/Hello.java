package study.querydsl_study.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Hello {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
