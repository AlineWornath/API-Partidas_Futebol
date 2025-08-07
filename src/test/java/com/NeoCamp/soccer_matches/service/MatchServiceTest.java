package com.neocamp.soccer_matches.service;

import com.neocamp.soccer_matches.dto.match.FinishMatchRequestDto;
import com.neocamp.soccer_matches.dto.match.MatchRequestDto;
import com.neocamp.soccer_matches.dto.match.MatchResponseDto;
import com.neocamp.soccer_matches.entity.ClubEntity;
import com.neocamp.soccer_matches.entity.MatchEntity;
import com.neocamp.soccer_matches.entity.StadiumEntity;
import com.neocamp.soccer_matches.enums.MatchStatusEnum;
import com.neocamp.soccer_matches.exception.BusinessException;
import com.neocamp.soccer_matches.mapper.MatchMapper;
import com.neocamp.soccer_matches.messagingrabbitmq.MatchResultPublisher;
import com.neocamp.soccer_matches.repository.MatchRepository;
import com.neocamp.soccer_matches.testUtils.ClubMockUtils;
import com.neocamp.soccer_matches.testUtils.MatchMockUtils;
import com.neocamp.soccer_matches.testUtils.StadiumMockUtils;
import com.neocamp.soccer_matches.validator.ExistenceValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class MatchServiceTest {

    @Mock
    private MatchRepository matchRepository;

    @Mock
    private MatchMapper matchMapper;

    @Mock
    private ExistenceValidator existenceValidator;

    @Mock
    private ClubService clubService;

    @Mock
    private StadiumService stadiumService;

    @Mock
    private MatchResultPublisher matchResultPublisher;

    @InjectMocks
    private MatchService matchService;

    private Pageable pageable;
    private ClubEntity corinthiansEntity, gremioEntity;
    private StadiumEntity morumbiEntity;
    private MatchEntity flamengoVsCorinthiansAtMaracana, corinthiansVsGremioAtMorumbi;
    private MatchRequestDto corinthiansVsGremioRequestDto;
    private MatchResponseDto flamengoVsCorinthiansResponseDto, corinthiansVsGremioResponseDto;

    @BeforeEach
    public void setUp() {
        pageable = PageRequest.of(0, 10);

        corinthiansEntity = ClubMockUtils.corinthians();
        gremioEntity = ClubMockUtils.gremio();

        morumbiEntity = StadiumMockUtils.morumbi();

        flamengoVsCorinthiansAtMaracana = MatchMockUtils.flamengoVsCorinthiansAtMaracana();
        corinthiansVsGremioAtMorumbi = MatchMockUtils.corinthiansVsGremioAtMorumbi();

        corinthiansVsGremioRequestDto = MatchMockUtils.corinthiansVsGremioAtMorumbiRequestDto();

        flamengoVsCorinthiansResponseDto = MatchMockUtils.flamengoVsCorinthiansAtMaracanaResponseDto();
        corinthiansVsGremioResponseDto = MatchMockUtils.corinthiansVsGremioAtMorumbiResponseDto();
    }

    @Test
    public void shouldListAllMatches_whenAllFiltersAreNull() {
        Page<MatchEntity> matchPage = new PageImpl<>(List.of(flamengoVsCorinthiansAtMaracana, corinthiansVsGremioAtMorumbi));
        Mockito.when(matchRepository.listMatchesByFilters(null, null, null, pageable)).thenReturn(matchPage);
        Mockito.when(matchMapper.toDto(flamengoVsCorinthiansAtMaracana)).thenReturn(flamengoVsCorinthiansResponseDto);
        Mockito.when(matchMapper.toDto(corinthiansVsGremioAtMorumbi)).thenReturn(corinthiansVsGremioResponseDto);

        Page<MatchResponseDto> result = matchService.listMatchesByFilters(null, null, null, pageable);

        Assertions.assertEquals(2, result.getTotalElements());
        Assertions.assertEquals(flamengoVsCorinthiansResponseDto, result.getContent().get(0));
        Assertions.assertEquals(corinthiansVsGremioResponseDto, result.getContent().get(1));
    }

    @Test
    public void shouldListMatchesByClubId() {
        Long clubId = 1L;
        Page<MatchEntity> matchPage = new PageImpl<>(List.of(flamengoVsCorinthiansAtMaracana));
        Mockito.doNothing().when(existenceValidator).validateClubExists(clubId);
        Mockito.when(matchRepository.listMatchesByFilters(clubId, null, null, pageable)).thenReturn(matchPage);
        Mockito.when(matchMapper.toDto(flamengoVsCorinthiansAtMaracana)).thenReturn(flamengoVsCorinthiansResponseDto);

        Page<MatchResponseDto> result = matchService.listMatchesByFilters(clubId, null, null, pageable);

        Assertions.assertEquals(1, result.getTotalElements());
        Assertions.assertEquals(flamengoVsCorinthiansResponseDto, result.getContent().getFirst());
    }

    @Test
    public void shouldListMatchesByStadiumId() {
        Long stadiumId = 1L;
        Page<MatchEntity> matchPage = new PageImpl<>(List.of(flamengoVsCorinthiansAtMaracana));
        Mockito.doNothing().when(existenceValidator).validateStadiumExists(stadiumId);
        Mockito.when(matchRepository.listMatchesByFilters(null, stadiumId, null, pageable)).thenReturn(matchPage);
        Mockito.when(matchMapper.toDto(flamengoVsCorinthiansAtMaracana)).thenReturn(flamengoVsCorinthiansResponseDto);

        Page<MatchResponseDto> result = matchService.listMatchesByFilters(null, stadiumId, null, pageable);

        Assertions.assertEquals(1, result.getTotalElements());
        Assertions.assertEquals(flamengoVsCorinthiansResponseDto, result.getContent().getFirst());
    }

    @Test
    public void shouldListMatchesByClubIdAndStadiumId() {
        Long clubId = 1L;
        Long stadiumId = 1L;
        Page<MatchEntity> matchPage = new PageImpl<>(List.of(flamengoVsCorinthiansAtMaracana));
        Mockito.doNothing().when(existenceValidator).validateClubExists(clubId);
        Mockito.doNothing().when(existenceValidator).validateStadiumExists(stadiumId);
        Mockito.when(matchRepository.listMatchesByFilters(clubId, stadiumId, null, pageable)).thenReturn(matchPage);
        Mockito.when(matchMapper.toDto(flamengoVsCorinthiansAtMaracana)).thenReturn(flamengoVsCorinthiansResponseDto);

        Page<MatchResponseDto> result = matchService.listMatchesByFilters(clubId, stadiumId, null, pageable);

        Assertions.assertEquals(1, result.getTotalElements());
        Assertions.assertEquals(flamengoVsCorinthiansResponseDto, result.getContent().getFirst());
    }

    @Test
    public void shouldReturnEmptyPage_whenFiltersDoNotMatchAnyGame() {
        Long clubId = 999L;
        Page<MatchEntity> emptyPage = Page.empty();
        Mockito.doNothing().when(existenceValidator).validateClubExists(clubId);
        Mockito.when(matchRepository.listMatchesByFilters(clubId, null, null, pageable)).thenReturn(emptyPage);

        Page<MatchResponseDto> result = matchService.listMatchesByFilters(clubId, null, null, pageable);

        Assertions.assertEquals(0, result.getTotalElements());
    }

    @Test
    public void shouldReturnMatchDtoByIdSuccessfully() {
        Long matchId = 1L;
        Mockito.when(matchRepository.findById(matchId)).thenReturn(Optional.of(flamengoVsCorinthiansAtMaracana));
        Mockito.when(matchMapper.toDto(flamengoVsCorinthiansAtMaracana)).thenReturn(flamengoVsCorinthiansResponseDto);

        MatchResponseDto result = matchService.findById(matchId);

        Assertions.assertEquals(flamengoVsCorinthiansResponseDto, result);
    }

    @Test
    public void shouldThrowException_whenFindByIdWithInvalidId() {
        Long invalidId = 999L;
        Mockito.when(matchRepository.findById(invalidId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = Assertions.assertThrows(EntityNotFoundException.class, () ->
            matchService.findById(invalidId));

        Assertions.assertTrue(exception.getMessage().contains("Match not found"));
    }

    @Test
    public void shouldReturnMatchEntityByIdSuccessfully() {
        Long matchId = 1L;
        Mockito.when(matchRepository.findById(matchId)).thenReturn(Optional.of(flamengoVsCorinthiansAtMaracana));

        MatchEntity result = matchService.findEntityById(matchId);

        Assertions.assertEquals(flamengoVsCorinthiansAtMaracana, result);
    }

    @Test
    public void shouldThrowException_whenFindEntityByIdInvalidId() {
        Long invalidId = 999L;
        Mockito.when(matchRepository.findById(invalidId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = Assertions.assertThrows(EntityNotFoundException.class, () ->
            matchService.findEntityById(invalidId));

        Assertions.assertTrue(exception.getMessage().contains("Match not found"));
    }

    @Test
    public void shouldSaveMatchSuccessfully() {
        Mockito.when(clubService.findEntityById(corinthiansVsGremioRequestDto.getHomeClubId())).thenReturn(corinthiansEntity);
        Mockito.when(clubService.findEntityById(corinthiansVsGremioRequestDto.getAwayClubId())).thenReturn(gremioEntity);
        Mockito.when(stadiumService.findEntityById(corinthiansVsGremioRequestDto.getStadiumId())).thenReturn(morumbiEntity);

        Mockito.when(matchMapper.toEntity(corinthiansVsGremioRequestDto, corinthiansEntity, gremioEntity, morumbiEntity)).
                thenReturn(corinthiansVsGremioAtMorumbi);
        Mockito.when(matchRepository.save(corinthiansVsGremioAtMorumbi)).thenReturn(corinthiansVsGremioAtMorumbi);
        Mockito.when(matchMapper.toDto(corinthiansVsGremioAtMorumbi)).thenReturn(corinthiansVsGremioResponseDto);

        MatchEntity entity = matchService.assembleMatchFromRequestDto(corinthiansVsGremioRequestDto);
        MatchResponseDto result = matchService.save(entity);

        Assertions.assertEquals(corinthiansVsGremioResponseDto, result);
        Mockito.verify(matchRepository).save(corinthiansVsGremioAtMorumbi);
    }

    @Test
    public void shouldThrowException_whenSaveMatchWithUnknownHomeClub() {
        Long unknownClubId = 999L;
        corinthiansVsGremioRequestDto.setHomeClubId(unknownClubId);

        Mockito.when(clubService.findEntityById(unknownClubId)).thenThrow(new EntityNotFoundException("Club not found"));

        EntityNotFoundException exception = Assertions.assertThrows(EntityNotFoundException.class, () -> {
                MatchEntity entity = matchService.assembleMatchFromRequestDto(corinthiansVsGremioRequestDto);
                matchService.save(entity);
            });

        Assertions.assertTrue(exception.getMessage().contains("Club not found"));
    }

    @Test
    public void shouldUpdateMatchSuccessfully() {
        Long matchId = 1L;
        Mockito.when(matchRepository.findById(matchId)).thenReturn(Optional.of(flamengoVsCorinthiansAtMaracana));
        Mockito.when(clubService.findEntityById(corinthiansVsGremioRequestDto.getHomeClubId())).thenReturn(corinthiansEntity);
        Mockito.when(clubService.findEntityById(corinthiansVsGremioRequestDto.getAwayClubId())).thenReturn(gremioEntity);
        Mockito.when(stadiumService.findEntityById(corinthiansVsGremioRequestDto.getStadiumId())).thenReturn(morumbiEntity);
        Mockito.when(matchRepository.save(flamengoVsCorinthiansAtMaracana)).thenReturn(flamengoVsCorinthiansAtMaracana);
        Mockito.when(matchMapper.toDto(flamengoVsCorinthiansAtMaracana)).thenReturn(flamengoVsCorinthiansResponseDto);

        MatchResponseDto result = matchService.updateFromRequestDto(matchId, corinthiansVsGremioRequestDto);

        Assertions.assertEquals(flamengoVsCorinthiansResponseDto, result);
        Mockito.verify(matchMapper).updateEntityFromDto(corinthiansVsGremioRequestDto, flamengoVsCorinthiansAtMaracana);
    }

    @Test
    public void shouldDeleteMatchSuccessfully() {
        Long matchId = 1L;
        Mockito.when(matchRepository.findById(matchId)).thenReturn(Optional.of(flamengoVsCorinthiansAtMaracana));

        matchService.delete(matchId);

        Mockito.verify(matchRepository).delete(flamengoVsCorinthiansAtMaracana);
    }

    @Test
    public void shouldFinishMatchSuccessfully() {
        Long matchId = 1L;
        int homeGoals = 2;
        int awayGoals = 1;
        LocalDateTime endAt = LocalDateTime.now();
        FinishMatchRequestDto dto = new FinishMatchRequestDto(homeGoals, awayGoals, endAt);

        MatchEntity match = flamengoVsCorinthiansAtMaracana;
        match.setStatus(MatchStatusEnum.IN_PROGRESS);

        Mockito.when(matchRepository.findById(matchId)).thenReturn(Optional.of(match));
        Mockito.when(matchRepository.save(match)).thenReturn(match);
        Mockito.when(matchMapper.toDto(match)).thenReturn(flamengoVsCorinthiansResponseDto);

        MatchResponseDto result = matchService.finish(matchId, dto);

        Assertions.assertEquals(flamengoVsCorinthiansResponseDto, result);
        Assertions.assertEquals(MatchStatusEnum.FINISHED, match.getStatus());
        Assertions.assertEquals(homeGoals, match.getHomeGoals());
        Assertions.assertEquals(awayGoals, match.getAwayGoals());
        Mockito.verify(matchResultPublisher).sendMatchResult(Mockito.any());
    }

    @Test
    public void shouldThrowException_whenFinishMatchWithStatusAlreadyFinished() {
        Long matchId = 1L;
        int homeGoals = 2;
        int awayGoals = 1;
        LocalDateTime endAt = LocalDateTime.now();
        FinishMatchRequestDto dto = new FinishMatchRequestDto(homeGoals, awayGoals, endAt);

        MatchEntity match = flamengoVsCorinthiansAtMaracana;
        match.setStatus(MatchStatusEnum.FINISHED);

        Mockito.when(matchRepository.findById(matchId)).thenReturn(Optional.of(match));

        BusinessException exception = Assertions.assertThrows(BusinessException.class, () ->
            matchService.finish(matchId, dto));

        Assertions.assertTrue(exception.getMessage().contains("Match has already been finished"));
    }
}
