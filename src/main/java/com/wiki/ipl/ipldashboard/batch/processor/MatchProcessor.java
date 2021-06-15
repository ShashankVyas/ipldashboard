package com.wiki.ipl.ipldashboard.batch.processor;

import java.time.LocalDate;

import com.wiki.ipl.ipldashboard.data.MatchInput;
import com.wiki.ipl.ipldashboard.model.Match;

import org.springframework.batch.item.ItemProcessor;

public class MatchProcessor implements ItemProcessor<MatchInput, Match> {

    @Override
    public Match process(MatchInput item) throws Exception {

        Match match = new Match();

        match.setId(Long.parseLong(item.getId()));
        match.setCity(item.getCity());
        match.setDate(LocalDate.parse(item.getDate()));

        match.setMatchWinner(item.getWinner());
        String firstInnings = null;
        String secondInnings = null;
        if (null != item.getToss_decision()) {
            if ("bat".equalsIgnoreCase(item.getToss_decision())) {
                firstInnings = item.getToss_winner();
                secondInnings = item.getToss_winner().equalsIgnoreCase(item.getTeam1()) ? item.getTeam2()
                        : item.getTeam1();
            } else {
                firstInnings = item.getToss_winner().equalsIgnoreCase(item.getTeam1()) ? item.getTeam2()
                        : item.getTeam1();
                secondInnings = item.getToss_winner();

            }
        }

        match.setFirstInningsTeam(firstInnings);
        match.setSecondInningsTeam(secondInnings);
        match.setPlayerOfMatch(item.getPlayer_of_match());
        match.setResult(item.getResult());
        match.setResultMargin(item.getResult_margin());
        match.setUmpire1(item.getUmpire1());
        match.setUmpire2(item.getUmpire2());

        return match;
    }

}
