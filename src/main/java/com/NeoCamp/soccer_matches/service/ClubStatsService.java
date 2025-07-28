package com.neocamp.soccer_matches.service;

import com.neocamp.soccer_matches.dto.club.ClubRankingDto;
import com.neocamp.soccer_matches.dto.club.ClubStatsResponseDto;
import com.neocamp.soccer_matches.dto.club.ClubVersusClubStatsDto;
import com.neocamp.soccer_matches.dto.match.HeadToHeadResponseDto;
import com.neocamp.soccer_matches.dto.match.MatchResponseDto;
import com.neocamp.soccer_matches.entity.MatchEntity;
import com.neocamp.soccer_matches.enums.MatchFilterEnum;
import com.neocamp.soccer_matches.enums.RankingOrderEnum;
import com.neocamp.soccer_matches.mapper.MatchMapper;
import com.neocamp.soccer_matches.repository.MatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClubStatsService {
    private final MatchRepository matchRepository;
    private final MatchMapper matchMapper;

    public ClubStatsResponseDto getClubStats(Long clubId, MatchFilterEnum filter) {
        return matchRepository.getClubStats(clubId, filter != null ? filter.name() : null);
    }

    public List<ClubVersusClubStatsDto> getClubVersusOpponentsStats(Long clubId, MatchFilterEnum filter) {
        return matchRepository.getClubVersusOpponentsStats(clubId, filter != null? filter.name() : null);
    }

    public HeadToHeadResponseDto getHeadToHeadStats(Long clubId, Long opponentId, MatchFilterEnum filter) {
        String matchFilter = filter != null? filter.name() : null;

        ClubVersusClubStatsDto stats = matchRepository.getHeadToHeadStats(clubId, opponentId, matchFilter);
        List<MatchEntity> matchEntities = matchRepository.getHeadToHeadMatches(clubId, opponentId, matchFilter);
        List<MatchResponseDto> matches = matchEntities.stream().map(matchMapper::toDto).toList();

        return new HeadToHeadResponseDto(stats, matches);
    }

    public List<ClubRankingDto> getClubRanking(RankingOrderEnum rankingOrder) {
        if (rankingOrder == null) {
            throw new IllegalArgumentException("Unknown ranking order: null");
        }
        switch (rankingOrder) {
            case MATCHES -> { return matchRepository.getClubRankingByTotalMatches(); }
            case WINS -> { return matchRepository.getClubRankingByTotalWins(); }
            case GOALS -> { return matchRepository.getClubRankingByTotalGoals(); }
            case POINTS -> { return matchRepository.getClubRankingByTotalPoints(); }
            default -> throw new IllegalArgumentException("Unknown ranking order: " + rankingOrder);
        }
    }
}
