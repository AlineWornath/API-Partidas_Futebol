package com.neocamp.soccer_matches.service;

import com.neocamp.soccer_matches.dto.match.FinishMatchRequestDto;
import com.neocamp.soccer_matches.dto.match.MatchRequestDto;
import com.neocamp.soccer_matches.dto.match.MatchResponseDto;
import com.neocamp.soccer_matches.entity.ClubEntity;
import com.neocamp.soccer_matches.entity.MatchEntity;
import com.neocamp.soccer_matches.entity.StadiumEntity;
import com.neocamp.soccer_matches.enums.MatchFilterEnum;
import com.neocamp.soccer_matches.enums.MatchStatusEnum;
import com.neocamp.soccer_matches.exception.BusinessException;
import com.neocamp.soccer_matches.mapper.MatchMapper;
import com.neocamp.soccer_matches.messagingrabbitmq.MatchResultPublisher;
import com.neocamp.soccer_matches.messagingrabbitmq.dto.ClubScoreDto;
import com.neocamp.soccer_matches.messagingrabbitmq.dto.MatchInfoMessageDto;
import com.neocamp.soccer_matches.messagingrabbitmq.dto.MatchResultMessageDto;
import com.neocamp.soccer_matches.repository.MatchRepository;
import com.neocamp.soccer_matches.validator.ExistenceValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MatchService {

    private final MatchRepository matchRepository;
    private final MatchMapper matchMapper;
    private final ExistenceValidator existenceValidator;
    private final ClubService clubService;
    private final StadiumService stadiumService;
    private final MatchResultPublisher matchResultPublisher;

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

    public MatchEntity findByUuid(UUID uuid) {
        return matchRepository.findByUuid(uuid).orElseThrow(
                () -> new EntityNotFoundException("Match not found: " + uuid));
    }

    @Transactional
    public MatchResponseDto save(MatchRequestDto matchRequestDto) {
        ClubEntity homeClub = clubService.findEntityById(matchRequestDto.getHomeClubId());
        ClubEntity awayClub = clubService.findEntityById(matchRequestDto.getAwayClubId());
        StadiumEntity stadium = stadiumService.findEntityById(matchRequestDto.getStadiumId());

        MatchEntity match = matchMapper.toEntity(matchRequestDto, homeClub, awayClub, stadium);
        match.setUuid(UUID.randomUUID());
        MatchEntity savedMatch = matchRepository.save(match);

        return matchMapper.toDto(savedMatch);
    }

    @Transactional
    public MatchResponseDto update(Long id, MatchRequestDto matchRequestDto) {
        MatchEntity match = findEntityById(id);
        ClubEntity homeClub = clubService.findEntityById(matchRequestDto.getHomeClubId());
        ClubEntity awayClub = clubService.findEntityById(matchRequestDto.getAwayClubId());
        StadiumEntity stadium = stadiumService.findEntityById(matchRequestDto.getStadiumId());

        matchMapper.updateEntityFromDto(matchRequestDto, match);
        match.setHomeClub(homeClub);
        match.setAwayClub(awayClub);
        match.setStadium(stadium);
        MatchEntity updatedMatch = matchRepository.save(match);

        return matchMapper.toDto(updatedMatch);
    }

    public void delete(Long id) {
        MatchEntity match = findEntityById(id);
        matchRepository.delete(match);
    }

    @Transactional
    public MatchResponseDto finish(String matchIdOrUuid, FinishMatchRequestDto dto) {
        MatchEntity match;
        try {
            Long id = Long.valueOf(matchIdOrUuid);
            match = findEntityById(id);
        } catch (NumberFormatException e) {
            try {
                UUID uuid = UUID.fromString(matchIdOrUuid);
                match = findByUuid(uuid);
            } catch (IllegalArgumentException ex) {
                throw new IllegalArgumentException("Invalid match identifier: " + matchIdOrUuid);
            }
        }

        if (match.getStatus() == MatchStatusEnum.FINISHED) {
            throw new BusinessException("Match has already been finished");
        }

        match.setHomeGoals(dto.getHomeGoals());
        match.setAwayGoals(dto.getAwayGoals());
        match.setStatus(MatchStatusEnum.FINISHED);

        matchRepository.save(match);

        LocalDateTime endAt = dto.getEndAt();
        if (endAt == null) {
            endAt = LocalDateTime.now();
        }

        MatchResultMessageDto resultMessage = buildResultMessage(match, endAt);

        matchResultPublisher.sendMatchResult(resultMessage);

        return matchMapper.toDto(match);
    }
    
    @Transactional
    public MatchResponseDto finish(Long matchId, FinishMatchRequestDto dto) {
        return finish(matchId.toString(), dto);
    }

    @Transactional
    public MatchEntity findOrCreateMatch(UUID matchUuid, MatchInfoMessageDto messageDto, ClubEntity homeClub,
                                          ClubEntity awayClub, StadiumEntity stadium) {
        Optional<MatchEntity> optionalMatch = matchRepository.findByUuid(matchUuid);
        MatchEntity match;
        if (optionalMatch.isPresent()) {
            match = optionalMatch.get();
            matchMapper.updateEntityFromMessageDto(messageDto, match, homeClub, awayClub, stadium);
        } else {
            match = matchMapper.fromMessageDto(messageDto, homeClub, awayClub, stadium);
            match.setUuid(matchUuid);
        }
        return matchRepository.save(match);
    }

    private MatchResultMessageDto buildResultMessage(MatchEntity match, LocalDateTime endAt) {
        String[] winner = getWinner(match);
        String winnerId = winner[0];
        String winnerName = winner[1];

        List<ClubScoreDto> clubScores = buildClubScores(match);

        return new MatchResultMessageDto(match.getUuid().toString(), winnerId, winnerName, clubScores, endAt);
    }

    private List<ClubScoreDto> buildClubScores(MatchEntity match) {
        String homeClubUuid = match.getHomeClub().getUuid().toString();
        String awayClubUuid = match.getAwayClub().getUuid().toString();
        String homeClubName = match.getHomeClub().getName();
        String awayClubName = match.getAwayClub().getName();
        int homeGoals = match.getHomeGoals();
        int awayGoals = match.getAwayGoals();

        return List.of(
                new ClubScoreDto(homeClubUuid, homeClubName, homeGoals),
                new ClubScoreDto(awayClubUuid, awayClubName, awayGoals));
    }

    private String[] getWinner(MatchEntity match) {
        int homeGoals = match.getHomeGoals();
        int awayGoals = match.getAwayGoals();
        String winnerId;
        String winnerName;

        if (homeGoals > awayGoals) {
            winnerId = match.getHomeClub().getUuid().toString();
            winnerName = match.getHomeClub().getName();
        } else if (awayGoals > homeGoals) {
            winnerId = match.getAwayClub().getUuid().toString();
            winnerName = match.getAwayClub().getName();
        } else {
            winnerId = "draw";
            winnerName = "draw";
        }
        return new String[]{winnerId, winnerName};
    }
}
