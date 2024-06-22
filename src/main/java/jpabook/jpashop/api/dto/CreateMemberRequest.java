package jpabook.jpashop.api.dto;

import jakarta.persistence.Embedded;
import jakarta.validation.constraints.NotBlank;
import jpabook.jpashop.domain.Address;
import lombok.Data;

@Data
public class CreateMemberRequest {

    @NotBlank
    private String name;
}
