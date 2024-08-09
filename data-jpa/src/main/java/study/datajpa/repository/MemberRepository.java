package study.datajpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import java.util.List;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    List<Member> findByUserNameAndAgeGreaterThan(String userName, int age);

    @Query("select new study.datajpa.dto.MemberDto(m.id, m.userName, t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto();

    /*
         // 카운트 쿼리는 일대다로 조인한 테이블이나 다쪽 테이블이나 총 row 개수가 같기때문에 굳이 조인하지 않고 "다"에 해당하는
         member에서 가져오면 됨. (뒤에 조건절이 붙지 않는다는 가정하에)
         join을 많이하는 쿼리에 따라붙는 count쿼리도 같이 조인을 하면서 만들어지면 성능에 문제 생김
         count쿼리에도 굳이 join이 필요한 상황인지 고려할 것
     */
    @Query(value = "select m from Member m left join m.team t",
            countQuery = "select count(m.userName) from Member m")
    Page<Member> findByAge(int age, Pageable pageable);

    Page<Member> findByAgeGreaterThan(int age, Pageable pageable);


}
