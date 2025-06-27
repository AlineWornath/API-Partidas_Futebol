package com.neocamp.soccer_matches.service;

import com.neocamp.soccer_matches.dto.club.ClubRequestDto;
import com.neocamp.soccer_matches.dto.club.ClubResponseDto;
import com.neocamp.soccer_matches.dto.club.ClubStatsResponseDto;
import com.neocamp.soccer_matches.dto.club.ClubVersusClubStatsDto;
import com.neocamp.soccer_matches.entity.ClubEntity;
import com.neocamp.soccer_matches.entity.StateEntity;
import com.neocamp.soccer_matches.enums.StateCode;
import com.neocamp.soccer_matches.exception.BusinessException;
import com.neocamp.soccer_matches.mapper.ClubMapper;
import com.neocamp.soccer_matches.repository.ClubRepository;
import com.neocamp.soccer_matches.repository.MatchRepository;
import com.neocamp.soccer_matches.testUtils.ClubMockUtils;
import com.neocamp.soccer_matches.testUtils.StateMockUtils;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class ClubServiceTest {

    @Mock
    private ClubRepository clubRepository;

    @Mock
    private StateService stateService;

    @Mock
    private ClubMapper clubMapper;

    @Mock
    private MatchRepository matchRepository;

    @InjectMocks
    private ClubService clubService;

    private Pageable pageable;
    private ClubEntity gremioEntity, flamengoEntity, corinthiansEntity;
    private ClubRequestDto gremioRequestDto;
    private ClubResponseDto gremioResponseDto, flamengoResponseDto, corinthiansResponseDto;
    private StateEntity rs, rj;

    @BeforeEach
    public void setUp() {
        pageable = PageRequest.of(0, 10);

        gremioEntity = ClubMockUtils.gremio();
        flamengoEntity = ClubMockUtils.flamengo();
        corinthiansEntity = ClubMockUtils.corinthians();

        gremioRequestDto = ClubMockUtils.gremioRequestDto();

        gremioResponseDto = ClubMockUtils.gremioResponseDto();
        flamengoResponseDto = ClubMockUtils.flamengoResponseDto();
        corinthiansResponseDto = ClubMockUtils.corinthiansResponseDto();

        rs = StateMockUtils.rs();
        rj = StateMockUtils.rj();
    }

    @Test
    public void shouldListAllClubs_whenAllFiltersAreNull() {
        Page<ClubEntity> clubs = new PageImpl<>(List.of(gremioEntity, flamengoEntity), pageable, 2);

        Mockito.when(clubRepository.listClubsByFilters(null, null, null, pageable))
                .thenReturn(clubs);
        Mockito.when(clubMapper.toDto(gremioEntity)).thenReturn(gremioResponseDto);
        Mockito.when(clubMapper.toDto(flamengoEntity)).thenReturn(flamengoResponseDto);

        Page<ClubResponseDto> result = clubService.listClubsByFilters(null, null, null, pageable);

        Assertions.assertEquals(2, result.getTotalElements());
        Assertions.assertEquals("Grêmio", result.getContent().get(0).getName());
        Assertions.assertEquals("Flamengo", result.getContent().get(1).getName());
    }

    @Test
    public void shouldListClubsByName() {
        Page<ClubEntity> clubs = new PageImpl<>(List.of(flamengoEntity), pageable, 1);

        Mockito.when(clubRepository.listClubsByFilters("Flamengo", null,
                null, pageable)).thenReturn(clubs);
        Mockito.when(clubMapper.toDto(flamengoEntity)).thenReturn(flamengoResponseDto);

        Page<ClubResponseDto> result = clubService.listClubsByFilters("Flamengo",
                null, null, pageable);

        Assertions.assertEquals(1, result.getTotalElements());
        Assertions.assertEquals("Flamengo", result.getContent().getFirst().getName());
    }

    @Test
    public void shouldListClubsByHomeState() {
        Mockito.when(stateService.findByCode(StateCode.RS)).thenReturn(rs);

        Page<ClubEntity> clubs = new PageImpl<>(List.of(gremioEntity), pageable, 1);

        Mockito.when(clubRepository.listClubsByFilters(null, rs,  null, pageable ))
                .thenReturn(clubs);
        Mockito.when(clubMapper.toDto(gremioEntity)).thenReturn(gremioResponseDto);

        Page<ClubResponseDto> result = clubService.listClubsByFilters(null, "RS", null, pageable);

        Assertions.assertEquals(1, result.getTotalElements());
        Assertions.assertEquals("Grêmio", result.getContent().getFirst().getName());
        Assertions.assertEquals("RS", result.getContent().getFirst().getHomeState().getCode());
    }

    @Test
    public void shouldListClubsByActive() {
        corinthiansEntity.setActive(true);

        Page<ClubEntity> clubs = new PageImpl<>(List.of(corinthiansEntity), pageable, 1);

        Mockito.when(clubRepository.listClubsByFilters(null, null, true, pageable))
                .thenReturn(clubs);
        Mockito.when(clubMapper.toDto(corinthiansEntity)).thenReturn(corinthiansResponseDto);

        Page<ClubResponseDto> result = clubService.listClubsByFilters(null, null, true, pageable);

        Assertions.assertEquals(1, result.getTotalElements());
        Assertions.assertEquals("Corinthians", result.getContent().getFirst().getName());
        Assertions.assertEquals("SP", result.getContent().getFirst().getHomeState().getCode());
    }

    @Test
    public void shouldListClubsByHomeStateAndActive() {
        Mockito.when(stateService.findByCode(StateCode.RS)).thenReturn(rs);

        gremioEntity.setActive(true);

        Page<ClubEntity> clubs = new PageImpl<>(List.of(gremioEntity), pageable, 1);

        Mockito.when(clubRepository.listClubsByFilters(null, rs, true, pageable))
                .thenReturn(clubs);
        Mockito.when(clubMapper.toDto(gremioEntity)).thenReturn(gremioResponseDto);

        Page<ClubResponseDto> result = clubService.listClubsByFilters(null, "RS", true, pageable);

        Assertions.assertEquals(1, result.getTotalElements());
        Assertions.assertEquals("Grêmio", result.getContent().getFirst().getName());
        Assertions.assertEquals("RS", result.getContent().getFirst().getHomeState().getCode());
    }

    @Test
    public void shouldListClubsByNameAndHomeStateAndActive() {
        Mockito.when(stateService.findByCode(StateCode.RJ)).thenReturn(rj);

        flamengoEntity.setActive(true);

        Page<ClubEntity> clubs = new PageImpl<>(List.of(flamengoEntity), pageable, 1);

        Mockito.when(clubRepository.listClubsByFilters("Flamengo", rj, true, pageable))
                .thenReturn(clubs);
        Mockito.when(clubMapper.toDto(flamengoEntity)).thenReturn(flamengoResponseDto);

        Page<ClubResponseDto> result = clubService.listClubsByFilters("Flamengo", "RJ", true, pageable);

        Assertions.assertEquals(1, result.getTotalElements());
        Assertions.assertEquals("Flamengo", result.getContent().getFirst().getName());
        Assertions.assertEquals("RJ", result.getContent().getFirst().getHomeState().getCode());
        Assertions.assertTrue(result.getContent().getFirst().getActive());
    }

    @Test
    public void shouldReturnEmptyPage_whenNoClubMatchFilters() {
        Mockito.when(clubRepository.listClubsByFilters("XXX", null, null, pageable))
                .thenReturn(Page.empty(pageable));

        Page<ClubResponseDto> result = clubService.listClubsByFilters("XXX", null, null, pageable);

        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    public void shouldReturnClubDtoByIdSuccessfully() {
        gremioEntity.setId(10L);

        gremioResponseDto.setId(10L);

        Mockito.when(clubRepository.findById(10L)).thenReturn(Optional.of(gremioEntity));
        Mockito.when(clubMapper.toDto(gremioEntity)).thenReturn(gremioResponseDto);

        ClubResponseDto result = clubService.findById(10L);

        Assertions.assertEquals("Grêmio", result.getName());
        Assertions.assertEquals(10L, result.getId());
        Assertions.assertEquals("RS", result.getHomeState().getCode());
    }

    @Test
    public void shouldThrowException_whenFindByIdWithInvalidId() {
        Long invalidId = -2L;
        Mockito.when(clubRepository.findById(invalidId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = Assertions.assertThrows(EntityNotFoundException.class,
                () -> clubService.findById(invalidId));
        Assertions.assertTrue(exception.getMessage().contains("Club not found: "));
    }

    @Test
    public void shouldReturnClubEntityByIdSuccessfully() {
        flamengoEntity.setId(75L);

        Mockito.when(clubRepository.findById(75L)).thenReturn(Optional.of(flamengoEntity));

        ClubEntity result = clubService.findEntityById(75L);

        Assertions.assertEquals("Flamengo", result.getName());
        Assertions.assertEquals(75L, result.getId());
        Assertions.assertEquals("RJ", result.getHomeState().getCode().name());
    }

    @Test
    public void shouldThrowException_whenFindEntityByIdWithInvalidId(){
        Long invalidId = -10L;
        Mockito.when(clubRepository.findById(invalidId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = Assertions.assertThrows(EntityNotFoundException.class,
                () -> clubService.findEntityById(invalidId));
        Assertions.assertTrue(exception.getMessage().contains("Club not found: "));
    }

    @Test
    public void shouldReturnClubStats_whenValidClubId() {
        Long clubId = 10L;
        ClubStatsResponseDto mockStats = new ClubStatsResponseDto(clubId, "Grêmio",
                5L, 9L, 8L, 11L, 15L);

        ClubEntity gremio = ClubMockUtils.gremio();

        Mockito.when(clubRepository.findById(clubId)).thenReturn(Optional.of(gremio));
        Mockito.when(matchRepository.getClubStats(clubId)).thenReturn(mockStats);

        ClubStatsResponseDto result = clubService.getClubStats(clubId);

        Assertions.assertEquals(8, result.getTotalLosses());
        Assertions.assertEquals("Grêmio", result.getClubName());
        Assertions.assertEquals(11, result.getGoalsScored());
    }

    @Test
    public void shouldReturnClubVersusOpponentsStats_whenValidClubId() {
        Long id = 15L;

        ClubVersusClubStatsDto mockOpponentStats = new ClubVersusClubStatsDto(id, "Grêmio", 8L,
                "Flamengo", 3L, 1L, 4L, 15L, 17L);

        List<ClubVersusClubStatsDto> statsList = List.of(mockOpponentStats);

        ClubEntity gremio = ClubMockUtils.gremio();
        Mockito.when(clubRepository.findById(id)).thenReturn(Optional.of(gremio));
        Mockito.when(matchRepository.getClubVersusOpponentsStats(id)).thenReturn(statsList);

        List<ClubVersusClubStatsDto> result = clubService.getClubVersusOpponentsStats(id);

        Assertions.assertEquals(statsList, result);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("Flamengo", result.getFirst().getOpponentName());
        Assertions.assertEquals(15, result.getFirst().getGoalsScored());
    }

    @Test
    public void shouldSaveClubSuccessfully() {
        gremioEntity.setId(12L);
        gremioResponseDto.setId(12L);

        Mockito.when(stateService.findByCode(StateCode.RS)).thenReturn(rs);
        Mockito.when(clubMapper.toEntity(gremioRequestDto, rs)).thenReturn(gremioEntity);
        Mockito.when(clubRepository.save(gremioEntity)).thenReturn(gremioEntity);
        Mockito.when(clubMapper.toDto(gremioEntity)).thenReturn(gremioResponseDto);

        ClubResponseDto result = clubService.save(gremioRequestDto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(12L, result.getId());
        Assertions.assertEquals("Grêmio", result.getName());
        Assertions.assertEquals("RS", result.getHomeState().getCode());
    }

    @Test
    public void shouldThrowExceptionInSaveClub_whenInvalidStateCode() {
        String invalidStateCode = "XXX";
        ClubRequestDto clubDto = ClubMockUtils.customRequestDto("club2",invalidStateCode,
                LocalDate.of(2020, 3, 15), true);

        BusinessException exception = Assertions.assertThrows(BusinessException.class,
                () -> clubService.save(clubDto));

        Assertions.assertEquals("Invalid state code: XXX", exception.getMessage());
        Mockito.verify(clubRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    public void shouldUpdateClubSuccessfully() {
        Long existingClubId = 1L;

        ClubEntity existingClub = ClubMockUtils.customEntity("Club", StateMockUtils.sp(),
                LocalDate.of(2020, 3, 15), false);
        existingClub.setId(existingClubId);

        ClubRequestDto updateRequest = ClubMockUtils.customRequestDto("newName", "RJ",
                LocalDate.of(2015, 10, 27), true);

        ClubResponseDto updatedResponse = ClubMockUtils.customResponseDto("newName", StateMockUtils.rjDto(),
                LocalDate.of(2015, 10, 27),true);
        updatedResponse.setId(existingClubId);

        Mockito.when(clubRepository.findById(existingClubId)).thenReturn(Optional.of(existingClub));
        Mockito.when(stateService.findByCode(StateCode.RJ)).thenReturn(rj);
        Mockito.when(clubRepository.save(existingClub)).thenReturn(existingClub);
        Mockito.when(clubMapper.toDto(existingClub)).thenReturn(updatedResponse);

        ClubResponseDto result = clubService.update(existingClubId, updateRequest);

        Assertions.assertEquals(1L, result.getId());
        Assertions.assertEquals("newName", result.getName());
        Assertions.assertEquals("RJ", result.getHomeState().getCode());
        Assertions.assertEquals(LocalDate.of(2015, 10, 27), result.getCreationDate());
    }

    @Test
    public void shouldThrowException_whenUpdateClubWithInvalidId(){
        Long invalidId = -1L;

        Mockito.when(clubRepository.findById(invalidId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = Assertions.assertThrows(EntityNotFoundException.class,
                () -> clubService.update(invalidId, gremioRequestDto));

        Assertions.assertTrue(exception.getMessage().contains("Club not found: "));
    }

    @Test
    public void shouldThrowException_whenUpdateClubWithInvalidStateCode() {
        Long validId = 5L;
        String invalidStateCode = "YYY";

        ClubRequestDto requestDto = ClubMockUtils.customRequestDto("Club6", invalidStateCode,
                LocalDate.of(2020, 3, 15), true);

        ClubEntity existingClub = new ClubEntity();
        existingClub.setId(validId);
        Mockito.when(clubRepository.findById(validId)).thenReturn(Optional.of(existingClub));

        BusinessException exception = Assertions.assertThrows(BusinessException.class,
                () -> clubService.update(validId, requestDto));

        Assertions.assertEquals("Invalid state code: YYY", exception.getMessage());
    }

    @Test
    void shouldMarkClubAsInactive_whenDeleteIsCalled() {
        Long existingClubId = 11L;
        ClubEntity existingClub = new ClubEntity();
        existingClub.setId(existingClubId);
        existingClub.setActive(true);

        Mockito.when(clubRepository.findById(existingClubId)).thenReturn(Optional.of(existingClub));

        clubService.delete(existingClubId);

        Mockito.verify(clubRepository, Mockito.times(1)).save(existingClub);
        Assertions.assertFalse(existingClub.getActive());
    }
}
