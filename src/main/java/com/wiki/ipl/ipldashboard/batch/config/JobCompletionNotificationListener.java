package com.wiki.ipl.ipldashboard.batch.config;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import com.wiki.ipl.ipldashboard.model.Team;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JobCompletionNotificationListener extends JobExecutionListenerSupport {

    private static final Logger log = LoggerFactory.getLogger(JobCompletionNotificationListener.class);

    private final EntityManager em;

    @Autowired
    public JobCompletionNotificationListener(EntityManager em) {
        this.em = em;
    }

    @Override
    @Transactional
    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            log.info("Job successfully executed");

            Map<String, Team> teamData = new HashMap<>();

            em.createQuery("SELECT firstInningsTeam,count(*) from Match group by firstInningsTeam", Object[].class)
                    .getResultList().stream().map(e -> new Team((String) e[0], (long) e[1]))
                    .forEach(team -> teamData.put(team.getTeamName(), team));
            log.info("Updated first innings team. Size = {}", teamData.size());
            em.createQuery("SELECT secondInningsTeam,count(*) from Match group by secondInningsTeam", Object[].class)
                    .getResultList().stream().forEach(e -> {
                        Team team = teamData.get(e[0]);
                        team.setTotalMatches(team.getTotalMatches() + (long) e[1]);
                        log.info("Second team name & count = {} and {}", e[0], e[1]);
                    });

            log.info("Updated second innings team. Size = {}", teamData.size());
            em.createQuery("SELECT matchWinner,count(*) from Match group by matchWinner", Object[].class)
                    .getResultList().stream().forEach(e -> {
                        log.info("Matchwinner team && count = {} and {}", e[0], e[1]);
                        Team team = teamData.get(e[0]);
                        if (null != team)
                            team.setTotalWins((long) e[1]);
                    });

            log.info("Updated match winner. Size = {}", teamData.size());
            teamData.values().forEach(team -> em.persist(team));

            teamData.values().forEach(team -> log.info("Team Name = {} and Total Matches = {}, total wins = {}",
                    team.getTeamName(), team.getTotalMatches(), team.getTotalWins()));
        }
    }
}
