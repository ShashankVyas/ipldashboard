package com.wiki.ipl.ipldashboard.controller;

import lombok.extern.slf4j.Slf4j;

import com.wiki.ipl.ipldashboard.model.Team;
import com.wiki.ipl.ipldashboard.repository.MatchRepository;
import com.wiki.ipl.ipldashboard.repository.TeamRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/wiki/ipl/dashboard/v1")
@Slf4j
public class TeamController {

    @Autowired
    private TeamRepository repository;
    
    @Autowired
    private MatchRepository matchRepository;

    @GetMapping("/team/{name}")
    public Team getTeamName(@PathVariable String name){
        log.info("Name of the team = {}",name);
        Team team = repository.findByTeamName(name);
        Pageable pageable = PageRequest.of(0, 10);

        team.setMatches(matchRepository.getByFirstInningsTeamOrSecondInningsTeamOrderByDateDesc(name,name,pageable));

        return team;

    }

    @GetMapping(value ="/health")
    public ResponseEntity<String> healthcheck(){
        return new ResponseEntity<>("UP",HttpStatus.OK);
    }

    
}
