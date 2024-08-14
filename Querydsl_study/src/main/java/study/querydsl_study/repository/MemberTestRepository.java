package study.querydsl_study.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;
import study.querydsl_study.dto.MemberSearchCond;
import study.querydsl_study.entity.Member;
import study.querydsl_study.repository.support.Querydsl4RepositorySupport;

import java.util.List;

import static org.springframework.util.StringUtils.hasText;
import static study.querydsl_study.entity.QMember.member;
import static study.querydsl_study.entity.QTeam.team;

@Repository
public class MemberTestRepository extends Querydsl4RepositorySupport {

    public MemberTestRepository() {
        super(Member.class);
    }

    public List<Member> basicSelect() {
        return select(member)
                .from(member)
                .fetch();
    }

    public List<Member> basicSelectFrom() {
        return selectFrom(member)
                .fetch();
    }

    public Page<Member> searchPageByApplyPage(MemberSearchCond cond, Pageable pageable) {
        JPAQuery<Member> query = selectFrom(member)
                .where(
                        userNameEq(cond.getUserName()),
                        teamNameEq(cond.getTeamName()),
                        ageGoe(cond.getAgeGoe()),
                        ageLoe(cond.getAgeLoe())
                );

        List<Member> content = getQuerydsl().applyPagination(pageable, query).fetch();

        JPAQuery<Long> countQuery = select(member.count())
                .from(member)
                .where(
                        userNameEq(cond.getUserName()),
                        teamNameEq(cond.getTeamName()),
                        ageGoe(cond.getAgeGoe()),
                        ageLoe(cond.getAgeLoe())
                );

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    public Page<Member> applyPagination(MemberSearchCond cond, Pageable pageable) {
        return applyPagination(pageable, query->
                query.selectFrom(member)
                        .where(
                                userNameEq(cond.getUserName()),
                                teamNameEq(cond.getTeamName()),
                                ageGoe(cond.getAgeGoe()),
                                ageLoe(cond.getAgeLoe())
                        ));
    }

    private BooleanExpression userNameEq(String userName) {
        return hasText(userName) ? member.userName.eq(userName) : null;
    }

    private BooleanExpression teamNameEq(String teamName) {
        return hasText(teamName) ? team.name.eq(teamName) : null;
    }

    private BooleanExpression ageGoe(Integer ageGoe) {
        return !ObjectUtils.isEmpty(ageGoe) ? member.age.goe(ageGoe) : null;
    }

    private BooleanExpression ageLoe(Integer ageLoe) {
        return !ObjectUtils.isEmpty(ageLoe) ? member.age.loe(ageLoe) : null;
    }
}
