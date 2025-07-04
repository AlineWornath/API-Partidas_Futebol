package com.neocamp.soccer_matches.service;

import com.neocamp.soccer_matches.dto.club.ClubRankingDto;
import com.neocamp.soccer_matches.dto.club.ClubStatsResponseDto;
import com.neocamp.soccer_matches.dto.club.ClubVersusClubStatsDto;
import com.neocamp.soccer_matches.dto.match.HeadToHeadResponseDto;
import com.neocamp.soccer_matches.dto.match.MatchRequestDto;
import com.neocamp.soccer_matches.dto.match.MatchResponseDto;
import com.neocamp.soccer_matches.entity.ClubEntity;
import com.neocamp.soccer_matches.entity.StadiumEntity;
import com.neocamp.soccer_matches.entity.MatchEntity;
import com.neocamp.soccer_matches.enums.MatchFilterEnum;
import com.neocamp.soccer_matches.enums.RankingOrderEnum;
import com.neocamp.soccer_matches.mapper.MatchMapper;
import com.neocamp.soccer_matches.repository.ClubRepository;
import com.neocamp.soccer_matches.repository.MatchRepository;
import com.neocamp.soccer_matches.validator.ExistenceValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MatchService {
    private final MatchRepository matchRepository;
    private final MatchMapper matchMapper;
    private final ExistenceValidator existenceValidator;
    private final ClubRepository clubRepository;
    private final StadiumService stadiumService;

    public Page<MatchResponseDto> listMatchesByFilters(Long clubId, Long stadiumId, MatchFilterEnum filter,
                                                       Pageable pageable) {
        if (clubId != null) {
            existenceValidator.validateClubExists(clubId);
        }
        if (stadiumId != null) {
            existenceValidator.validateStadiumExists(stadiumId);
        }

        String matchFilter = filter != null ? filter.name() : null;

        Page<MatchEntity> matches = matchRepository.listMatchesByFilters(clubId, stadiumId, matchFilter, pageable);
        return matches.map(matchMapper::toDto);
    }

    public MatchResponseDto findById(Long id) {
        MatchEntity match = matchRepository.findById(id).
                orElseThrow(() -> new EntityNotFoundException("Match not found: " + id));
        return matchMapper.toDto(match);
    }

    public MatchEntity findEntityById(Long Id) {
        return matchRepository.findById(Id).
                orElseThrow(() -> new EntityNotFoundException("Match not found: " + Id));
    }

    public MatchResponseDto save(MatchRequestDto matchRequestDto) {
        ClubEntity homeClub = findClubOrThrow(matchRequestDto.getHomeClubId());
        ClubEntity awayClub = findClubOrThrow(matchRequestDto.getAwayClubId());
        StadiumEntity stadium = stadiumService.findEntityById(matchRequestDto.getStadiumId());

        MatchEntity match = matchMapper.toEntity(matchRequestDto, homeClub, awayClub, stadium);
        matchRepository.save(match);
        return matchMapper.toDto(match);
    }

    public MatchResponseDto update(Long id, MatchRequestDto matchRequestDto) {
        MatchEntity match = findEntityById(id);
        ClubEntity homeClub =  findClubOrThrow(matchRequestDto.getHomeClubId());
        ClubEntity awayClub =  findClubOrThrow(matchRequestDto.getAwayClubId());
        StadiumEntity stadium = stadiumService.findEntityById(matchRequestDto.getStadiumId());

        match.setHomeClub(homeClub);
        match.setAwayClub(awayClub);
        match.setStadium(stadium);

        matchMapper.updateEntityFromDto(matchRequestDto, match);

        MatchEntity updatedMatch = matchRepository.save(match);
        return matchMapper.toDto(updatedMatch);
    }

    public void delete(Long id) {
        MatchEntity match = findEntityById(id);
        matchRepository.delete(match);
    }

    public ClubStatsResponseDto getClubStats(Long clubId, MatchFilterEnum filter) {
        return matchRepository.getClubStats(clubId, filter != null? filter.name() : null);
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
        switch (rankingOrder) {
            case MATCHES -> { return matchRepository.getClubRankingByTotalMatches(); }
            case WINS -> { return matchRepository.getClubRankingByTotalWins(); }
            case GOALS -> { return matchRepository.getClubRankingByTotalGoals(); }
            case POINTS -> { return matchRepository.getClubRankingByTotalPoints(); }
            default -> throw new IllegalArgumentException("Unknown ranking order: " + rankingOrder);
        }
    }

    private ClubEntity findClubOrThrow(Long clubId) {
        return clubRepository.findById(clubId)
                .orElseThrow(() -> new EntityNotFoundException("Club not found: " + clubId));
    }
}

