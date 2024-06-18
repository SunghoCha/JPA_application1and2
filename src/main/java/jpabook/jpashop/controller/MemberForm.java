package jpabook.jpashop.controller;

import lombok.*;
import org.hibernate.validator.constraints.NotBlank;

@Builder
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class MemberForm {

    @NotBlank
    private String name;

    private String city;
    private String street;
    private String zipcode;

}
