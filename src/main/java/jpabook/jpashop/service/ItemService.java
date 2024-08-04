package jpabook.jpashop.service;

import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.item.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    @Transactional
    public void saveItem(Item item) {
        itemRepository.save(item);
    }

    @Transactional
    public void updateItem(Long itemId, String name, int price, int stockQuantity) { //param: 파라미터로 넘어온 준영속 상태의 엔티티
        Item findItem = itemRepository.findOne(itemId); // 트랜잭션 안에서 엔티티를 다시 조회
        findItem.setName(name);
        findItem.setPrice(price);
        findItem.setStockQuantity(stockQuantity); // 트랜잭션 커밋 시점에 변경감지(Dirty Checking)이 동작해서 DB에 UPDATE SQL 실행
        // setter로 하지 말고 findItem.change(~param) 형태로 만들어서 entity 내부에서 책임지도록해야 유지보수 용이함.
    }

    public List<Item> findItems() {
        return itemRepository.findAll();
    }

    public Item findOne(Long itemId) {
        return itemRepository.findOne(itemId);
    }
}
