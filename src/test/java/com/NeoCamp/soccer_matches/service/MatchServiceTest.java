package com.neocamp.soccer_matches.service;

import com.neocamp.soccer_matches.dto.club.ClubRequestDto;
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
    private ClubRequestDto corinthiansRequestDto, flamengoRequestDto, gremioRequestDto;
    private ClubResponseDto corinthiansResponseDto, flamengoResponseDto, gremioResponseDto;
    private StadiumEntity maracana, morumbi;
    private StadiumResponseDto maracanaResponseDto, morumbiResponseDto;
    private MatchEntity flamengoVsCorinthiansAtMaracana, corinthiansVsGremioAtMorumbi;
    private MatchRequestDto flamengoVsCorinthiansRequestDto, corinthiansVsGremioRequestDto;
    private MatchResponseDto flamengoVsCorinthiansResponseDto, corinthiansVsGremioResponseDto;

    @BeforeEach
    public void setUp() {
        pageable = PageRequest.of(0, 10);

        corinthiansEntity = ClubMockUtils.corinthians();
        flamengoEntity = ClubMockUtils.flamengo();
        gremioEntity = ClubMockUtils.gremio();

        corinthiansRequestDto = ClubMockUtils.corinthiansRequestDto();
        flamengoRequestDto = ClubMockUtils.flamengoRequestDto();
        gremioRequestDto = ClubMockUtils.gremioRequestDto();

        corinthiansResponseDto = ClubMockUtils.corinthiansResponseDto();
        flamengoResponseDto = ClubMockUtils.flamengoResponseDto();
        gremioResponseDto = ClubMockUtils.gremioResponseDto();

        maracana = StadiumMockUtils.maracana();
        morumbi = StadiumMockUtils.morumbi();

        maracanaResponseDto = StadiumMockUtils.maracanaResponseDto();
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

        Mockito.when(matchRepository.listMatchesByFilters(null, null, pageable))
                .thenReturn(matches);
        Mockito.when(matchMapper.toDto(flamengoVsCorinthiansAtMaracana)).thenReturn(flamengoVsCorinthiansResponseDto);

        Page<MatchResponseDto> result = matchService.listMatchesByFilters(null, null, pageable);

        Assertions.assertEquals(2,  result.getTotalElements());
        Assertions.assertEquals("Flamengo", result.getContent().getFirst().getHomeClub().getName());
        Assertions.assertEquals("Corinthians", result.getContent().getFirst().getAwayClub().getName());
    }

    @Test
    public void shouldListMatchesByClubId(){
        Long flamengoId = flamengoEntity.getId();

        Page<MatchEntity> matches = new PageImpl<>(List.of(flamengoVsCorinthiansAtMaracana));

        Mockito.when(clubService.findEntityById(flamengoId)).thenReturn(flamengoEntity);
        Mockito.when(matchRepository.listMatchesByFilters(flamengoEntity, null, pageable)).thenReturn(matches);
        Mockito.when(matchMapper.toDto(flamengoVsCorinthiansAtMaracana)).thenReturn(flamengoVsCorinthiansResponseDto);
        Mockito.when(matchMapper.toDto(corinthiansVsGremioAtMorumbi)).thenReturn(corinthiansVsGremioResponseDto);

        Page<MatchResponseDto> result = matchService.listMatchesByFilters(flamengoId, null, pageable);

        Assertions.assertEquals(1,  result.getTotalElements());
        Assertions.assertEquals("Flamengo", result.getContent().getFirst().getHomeClub().getName());
        Assertions.assertEquals("Corinthians", result.getContent().getFirst().getAwayClub().getName());
    }
}
