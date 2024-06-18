package jpabook.jpashop.util;

import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.dto.BookForm;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BookFormMapper {
    BookFormMapper INSTANCE = Mappers.getMapper(BookFormMapper.class);

    // BookForm -> Book 매핑
    Book toBook(BookForm bookForm);
}
