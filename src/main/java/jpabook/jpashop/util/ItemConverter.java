package jpabook.jpashop.util;

import jpabook.jpashop.domain.item.Album;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.domain.item.Movie;

public class ItemConverter {

    public static <T extends Item> T convertFrom(Item item) {
        if (item instanceof Book) {
            return (T) item;
        } else if (item instanceof Movie){
            return (T) item;
        } else if (item instanceof Album) {
            return (T) item;
        } else {
            throw new IllegalArgumentException("지원하지 않는 아이템 유형입니다.");
        }
    }
}
