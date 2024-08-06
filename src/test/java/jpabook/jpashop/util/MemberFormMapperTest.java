package jpabook.jpashop.util;

import jpabook.jpashop.dto.MemberForm;
import jpabook.jpashop.domain.Address;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MemberFormMapperTest {

    private final String NAME = "회원99";
    private final String CITY = "도시99";
    private final String STREET = "거리99";
    private final String ZIPCODE = "우편99";
    @Test
    public void 매핑테스트() throws Exception {
        //given
        MemberForm memberForm = MemberForm.builder()
                .name(NAME)
                .city(CITY)
                .street(STREET)
                .zipcode(ZIPCODE)
                .build();
        //when
        Address address = MemberFormMapper.INSTANCE.toAddress(memberForm);
        //then
        assertEquals(address.getCity(), CITY);
        assertEquals(address.getStreet(), STREET);
        assertEquals(address.getZipcode(), ZIPCODE);
    }
}
