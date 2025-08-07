package com.neocamp.soccer_matches.service;

import com.neocamp.soccer_matches.dto.club.*;
import com.neocamp.soccer_matches.dto.match.HeadToHeadResponseDto;
import com.neocamp.soccer_matches.entity.ClubEntity;
import com.neocamp.soccer_matches.entity.StateEntity;
import com.neocamp.soccer_matches.enums.RankingOrderEnum;
import com.neocamp.soccer_matches.enums.MatchFilterEnum;
import com.neocamp.soccer_matches.enums.StateCodeEnum;
import com.neocamp.soccer_matches.exception.BusinessException;
import com.neocamp.soccer_matches.mapper.ClubMapper;
import com.neocamp.soccer_matches.repository.ClubRepository;
import com.neocamp.soccer_matches.validator.ExistenceValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClubService {
    private final ClubRepository clubRepository;
    private final ClubMapper clubMapper;
    private final StateService stateService;
    private final ExistenceValidator existenceValidator;
    private final ClubStatsService clubStatsService;

    public Page<ClubResponseDto> listClubsByFilters(String name, StateCodeEnum stateCode, Boolean active,
                                                    Pageable pageable) {
        Page<ClubEntity> clubs = clubRepository.listClubsByFilters(name, stateCode, active, pageable);
        return clubs.map(clubMapper::toDto);
    }

    @Cacheable(value = "clubById", key = "#id")
    public ClubResponseDto findById(Long id) {
        ClubEntity club = clubRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Club not found: " + id));
        return clubMapper.toDto(club);
    }

    public ClubEntity findEntityById(Long id) {
        return clubRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Club not found: " + id));
    }

    public ClubEntity findByUuid(UUID uuid) {
        return clubRepository.findByUuid(uuid).orElseThrow(
                () -> new EntityNotFoundException("Club not found: " + uuid));
    }

    public ClubStatsResponseDto getClubStats(Long id, MatchFilterEnum filter) {
        existenceValidator.validateClubExists(id);

        if (MatchFilterEnum.ROUT.equals(filter)) {
            throw new BusinessException("Filter rout is not supported for this endpoint");
            }
        return clubStatsService.getClubStats(id, filter);
    }

    public List<ClubVersusClubStatsDto> getClubVersusOpponentsStats(Long id, MatchFilterEnum filter) {
        existenceValidator.validateClubExists(id);

        if (MatchFilterEnum.ROUT.equals(filter)) {
            throw new BusinessException("Filter rout is not supported for this endpoint");
        }
        return clubStatsService.getClubVersusOpponentsStats(id, filter);
    }

    public HeadToHeadResponseDto getHeadToHeadStats(Long clubId, Long opponentId, MatchFilterEnum filter) {
        existenceValidator.validateClubExists(clubId);
        existenceValidator.validateClubExists(opponentId);

        if (clubId.equals(opponentId)) {
            throw new BusinessException("Head-to-head comparison requires two different clubs");
        }
        return clubStatsService.getHeadToHeadStats(clubId, opponentId, filter);
    }

    public List<ClubRankingDto> getClubRanking(RankingOrderEnum rankingOrder) {
        return clubStatsService.getClubRanking(rankingOrder);
    }

    public ClubResponseDto save(ClubRequestDto clubRequestDto) {
        StateCodeEnum stateEnum = existenceValidator.validateStateCode(clubRequestDto.getStateCode());
        StateEntity homeState = stateService.findByCode(stateEnum);

        ClubEntity club = clubMapper.toEntity(clubRequestDto, homeState);
        club.setUuid(UUID.randomUUID());
        ClubEntity savedClub = clubRepository.save(club);

        return clubMapper.toDto(savedClub);
    }

    @CacheEvict(value = "clubById", key = "#id")
    public ClubResponseDto update(Long id, ClubRequestDto clubRequestDto) {
        ClubEntity club = findEntityById(id);

        StateCodeEnum stateEnum = existenceValidator.validateStateCode(clubRequestDto.getStateCode());
        StateEntity state = stateService.findByCode(stateEnum);
        club.setHomeState(state);

        clubMapper.updateEntityFromDto(clubRequestDto, club);

        ClubEntity updatedClub = clubRepository.save(club);
        return clubMapper.toDto(updatedClub);
    }

    @CacheEvict(value = "clubById", key = "#id")
    public void delete(Long id) {
        ClubEntity club = findEntityById(id);
        club.setActive(false);
        clubRepository.save(club);
    }
}
