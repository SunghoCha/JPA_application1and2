package study.querydsl_study.controller;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl_study.entity.Member;
import study.querydsl_study.entity.Team;

@Profile("local")
@Component
@RequiredArgsConstructor
public class InitMember {

    private final InitMemberService initMemberService;

    @PostConstruct
    public void init() {
        initMemberService.init();
    }

    @Component
    static class InitMemberService {

        @PersistenceContext
        private EntityManager em;

        @Transactional
        public void init() {
            Team teamA = Team.builder()
                    .name("teamA")
                    .build();
            Team teamB = Team.builder()
                    .name("teamB")
                    .build();
            em.persist(teamA);
            em.persist(teamB);

            for (int i = 0; i < 100; i++) {
                Team selectedTeam = i % 2 == 0 ? teamA : teamB;
                em.persist(Member.builder()
                        .userName("member" + i)
                        .age(i)
                        .team(selectedTeam)
                        .build());
            }
        }
    }
}
