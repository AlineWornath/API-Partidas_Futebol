package com.neocamp.soccer_matches.service;

import com.neocamp.soccer_matches.dto.club.ClubRankingDto;
import com.neocamp.soccer_matches.dto.club.ClubVersusClubStatsDto;
import com.neocamp.soccer_matches.dto.match.HeadToHeadResponseDto;
import com.neocamp.soccer_matches.dto.match.MatchRequestDto;
import com.neocamp.soccer_matches.dto.match.MatchResponseDto;
import com.neocamp.soccer_matches.entity.ClubEntity;
import com.neocamp.soccer_matches.entity.MatchEntity;
import com.neocamp.soccer_matches.entity.StadiumEntity;
import com.neocamp.soccer_matches.enums.MatchFilterEnum;
import com.neocamp.soccer_matches.enums.RankingOrderEnum;
import com.neocamp.soccer_matches.mapper.MatchMapper;
import com.neocamp.soccer_matches.repository.ClubRepository;
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
    private MatchMapper matchMapper;

    @Mock
    private ExistenceValidator existenceValidator;

    @Mock
    private ClubRepository clubRepository;

    @Mock
    private StadiumService stadiumService;

    @InjectMocks
    private MatchService matchService;

    private Pageable pageable;
    private ClubEntity corinthiansEntity, flamengoEntity, gremioEntity;
    private StadiumEntity maracanaEntity, morumbiEntity;
    private MatchEntity flamengoVsCorinthiansAtMaracana, corinthiansVsGremioAtMorumbi;
    private MatchRequestDto corinthiansVsGremioRequestDto;
    private MatchResponseDto flamengoVsCorinthiansResponseDto, corinthiansVsGremioResponseDto;

    @BeforeEach
    public void setUp() {
        pageable = PageRequest.of(0, 10);

        corinthiansEntity = ClubMockUtils.corinthians();
        flamengoEntity = ClubMockUtils.flamengo();
        gremioEntity = ClubMockUtils.gremio();

        maracanaEntity = StadiumMockUtils.maracana();
        morumbiEntity = StadiumMockUtils.morumbi();

        flamengoVsCorinthiansAtMaracana = MatchMockUtils.flamengoVsCorinthiansAtMaracana();
        corinthiansVsGremioAtMorumbi = MatchMockUtils.corinthiansVsGremioAtMorumbi();

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
        Assertions.assertEquals("Flamengo", result.getContent().getFirst().getHomeClubName());
        Assertions.assertEquals("Corinthians", result.getContent().getFirst().getAwayClubName());
    }

    @Test
    public void shouldListMatchesByClubId(){
        Long flamengoId = flamengoEntity.getId();

        Page<MatchEntity> matches = new PageImpl<>(List.of(flamengoVsCorinthiansAtMaracana));

        Mockito.doNothing().when(existenceValidator).validateClubExists(flamengoId);
        Mockito.when(matchRepository.listMatchesByFilters(flamengoId, null, null, pageable))
                .thenReturn(matches);
        Mockito.when(matchMapper.toDto(flamengoVsCorinthiansAtMaracana)).thenReturn(flamengoVsCorinthiansResponseDto);

        Page<MatchResponseDto> result = matchService.listMatchesByFilters(flamengoId, null,
                null, pageable);

        Assertions.assertEquals(1,  result.getTotalElements());
        Assertions.assertEquals("Flamengo", result.getContent().getFirst().getHomeClubName());
        Assertions.assertEquals("Corinthians", result.getContent().getFirst().getAwayClubName());
    }

    @Test
    public void shouldListMatchesByStadiumId(){
        Long morumbiId = morumbiEntity.getId();

        Page<MatchEntity> matches = new PageImpl<>(List.of(corinthiansVsGremioAtMorumbi));

        Mockito.doNothing().when(existenceValidator).validateStadiumExists(morumbiId);
        Mockito.when(matchRepository.listMatchesByFilters(null, morumbiId,null, pageable))
                .thenReturn(matches);
        Mockito.when(matchMapper.toDto(corinthiansVsGremioAtMorumbi)).thenReturn(corinthiansVsGremioResponseDto);

        Page<MatchResponseDto> result = matchService.listMatchesByFilters(null, morumbiId, null, pageable);

        Assertions.assertEquals(1,  result.getTotalElements());
        Assertions.assertEquals("Corinthians", result.getContent().getFirst().getHomeClubName());
        Assertions.assertEquals("Grêmio", result.getContent().getFirst().getAwayClubName());
    }

    @Test
    public void shouldListMatchesByClubIdAndStadiumId(){
        Long corinthiansId = corinthiansEntity.getId();
        Long maracanaId = maracanaEntity.getId();

        Page<MatchEntity> matches = new PageImpl<>(List.of(flamengoVsCorinthiansAtMaracana));

        Mockito.doNothing().when(existenceValidator).validateClubExists(corinthiansId);
        Mockito.doNothing().when(existenceValidator).validateStadiumExists(maracanaId);
        Mockito.when(matchRepository.listMatchesByFilters(corinthiansId, maracanaId, null, pageable)).
                thenReturn(matches);
        Mockito.when(matchMapper.toDto(flamengoVsCorinthiansAtMaracana)).thenReturn(flamengoVsCorinthiansResponseDto);

        Page<MatchResponseDto> result = matchService.listMatchesByFilters(corinthiansId, maracanaId,
                null, pageable);

        Assertions.assertEquals(1,  result.getTotalElements());
        Assertions.assertEquals("Flamengo", result.getContent().getFirst().getHomeClubName());
        Assertions.assertEquals("Corinthians", result.getContent().getFirst().getAwayClubName());
        Assertions.assertEquals("Maracanã", result.getContent().getFirst().getStadiumName());
    }

    @Test
    public void shouldReturnEmptyPage_whenFiltersDoNotMatchAnyGame(){
        Long clubId = -2L;
        Long stadiumId = -25L;

        Page<MatchEntity> emptyPage = Page.empty(pageable);

        Mockito.doNothing().when(existenceValidator).validateClubExists(clubId);
        Mockito.doNothing().when(existenceValidator).validateStadiumExists(stadiumId);
        Mockito.when(matchRepository.listMatchesByFilters(clubId, stadiumId, null, pageable))
                .thenReturn(emptyPage);

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

        Assertions.assertEquals("Corinthians", result.getHomeClubName());
        Assertions.assertEquals("Grêmio", result.getAwayClubName());
        Assertions.assertEquals("Morumbi", result.getStadiumName());
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

        Mockito.when(clubRepository.findById(homeClubId)).thenReturn(Optional.of(corinthiansEntity));
        Mockito.when(clubRepository.findById(awayClubId)).thenReturn(Optional.of(gremioEntity));
        Mockito.when(stadiumService.findEntityById(stadiumId)).thenReturn(morumbiEntity);
        Mockito.when(matchMapper.toEntity(corinthiansVsGremioRequestDto, corinthiansEntity,
                gremioEntity, morumbiEntity)).thenReturn(corinthiansVsGremioAtMorumbi);
        Mockito.when(matchRepository.save(corinthiansVsGremioAtMorumbi)).thenReturn(corinthiansVsGremioAtMorumbi);
        Mockito.when(matchMapper.toDto(corinthiansVsGremioAtMorumbi)).thenReturn(corinthiansVsGremioResponseDto);

        MatchResponseDto result = matchService.save(corinthiansVsGremioRequestDto);

        Assertions.assertEquals("Corinthians", result.getHomeClubName());
        Assertions.assertEquals("Grêmio", result.getAwayClubName());
        Assertions.assertEquals("Morumbi", result.getStadiumName());
    }

    @Test
    public void shouldUpdateMatchSuccessfully(){
        Long existingMatchId = 1L;

        MatchEntity existingMatch = flamengoVsCorinthiansAtMaracana;
        existingMatch.setId(existingMatchId);

        MatchRequestDto updateRequest = MatchMockUtils.customRequest(gremioEntity.getId(), flamengoEntity.getId(),
                2, 3, morumbiEntity.getId());

        MatchResponseDto updatedResponse = flamengoVsCorinthiansResponseDto;
        updatedResponse.setHomeClubName("Grêmio");
        updatedResponse.setAwayClubName("Flamengo");
        updatedResponse.setStadiumName("Morumbi");

        Long homeClubId = updateRequest.getHomeClubId();
        Long awayClubId = updateRequest.getAwayClubId();
        Long stadiumId = updateRequest.getStadiumId();

        Mockito.when(matchRepository.findById(1L)).thenReturn(Optional.of(existingMatch));
        Mockito.when(clubRepository.findById(homeClubId)).thenReturn(Optional.of(gremioEntity));
        Mockito.when(clubRepository.findById(awayClubId)).thenReturn(Optional.of(flamengoEntity));
        Mockito.when(stadiumService.findEntityById(stadiumId)).thenReturn(morumbiEntity);
        Mockito.when(matchRepository.save(existingMatch)).thenReturn(existingMatch);
        Mockito.when(matchMapper.toDto(existingMatch)).thenReturn(updatedResponse);

        MatchResponseDto result = matchService.update(existingMatchId, updateRequest);

        Assertions.assertEquals("Grêmio", result.getHomeClubName());
        Assertions.assertEquals("Flamengo", result.getAwayClubName());
        Assertions.assertEquals("Morumbi", result.getStadiumName());
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

    @Test
    public void shouldGetHeadToHeadStats_whenNoFilterApplied(){
        Long clubId = corinthiansEntity.getId();
        Long opponentId = flamengoEntity.getId();

        ClubVersusClubStatsDto stats = new ClubVersusClubStatsDto();
        Mockito.when(matchRepository.getHeadToHeadStats(clubId, opponentId, null)).thenReturn(stats);

        List<MatchEntity> matchEntities = List.of(flamengoVsCorinthiansAtMaracana);
        Mockito.when(matchRepository.getHeadToHeadMatches(clubId, opponentId, null)).thenReturn(matchEntities);

        Mockito.when(matchMapper.toDto(flamengoVsCorinthiansAtMaracana)).thenReturn(flamengoVsCorinthiansResponseDto);

        HeadToHeadResponseDto result = matchService.getHeadToHeadStats(clubId, opponentId, null);

        Assertions.assertEquals(1, result.getMatches().size());
        Assertions.assertEquals(stats, result.getStats());
        Mockito.verify(matchRepository, Mockito.times(1)).getHeadToHeadStats(
                clubId, opponentId, null);
        Mockito.verify(matchRepository, Mockito.times(1)).getHeadToHeadMatches(
                clubId, opponentId, null);
        Mockito.verify(matchMapper, Mockito.times(1)).toDto(flamengoVsCorinthiansAtMaracana);
    }

    @Test
    public void shouldGetHeadToHeadStats_whenFilterApplied(){
        Long clubId = corinthiansEntity.getId();
        Long opponentId = flamengoEntity.getId();
        MatchFilterEnum filter = MatchFilterEnum.ROUT;
        String filterString = filter.name();

        ClubVersusClubStatsDto stats = new ClubVersusClubStatsDto();
        List<MatchEntity> matchEntities = List.of(flamengoVsCorinthiansAtMaracana);

        Mockito.when(matchRepository.getHeadToHeadStats(clubId, opponentId, filterString)).thenReturn(stats);
        Mockito.when(matchRepository.getHeadToHeadMatches(clubId, opponentId, filterString)).thenReturn(matchEntities);
        Mockito.when(matchMapper.toDto(flamengoVsCorinthiansAtMaracana)).thenReturn(flamengoVsCorinthiansResponseDto);

        HeadToHeadResponseDto result = matchService.getHeadToHeadStats(clubId, opponentId, filter);

        Assertions.assertEquals(1, result.getMatches().size());
        Assertions.assertEquals(stats, result.getStats());
        Assertions.assertEquals(flamengoVsCorinthiansResponseDto, result.getMatches().getFirst());
        Mockito.verify(matchRepository, Mockito.times(1)).getHeadToHeadStats(
                clubId, opponentId, filterString);
        Mockito.verify(matchRepository, Mockito.times(1)).getHeadToHeadMatches(
                clubId, opponentId, filterString);
        Mockito.verify(matchMapper).toDto(flamengoVsCorinthiansAtMaracana);
    }

    @Test
    public void shouldReturnClubRankingOrderedByMatches(){
        List<ClubRankingDto> clubRanking = List.of();
        Mockito.when(matchRepository.getClubRankingByTotalMatches()).thenReturn(clubRanking);

        List<ClubRankingDto> result = matchService.getClubRanking(RankingOrderEnum.MATCHES);

        Assertions.assertEquals(clubRanking, result);
        Mockito.verify(matchRepository, Mockito.times(1)).getClubRankingByTotalMatches();
    }

    @Test
    public void shouldReturnClubRankingOrderedByWins(){
        List<ClubRankingDto> clubRanking = List.of();
        Mockito.when(matchRepository.getClubRankingByTotalWins()).thenReturn(clubRanking);

        List<ClubRankingDto> result = matchService.getClubRanking(RankingOrderEnum.WINS);

        Assertions.assertSame(clubRanking, result);
        Mockito.verify(matchRepository, Mockito.times(1)).getClubRankingByTotalWins();
    }

    @Test
    public void shouldReturnClubRankingOrderedByGoals(){
        List<ClubRankingDto> clubRanking = List.of();
        Mockito.when(matchRepository.getClubRankingByTotalGoals()).thenReturn(clubRanking);

        List<ClubRankingDto> result = matchService.getClubRanking(RankingOrderEnum.GOALS);

        Assertions.assertEquals(clubRanking, result);
        Mockito.verify(matchRepository, Mockito.times(1)).getClubRankingByTotalGoals();
    }

    @Test
    public void shouldReturnClubRankingOrderedByPoints(){
        List<ClubRankingDto> clubRanking = List.of();
        Mockito.when(matchRepository.getClubRankingByTotalPoints()).thenReturn(clubRanking);

        List<ClubRankingDto> result = matchService.getClubRanking(RankingOrderEnum.POINTS);

        Assertions.assertEquals(clubRanking, result);
        Mockito.verify(matchRepository, Mockito.times(1)).getClubRankingByTotalPoints();
    }

    @Test
    public void shouldThrowException_whenUnknownRankingOrder(){
        RankingOrderEnum invalidOrder = null;

        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class,
                () -> matchService.getClubRanking(invalidOrder));

        Assertions.assertTrue(exception.getMessage().contains("Unknown ranking order: "));
    }
}
