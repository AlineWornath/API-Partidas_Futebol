package com.neocamp.soccer_matches.service;

import com.neocamp.soccer_matches.dto.match.MatchRequestDto;
import com.neocamp.soccer_matches.dto.match.MatchResponseDto;
import com.neocamp.soccer_matches.entity.ClubEntity;
import com.neocamp.soccer_matches.entity.StadiumEntity;
import com.neocamp.soccer_matches.entity.MatchEntity;
import com.neocamp.soccer_matches.enums.MatchFilterEnum;
import com.neocamp.soccer_matches.mapper.MatchMapper;
import com.neocamp.soccer_matches.repository.MatchRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MatchService {
    private final MatchRepository matchRepository;
    private final ClubService clubService;
    private final StadiumService stadiumService;
    private final MatchMapper matchMapper;

    public Page<MatchResponseDto> listMatchesByFilters(Long clubId, Long stadiumId, MatchFilterEnum filter,
                                                       Pageable pageable) {
        Boolean isRout = null;
        Boolean isHome = null;
        Boolean isAway = null;

        if (clubId != null) {
            clubService.findEntityById(clubId);
        }
        if (stadiumId != null) {
            stadiumService.findEntityById(stadiumId);
        }
       if (filter != null) {
           switch (filter) {
               case ROUT ->  isRout = true;
               case HOME -> isHome = true;
               case AWAY ->  isAway = true;
           }
       }

        Page<MatchEntity> matches = matchRepository.listMatchesByFilters(
                clubId, stadiumId, isRout, isHome, isAway, pageable);
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
        ClubEntity homeClub = clubService.findEntityById(matchRequestDto.getHomeClubId());
        ClubEntity awayClub = clubService.findEntityById(matchRequestDto.getAwayClubId());
        StadiumEntity stadium = stadiumService.findEntityById(matchRequestDto.getStadiumId());

        MatchEntity match = matchMapper.toEntity(matchRequestDto, homeClub, awayClub, stadium);
        matchRepository.save(match);
        return matchMapper.toDto(match);
    }

    public MatchResponseDto update(Long id, MatchRequestDto matchRequestDto) {
        MatchEntity match = findEntityById(id);
        ClubEntity homeClub = clubService.findEntityById(matchRequestDto.getHomeClubId());
        ClubEntity awayClub = clubService.findEntityById(matchRequestDto.getAwayClubId());
        StadiumEntity stadium = stadiumService.findEntityById(matchRequestDto.getStadiumId());

        matchMapper.updateEntityFromDto(matchRequestDto, match);

        MatchEntity updatedMatch = matchRepository.save(match);
        return matchMapper.toDto(updatedMatch);
    }

    public void delete(Long id) {
        MatchEntity match = findEntityById(id);
        matchRepository.delete(match);
    }
}

