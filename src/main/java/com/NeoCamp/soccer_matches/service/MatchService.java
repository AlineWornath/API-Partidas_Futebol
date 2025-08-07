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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.neocamp.soccer_matches.utils.UuidUtils.parseUuid;

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

    @Cacheable(value = "matchById", key = "#id")
    public MatchResponseDto findById(Long id) {
        MatchEntity match = matchRepository.findById(id).
                orElseThrow(() -> new EntityNotFoundException("Match not found: " + id));
        return matchMapper.toDto(match);
    }

    public MatchEntity findEntityById(Long Id) {
        return matchRepository.findById(Id).
                orElseThrow(() -> new EntityNotFoundException("Match not found: " + Id));
    }

    public MatchEntity findByUuidOrThrow(UUID uuid) {
        return matchRepository.findByUuid(uuid).orElseThrow(
                () -> new EntityNotFoundException("Match not found: " + uuid));
    }

    public Optional<MatchEntity> findByUuid(UUID uuid) {
        return matchRepository.findByUuid(uuid);
    }

    @Transactional
    public MatchResponseDto save(MatchEntity match) {
        MatchEntity savedMatch = matchRepository.save(match);
        return matchMapper.toDto(savedMatch);
    }

    @Transactional
    @CacheEvict(value = "matchById", key = "#id")
    public MatchResponseDto updateFromRequestDto(Long id, MatchRequestDto dto) {
        MatchEntity existingMatch = findEntityById(id);

       ClubEntity homeClub = clubService.findEntityById(dto.getHomeClubId());
       ClubEntity awayClub = clubService.findEntityById(dto.getAwayClubId());
       StadiumEntity stadium = stadiumService.findEntityById(dto.getStadiumId());

        matchMapper.updateEntityFromDto(dto, existingMatch);
        existingMatch.setHomeClub(homeClub);
        existingMatch.setAwayClub(awayClub);
        existingMatch.setStadium(stadium);

        return matchMapper.toDto(matchRepository.save(existingMatch));
    }

    @Transactional
    @CacheEvict(value = "matchById", allEntries = true)
    public void updateFromMessageDto(String matchUuid, MatchInfoMessageDto dto) {
        UUID uuid = parseUuid(matchUuid, "matchUuid");
        MatchEntity existingMatch = findByUuidOrThrow(uuid);

        ClubEntity homeClub = clubService.findByUuid(parseUuid(dto.getHomeClubUuid(), "homeClubUuid"));
        ClubEntity awayClub = clubService.findByUuid(parseUuid(dto.getAwayClubUuid(), "awayClubUuid"));
        StadiumEntity stadium = stadiumService.findByUuid(parseUuid(dto.getStadiumUuid(), "stadiumUuid"));

        matchMapper.updateEntityFromMessageDto(dto, existingMatch, homeClub, awayClub, stadium);
        existingMatch.setHomeClub(homeClub);
        existingMatch.setAwayClub(awayClub);
        existingMatch.setStadium(stadium);
        matchRepository.save(existingMatch);
    }

    @Caching(evict = {
            @CacheEvict(value = "matches", allEntries = true),
            @CacheEvict(value = "matchById", key = "#id")
    })
    public void delete(Long id) {
        MatchEntity match = findEntityById(id);
        matchRepository.delete(match);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "matches", allEntries = true),
            @CacheEvict(value = "matchById", key = "#matchIdOrUuid")
    })
    public MatchResponseDto finish(String matchIdOrUuid, FinishMatchRequestDto dto) {
        MatchEntity match;
        try {
            Long id = Long.valueOf(matchIdOrUuid);
            match = findEntityById(id);
        } catch (NumberFormatException e) {
            try {
                UUID uuid = UUID.fromString(matchIdOrUuid);
                match = findByUuidOrThrow(uuid);
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

    public MatchEntity assembleMatchFromRequestDto(MatchRequestDto matchRequestDto) {
        ClubEntity homeClub = clubService.findEntityById(matchRequestDto.getHomeClubId());
        ClubEntity awayClub = clubService.findEntityById(matchRequestDto.getAwayClubId());
        StadiumEntity stadium = stadiumService.findEntityById(matchRequestDto.getStadiumId());

        return matchMapper.toEntity(matchRequestDto, homeClub, awayClub, stadium);
    }

    public MatchEntity assembleMatchFromInfoMessageDto(MatchInfoMessageDto matchInfoMessageDto) {
        UUID homeClubUuid = parseUuid(matchInfoMessageDto.getHomeClubUuid(), "homeClubUuid");
        UUID awayClubUuid = parseUuid(matchInfoMessageDto.getAwayClubUuid(), "awayClubUuid");
        UUID stadiumUuid = parseUuid(matchInfoMessageDto.getStadiumUuid(), "stadiumUuid");

        ClubEntity homeClub = clubService.findByUuid(homeClubUuid);
        ClubEntity awayClub = clubService.findByUuid(awayClubUuid);
        StadiumEntity stadium = stadiumService.findByUuid(stadiumUuid);

        return matchMapper.fromMessageDto(matchInfoMessageDto, homeClub, awayClub, stadium);
    }

    private MatchResultMessageDto buildResultMessage(MatchEntity existingMatch, LocalDateTime endAt) {
        String[] winner = getWinner(existingMatch);
        String winnerId = winner[0];
        String winnerName = winner[1];

        List<ClubScoreDto> clubScores = buildClubScores(existingMatch);

        return new MatchResultMessageDto(existingMatch.getUuid().toString(), winnerId, winnerName, clubScores, endAt);
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
