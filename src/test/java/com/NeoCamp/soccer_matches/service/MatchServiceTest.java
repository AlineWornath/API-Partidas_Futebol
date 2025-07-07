package com.neocamp.soccer_matches.service;

import com.neocamp.soccer_matches.dto.club.ClubResponseDto;
import com.neocamp.soccer_matches.dto.match.MatchRequestDto;
import com.neocamp.soccer_matches.dto.match.MatchResponseDto;
import com.neocamp.soccer_matches.dto.stadium.StadiumResponseDto;
import com.neocamp.soccer_matches.entity.ClubEntity;
import com.neocamp.soccer_matches.entity.MatchEntity;
import com.neocamp.soccer_matches.entity.StadiumEntity;
import com.neocamp.soccer_matches.mapper.MatchMapper;
import com.neocamp.soccer_matches.repository.MatchRepository;
import com.neocamp.soccer_matches.testUtils.ClubMockUtils;
import com.neocamp.soccer_matches.testUtils.MatchMockUtils;
import com.neocamp.soccer_matches.testUtils.StadiumMockUtils;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class MatchServiceTest {
    @Mock
    private MatchRepository matchRepository;

    @Mock
    private ClubService clubService;

    @Mock
    private StadiumService stadiumService;

    @Mock
    private MatchMapper matchMapper;

    @InjectMocks
    private MatchService matchService;

    private Pageable pageable;
    private ClubEntity corinthiansEntity, flamengoEntity, gremioEntity;
    private ClubResponseDto flamengoResponseDto, gremioResponseDto;
    private StadiumEntity maracanaEntity, morumbiEntity;
    private StadiumResponseDto morumbiResponseDto;
    private MatchEntity flamengoVsCorinthiansAtMaracana, corinthiansVsGremioAtMorumbi;
    private MatchRequestDto flamengoVsCorinthiansRequestDto, corinthiansVsGremioRequestDto;
    private MatchResponseDto flamengoVsCorinthiansResponseDto, corinthiansVsGremioResponseDto;

    @BeforeEach
    public void setUp() {
        pageable = PageRequest.of(0, 10);

        corinthiansEntity = ClubMockUtils.corinthians();
        flamengoEntity = ClubMockUtils.flamengo();
        gremioEntity = ClubMockUtils.gremio();

        flamengoResponseDto = ClubMockUtils.flamengoResponseDto();
        gremioResponseDto = ClubMockUtils.gremioResponseDto();

        maracanaEntity = StadiumMockUtils.maracana();
        morumbiEntity = StadiumMockUtils.morumbi();

        morumbiResponseDto = StadiumMockUtils.morumbiResponseDto();

        flamengoVsCorinthiansAtMaracana = MatchMockUtils.flamengoVsCorinthiansAtMaracana();
        corinthiansVsGremioAtMorumbi = MatchMockUtils.corinthiansVsGremioAtMorumbi();

        flamengoVsCorinthiansRequestDto = MatchMockUtils.flamengoVsCorinthiansAtMaracanaRequestDto();
        corinthiansVsGremioRequestDto = MatchMockUtils.corinthiansVsGremioAtMorumbiRequestDto();

        flamengoVsCorinthiansResponseDto = MatchMockUtils.flamengoVsCorinthiansAtMaracanaResponseDto();
        corinthiansVsGremioResponseDto = MatchMockUtils.corinthiansVsGremioAtMorumbiResponseDto();
    }

    @Test
    public void shouldListAllMatches_whenAllFiltersAreNull() {
        Page<MatchEntity> matches = new PageImpl<>(List.of(
                flamengoVsCorinthiansAtMaracana, corinthiansVsGremioAtMorumbi));

        Mockito.when(matchRepository.listMatchesByFilters(null, null, null, pageable))
                .thenReturn(matches);
        Mockito.when(matchMapper.toDto(flamengoVsCorinthiansAtMaracana)).thenReturn(flamengoVsCorinthiansResponseDto);

        Page<MatchResponseDto> result = matchService.listMatchesByFilters(null, null,
                null, pageable);

        Assertions.assertEquals(2,  result.getTotalElements());
        Assertions.assertEquals("Flamengo", result.getContent().getFirst().getHomeClub().getName());
        Assertions.assertEquals("Corinthians", result.getContent().getFirst().getAwayClub().getName());
    }

    @Test
    public void shouldListMatchesByClubId(){
        Long flamengoId = flamengoEntity.getId();

        Page<MatchEntity> matches = new PageImpl<>(List.of(flamengoVsCorinthiansAtMaracana));

        Mockito.when(clubService.findEntityById(flamengoId)).thenReturn(flamengoEntity);
        Mockito.when(matchRepository.listMatchesByFilters(flamengoId, null, null, pageable))
                .thenReturn(matches);
        Mockito.when(matchMapper.toDto(flamengoVsCorinthiansAtMaracana)).thenReturn(flamengoVsCorinthiansResponseDto);

        Page<MatchResponseDto> result = matchService.listMatchesByFilters(flamengoId, null,
                null, pageable);

        Assertions.assertEquals(1,  result.getTotalElements());
        Assertions.assertEquals("Flamengo", result.getContent().getFirst().getHomeClub().getName());
        Assertions.assertEquals("Corinthians", result.getContent().getFirst().getAwayClub().getName());
    }

    @Test
    public void shouldListMatchesByStadiumId(){
        Long morumbiId = morumbiEntity.getId();

        Page<MatchEntity> matches = new PageImpl<>(List.of(corinthiansVsGremioAtMorumbi));

        Mockito.when(stadiumService.findEntityById(morumbiId)).thenReturn(morumbiEntity);
        Mockito.when(matchRepository.listMatchesByFilters(null, morumbiId,null, pageable))
                .thenReturn(matches);
        Mockito.when(matchMapper.toDto(corinthiansVsGremioAtMorumbi)).thenReturn(corinthiansVsGremioResponseDto);

        Page<MatchResponseDto> result = matchService.listMatchesByFilters(null, morumbiId, null, pageable);

        Assertions.assertEquals(1,  result.getTotalElements());
        Assertions.assertEquals("Corinthians", result.getContent().getFirst().getHomeClub().getName());
        Assertions.assertEquals("Grêmio", result.getContent().getFirst().getAwayClub().getName());
    }

    @Test
    public void shouldListMatchesByClubIdAndStadiumId(){
        Long corinthiansId = corinthiansEntity.getId();
        Long maracanaId = maracanaEntity.getId();

        Page<MatchEntity> matches = new PageImpl<>(List.of(flamengoVsCorinthiansAtMaracana));

        Mockito.when(clubService.findEntityById(corinthiansId)).thenReturn(corinthiansEntity);
        Mockito.when(stadiumService.findEntityById(maracanaId)).thenReturn(maracanaEntity);
        Mockito.when(matchRepository.listMatchesByFilters(corinthiansId, maracanaId, null, pageable)).
                thenReturn(matches);
        Mockito.when(matchMapper.toDto(flamengoVsCorinthiansAtMaracana)).thenReturn(flamengoVsCorinthiansResponseDto);

        Page<MatchResponseDto> result = matchService.listMatchesByFilters(corinthiansId, maracanaId,
                null, pageable);

        Assertions.assertEquals(1,  result.getTotalElements());
        Assertions.assertEquals("Flamengo", result.getContent().getFirst().getHomeClub().getName());
        Assertions.assertEquals("Corinthians", result.getContent().getFirst().getAwayClub().getName());
        Assertions.assertEquals("Maracanã", result.getContent().getFirst().getStadium().getName());
    }

    @Test
    public void shouldReturnEmptyPage_whenFiltersDoNotMatchAnyGame(){
        Long clubId = -2L;
        Long stadiumId = -25L;

        Page<MatchEntity> emptyPage = Page.empty(pageable);

        Mockito.when(clubService.findEntityById(clubId)).thenReturn(null);
        Mockito.when(stadiumService.findEntityById(stadiumId)).thenReturn(null);
        Mockito.when(matchRepository.listMatchesByFilters(null, null,null, pageable))
                .thenReturn(emptyPage);
        Mockito.when(matchRepository.listMatchesByFilters(clubId, stadiumId, null, pageable)).thenReturn(emptyPage);

        Page<MatchResponseDto> result = matchService.listMatchesByFilters(clubId, stadiumId, null, pageable);

        Assertions.assertEquals(0, result.getTotalElements());
    }

    @Test
    public void shouldReturnMatchDtoByIdSuccessfully(){
        corinthiansVsGremioAtMorumbi.setId(2L);
        corinthiansVsGremioResponseDto.setId(2L);

        Mockito.when(matchRepository.findById(2L)).thenReturn(Optional.of(corinthiansVsGremioAtMorumbi));
        Mockito.when(matchMapper.toDto(corinthiansVsGremioAtMorumbi)).thenReturn(corinthiansVsGremioResponseDto);

        MatchResponseDto result = matchService.findById(2L);

        Assertions.assertEquals("Corinthians", result.getHomeClub().getName());
        Assertions.assertEquals("Grêmio", result.getAwayClub().getName());
        Assertions.assertEquals("Morumbi", result.getStadium().getName());
        Assertions.assertEquals(2L, result.getId());
    }

    @Test
    public void shouldThrowException_whenFindByIdWithInvalidId(){
        Long invalidId = -2L;

        Mockito.when(matchRepository.findById(invalidId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = Assertions.assertThrows(EntityNotFoundException.class,
                () -> matchService.findById(invalidId));

        Assertions.assertTrue(exception.getMessage().contains("Match not found: "));
    }

    @Test
    public void shouldReturnMatchEntityByIdSuccessfully(){
        flamengoVsCorinthiansAtMaracana.setId(1L);

        Mockito.when(matchRepository.findById(1L)).thenReturn(Optional.of(flamengoVsCorinthiansAtMaracana));

        MatchEntity result = matchService.findEntityById(1L);

        Assertions.assertEquals("Flamengo", result.getHomeClub().getName());
        Assertions.assertEquals("Corinthians", result.getAwayClub().getName());
        Assertions.assertEquals("Maracanã", result.getStadium().getName());
        Assertions.assertEquals(1L, result.getId());
    }

    @Test
    public void shouldThrowException_whenFindEntityByIdInvalidId(){
        Long invalidId = -10L;

        Mockito.when(matchRepository.findById(invalidId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = Assertions.assertThrows(EntityNotFoundException.class,
                () -> matchService.findEntityById(invalidId));

        Assertions.assertTrue(exception.getMessage().contains("Match not found: "));
    }

    @Test
    public void shouldSaveMatchSuccessfully(){
        Long homeClubId = corinthiansVsGremioRequestDto.getHomeClubId();
        Long awayClubId = corinthiansVsGremioRequestDto.getAwayClubId();
        Long stadiumId = corinthiansVsGremioRequestDto.getStadiumId();

        Mockito.when(clubService.findEntityById(homeClubId)).thenReturn(corinthiansEntity);
        Mockito.when(clubService.findEntityById(awayClubId)).thenReturn(gremioEntity);
        Mockito.when(stadiumService.findEntityById(stadiumId)).thenReturn(morumbiEntity);
        Mockito.when(matchMapper.toEntity(corinthiansVsGremioRequestDto, corinthiansEntity,
                gremioEntity, morumbiEntity)).thenReturn(corinthiansVsGremioAtMorumbi);
        Mockito.when(matchRepository.save(corinthiansVsGremioAtMorumbi)).thenReturn(corinthiansVsGremioAtMorumbi);
        Mockito.when(matchMapper.toDto(corinthiansVsGremioAtMorumbi)).thenReturn(corinthiansVsGremioResponseDto);

        MatchResponseDto result = matchService.save(corinthiansVsGremioRequestDto);

        Assertions.assertEquals("Corinthians", result.getHomeClub().getName());
        Assertions.assertEquals("Grêmio", result.getAwayClub().getName());
        Assertions.assertEquals("Morumbi", result.getStadium().getName());
    }

    @Test
    public void shouldUpdateMatchSuccessfully(){
        Long existingMatchId = 1L;

        MatchEntity existingMatch = flamengoVsCorinthiansAtMaracana;
        existingMatch.setId(existingMatchId);

        MatchRequestDto updateRequest = flamengoVsCorinthiansRequestDto;
        updateRequest.setHomeClubId(gremioEntity.getId());
        updateRequest.setAwayClubId(flamengoEntity.getId());
        updateRequest.setStadiumId(morumbiEntity.getId());

        MatchResponseDto updatedResponse = flamengoVsCorinthiansResponseDto;
        updatedResponse.setHomeClub(gremioResponseDto);
        updatedResponse.setAwayClub(flamengoResponseDto);
        updatedResponse.setStadium(morumbiResponseDto);

        Long homeClubId = updateRequest.getHomeClubId();
        Long awayClubId = updateRequest.getAwayClubId();
        Long stadiumId = updateRequest.getStadiumId();

        Mockito.when(matchRepository.findById(1L)).thenReturn(Optional.of(existingMatch));
        Mockito.when(clubService.findEntityById(homeClubId)).thenReturn(gremioEntity);
        Mockito.when(clubService.findEntityById(awayClubId)).thenReturn(flamengoEntity);
        Mockito.when(stadiumService.findEntityById(stadiumId)).thenReturn(morumbiEntity);
        Mockito.when(matchRepository.save(existingMatch)).thenReturn(existingMatch);
        Mockito.when(matchMapper.toDto(existingMatch)).thenReturn(updatedResponse);

        MatchResponseDto result = matchService.update(existingMatchId, updateRequest);

        Assertions.assertEquals("Grêmio", result.getHomeClub().getName());
        Assertions.assertEquals("Flamengo", result.getAwayClub().getName());
        Assertions.assertEquals("Morumbi", result.getStadium().getName());
    }

    @Test
    public void shouldDeleteMatchSuccessfully(){
        Long existingMatchId = 1L;

        MatchEntity existingMatch = corinthiansVsGremioAtMorumbi;
        existingMatch.setId(existingMatchId);

        Mockito.when(matchRepository.findById(existingMatchId)).thenReturn(Optional.of(existingMatch));

        matchService.delete(existingMatchId);

        Mockito.verify(matchRepository, Mockito.times(1)).delete(existingMatch);
    }
}
