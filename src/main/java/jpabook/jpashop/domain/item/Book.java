package jpabook.jpashop.domain.item;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;

@Entity
@Builder
@DiscriminatorValue("B")
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class Book extends Item{

    private String author;
    private String isbn;
}
