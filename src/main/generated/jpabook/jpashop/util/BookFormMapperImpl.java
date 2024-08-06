package jpabook.jpashop.util;

import javax.annotation.processing.Generated;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.dto.BookForm;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-08-03T22:37:41+0900",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 17.0.6 (Oracle Corporation)"
)
public class BookFormMapperImpl implements BookFormMapper {

    @Override
    public Book toBook(BookForm bookForm) {
        if ( bookForm == null ) {
            return null;
        }

        Book.BookBuilder book = Book.builder();

        book.author( bookForm.getAuthor() );
        book.isbn( bookForm.getIsbn() );

        return book.build();
    }
}
