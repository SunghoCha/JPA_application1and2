package jpabook.jpashop.controller;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;

@Getter
@Setter
public class MemberForm {

    @NotBlank
    private String name;

    private String city;
    private String street;
    private String zipcode;

}
