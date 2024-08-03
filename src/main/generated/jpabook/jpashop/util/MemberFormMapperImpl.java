package jpabook.jpashop.util;

import javax.annotation.processing.Generated;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.dto.MemberForm;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-08-03T22:37:41+0900",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 17.0.6 (Oracle Corporation)"
)
public class MemberFormMapperImpl implements MemberFormMapper {

    @Override
    public Address toAddress(MemberForm memberForm) {
        if ( memberForm == null ) {
            return null;
        }

        Address address = new Address();

        address.setCity( memberForm.getCity() );
        address.setStreet( memberForm.getStreet() );
        address.setZipcode( memberForm.getZipcode() );

        return address;
    }
}
