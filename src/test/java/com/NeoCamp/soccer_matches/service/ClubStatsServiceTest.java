package com.neocamp.soccer_matches.service;

import com.neocamp.soccer_matches.dto.club.ClubRankingDto;
import com.neocamp.soccer_matches.dto.club.ClubVersusClubStatsDto;
import com.neocamp.soccer_matches.dto.match.HeadToHeadResponseDto;
import com.neocamp.soccer_matches.dto.match.MatchResponseDto;
import com.neocamp.soccer_matches.entity.ClubEntity;
import com.neocamp.soccer_matches.entity.MatchEntity;
import com.neocamp.soccer_matches.enums.MatchFilterEnum;
import com.neocamp.soccer_matches.enums.RankingOrderEnum;
import com.neocamp.soccer_matches.mapper.MatchMapper;
import com.neocamp.soccer_matches.repository.MatchRepository;
import com.neocamp.soccer_matches.testUtils.ClubMockUtils;
import com.neocamp.soccer_matches.testUtils.MatchMockUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class ClubStatsServiceTest {

    @Mock
    private MatchRepository matchRepository;

    @Mock
    private MatchMapper matchMapper;

    @InjectMocks
    private ClubStatsService clubStatsService;

    private ClubEntity corinthiansEntity, flamengoEntity;
    private MatchEntity flamengoVsCorinthiansAtMaracana;
    private MatchResponseDto flamengoVsCorinthiansResponseDto;

    @BeforeEach
    public void setUp() {
        corinthiansEntity = ClubMockUtils.corinthians();
        flamengoEntity = ClubMockUtils.flamengo();

        flamengoVsCorinthiansAtMaracana = MatchMockUtils.flamengoVsCorinthiansAtMaracana();

        flamengoVsCorinthiansResponseDto = MatchMockUtils.flamengoVsCorinthiansAtMaracanaResponseDto();
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

        HeadToHeadResponseDto result = clubStatsService.getHeadToHeadStats(clubId, opponentId, null);

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

        HeadToHeadResponseDto result = clubStatsService.getHeadToHeadStats(clubId, opponentId, filter);

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

        List<ClubRankingDto> result = clubStatsService.getClubRanking(RankingOrderEnum.MATCHES);

        Assertions.assertEquals(clubRanking, result);
        Mockito.verify(matchRepository, Mockito.times(1)).getClubRankingByTotalMatches();
    }

    @Test
    public void shouldReturnClubRankingOrderedByWins(){
        List<ClubRankingDto> clubRanking = List.of();
        Mockito.when(matchRepository.getClubRankingByTotalWins()).thenReturn(clubRanking);

        List<ClubRankingDto> result = clubStatsService.getClubRanking(RankingOrderEnum.WINS);

        Assertions.assertSame(clubRanking, result);
        Mockito.verify(matchRepository, Mockito.times(1)).getClubRankingByTotalWins();
    }

    @Test
    public void shouldReturnClubRankingOrderedByGoals(){
        List<ClubRankingDto> clubRanking = List.of();
        Mockito.when(matchRepository.getClubRankingByTotalGoals()).thenReturn(clubRanking);

        List<ClubRankingDto> result = clubStatsService.getClubRanking(RankingOrderEnum.GOALS);

        Assertions.assertEquals(clubRanking, result);
        Mockito.verify(matchRepository, Mockito.times(1)).getClubRankingByTotalGoals();
    }

    @Test
    public void shouldReturnClubRankingOrderedByPoints(){
        List<ClubRankingDto> clubRanking = List.of();
        Mockito.when(matchRepository.getClubRankingByTotalPoints()).thenReturn(clubRanking);

        List<ClubRankingDto> result = clubStatsService.getClubRanking(RankingOrderEnum.POINTS);

        Assertions.assertEquals(clubRanking, result);
        Mockito.verify(matchRepository, Mockito.times(1)).getClubRankingByTotalPoints();
    }

    @Test
    public void shouldThrowException_whenNullRankingOrder(){
        RankingOrderEnum nullOrder = null;

        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class,
                () -> clubStatsService.getClubRanking(nullOrder));

        Assertions.assertTrue(exception.getMessage().contains("Unknown ranking order: "));
    }
}
