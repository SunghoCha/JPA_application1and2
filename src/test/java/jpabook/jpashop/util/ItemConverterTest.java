package jpabook.jpashop.util;

import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.domain.item.Movie;
import jpabook.jpashop.dto.BookForm;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ItemConverterTest {

    private final String NAME = "책이름99";
    private final int PRICE = 100;
    private final int STOCK_QUANTITY = 10;
    private final String AUTHOR = "책저자99";
    private final String ISBN = "ISBN99";

    @Test
    public void Book타입_반환() throws Exception {
        BookForm bookForm = BookForm.builder()
                .name(NAME)
                .price(PRICE)
                .stockQuantity(STOCK_QUANTITY)
                .author(AUTHOR)
                .isbn(ISBN)
                .build();

        Book book = BookFormMapper.INSTANCE.toBook(bookForm);
        Item itemTypeBook = (Item) book;
        //when
        Movie item = ItemConverter.convertFrom(itemTypeBook);
        //then
    }
}
