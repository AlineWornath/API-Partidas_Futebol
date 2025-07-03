package com.neocamp.soccer_matches.controller;

import com.neocamp.soccer_matches.dto.club.*;
import com.neocamp.soccer_matches.dto.match.HeadToHeadResponseDto;
import com.neocamp.soccer_matches.enums.RankingOrderEnum;
import com.neocamp.soccer_matches.enums.MatchFilterEnum;
import com.neocamp.soccer_matches.service.ClubService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/clubs")
public class ClubController {

    private final ClubService clubService;

    @GetMapping
    public ResponseEntity<Page<ClubResponseDto>> listClubsByFilters(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String stateCode,
            @RequestParam(required = false) Boolean active,
            Pageable pageable) {
        Page<ClubResponseDto> clubs = clubService.listClubsByFilters(name, stateCode, active, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(clubs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClubResponseDto> findById(@PathVariable Long id) {
        ClubResponseDto club = clubService.findById(id);
        return ResponseEntity.status(HttpStatus.OK).body(club);
    }

    @GetMapping("/{id}/stats")
    public ResponseEntity<ClubStatsResponseDto> getClubStats(
            @PathVariable Long id,
            @RequestParam(required = false) MatchFilterEnum filter) {
        ClubStatsResponseDto clubStats = clubService.getClubStats(id, filter);
        return ResponseEntity.status(HttpStatus.OK).body(clubStats);
    }

    @GetMapping("/{id}/opponents/stats")
    public ResponseEntity<List<ClubVersusClubStatsDto>> getClubVersusOpponentsStats(
            @PathVariable Long id,
            @RequestParam(required = false) MatchFilterEnum filter) {
        List<ClubVersusClubStatsDto> opponentsStats = clubService.getClubVersusOpponentsStats(id,
                filter);
        return ResponseEntity.status(HttpStatus.OK).body(opponentsStats);
    }

    @GetMapping("/{clubId}/head-to-head/{opponentId}")
    public ResponseEntity<HeadToHeadResponseDto> getHeadToHeadStats(
            @PathVariable Long clubId,
            @PathVariable Long opponentId,
            @RequestParam(required = false) MatchFilterEnum filter) {
        HeadToHeadResponseDto headToHeadStats = clubService.getHeadToHeadStats(clubId, opponentId, filter);
        return ResponseEntity.status(HttpStatus.OK).body(headToHeadStats);
    }

    @GetMapping("/ranking")
    public ResponseEntity<List<ClubRankingDto>>  getClubRanking(RankingOrderEnum rankingOrderEnum) {
        List<ClubRankingDto> ranking = clubService.getClubRanking(rankingOrderEnum);
        return ResponseEntity.status(HttpStatus.OK).body(ranking);
    }

    @PostMapping
    public ResponseEntity<ClubResponseDto> create(@RequestBody @Valid ClubRequestDto dto) {
        ClubResponseDto club = clubService.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(club);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClubResponseDto> update(@PathVariable Long id, @RequestBody @Valid ClubRequestDto dto) {
        ClubResponseDto club = clubService.update(id, dto);
        return ResponseEntity.status(HttpStatus.OK).body(club);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        clubService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
