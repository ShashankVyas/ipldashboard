package com.wiki.ipl.ipldashboard.repository;

import java.util.List;

import com.wiki.ipl.ipldashboard.model.Match;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

public interface MatchRepository extends CrudRepository<Match, Long>{

    List<Match> getByFirstInningsTeamOrSecondInningsTeamOrderByDateDesc(String team1,String team2, Pageable pageable);
    
}
