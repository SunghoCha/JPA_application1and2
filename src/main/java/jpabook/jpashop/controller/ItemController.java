package jpabook.jpashop.controller;

import jakarta.validation.Valid;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.domain.item.Movie;
import jpabook.jpashop.dto.BookForm;
import jpabook.jpashop.service.ItemService;
import jpabook.jpashop.util.BookFormMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping("/items/new")
    public String createForm(Model model) {
        model.addAttribute("form", new BookForm());
        return "items/createItemForm";
    }

    @PostMapping("/items/new")
    public String create(@Valid @ModelAttribute BookForm bookForm, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            log.info("bindingResult : {}", bindingResult);
            return "redirect:/items/createItemForm";
        }
        Book book = BookFormMapper.INSTANCE.toBook(bookForm);
        itemService.saveItem(book);
        return "redirect:/itemList";
    }

    @GetMapping("/items")
    public String list(Model model) {
        List<Item> items = itemService.findItems();
        model.addAttribute("items", items);
        return "items/itemList";
    }

    @GetMapping("items/{itemId}/edit")
    public String updateItemForm(@PathVariable("itemId") Long itemId, Model model) {
        Item item = itemService.findOne(itemId);
        if (item instanceof Book) {
            Book book = (Book) item;
            BookForm bookForm = BookForm.builder()
                    .id(book.getId())
                    .name(book.getName())
                    .price(book.getPrice())
                    .stockQuantity(book.getStockQuantity())
                    .author(book.getAuthor())// entity에 setter를 아예 안쓰는게 쉽지 않음..
                    .isbn(book.getIsbn())
                    .build();
            model.addAttribute("bookForm", bookForm);
        } else if (item instanceof Movie){
            // Movie, Album인 경우...
            // 지금은 임시로 Book만 조회 ( 그리고 이런 방식으로 코드를 작성해도 되는지 모르겠음...
            // 애초에 아이템을 수정한다는게 개발자가 어떤 타입의 아이템인지 알고 있다는 뜻일텐데 지금의 가정은 아이템아이디만 알고 어떤 타입인지 모른다는 가정임
            // 이런 상황이 존재하긴 하나?
        }
        return "items/updateItemForm";
    }

    @PostMapping("items/{itemId}/edit")
    public String updateItem(@PathVariable String itemId, @ModelAttribute("form") BookForm form) {

        return "";
    }
}
