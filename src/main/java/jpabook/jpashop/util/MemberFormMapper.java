package jpabook.jpashop.util;

import jpabook.jpashop.dto.MemberForm;
import jpabook.jpashop.domain.Address;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MemberFormMapper {
    MemberFormMapper INSTANCE =  Mappers.getMapper(MemberFormMapper.class);
    
    // MemberForm -> Address 매핑
    Address toAddress(MemberForm memberForm);
}
