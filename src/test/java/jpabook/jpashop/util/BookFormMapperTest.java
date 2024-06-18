package jpabook.jpashop.util;

import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.dto.BookForm;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BookFormMapperTest {

    private final String NAME = "책이름99";
    private final int PRICE = 100;
    private final int STOCK_QUANTITY = 10;
    private final String AUTHOR = "책저자99";
    private final String ISBN = "ISBN99";

    @Test
    public void Book매핑테스트() throws Exception {
        //given
        BookForm bookForm = BookForm.builder()
                .name(NAME)
                .price(PRICE)
                .stockQuantity(STOCK_QUANTITY)
                .author(AUTHOR)
                .isbn(ISBN)
                .build();
        //when
        Book book = BookFormMapper.INSTANCE.toBook(bookForm);
        //then
        assertEquals(book.getName(), NAME);
        assertEquals(book.getPrice(), PRICE);
        assertEquals(book.getStockQuantity(), STOCK_QUANTITY);
        assertEquals(book.getAuthor(), AUTHOR);
        assertEquals(book.getIsbn(), ISBN);
    }

}
