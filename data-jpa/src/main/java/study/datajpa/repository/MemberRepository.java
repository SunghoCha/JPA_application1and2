package study.datajpa.repository;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import java.util.List;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

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

    int countByAgeGreaterThan(int age);

    /*
        @Modifying과 @Query를 사용하면 영속성 컨텍스트의 변경감지를 통한 업데이트 방식이 아님 
        영속성 컨텍스트의 캐시와 별개로 데이터베이스에 직접 접근하는거라 영속성 컨텍스트와 데이터베이스의 상태가 서로 일치하지 않을 수 있음
         em.flush()로 영속성 컨텍스트의 변경사항을 데이터베이스에 반영하고 em.clear()를 통해 영속성 컨텍스트 내용을 날리면
         엔티티 매니저는 1차캐시가 없으므로 최신사항이 모두 반영된 데이터베이스로부터 새로운 데이터를 가져와서 캐싱하고 반환해줌
         * JPQL query를 날릴때 트랜잭션 커밋이 자동으로 호출되고 커밋 전에 flush()가 먼저 자동으로 호출되므로 em.flush() 는 생략 가능
     */
    @Modifying(clearAutomatically = true)
    @Query("update Member m set m.age = m.age + 100 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);

    @Override
    @EntityGraph(attributePaths = {"team"})
    List<Member> findAll();

    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
    Member findReadOnlyByUserName(String userName);

    // 실시간 데이터 많은 경우 권장하지 않음(옵티미스틱 락 쓰는걸 권장). 금액을 맞추거나 하는 중요한 로직할 때 사용 권장
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Member> findLockByUserName(String userName);
}
