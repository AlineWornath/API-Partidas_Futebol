package com.neocamp.soccer_matches.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neocamp.soccer_matches.dto.match.MatchRequestDto;
import com.neocamp.soccer_matches.dto.match.MatchResponseDto;
import com.neocamp.soccer_matches.entity.MatchEntity;
import com.neocamp.soccer_matches.entity.StadiumEntity;
import com.neocamp.soccer_matches.service.MatchService;
import com.neocamp.soccer_matches.testUtils.MatchMockUtils;
import com.neocamp.soccer_matches.testUtils.StadiumMockUtils;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.data.domain.Pageable;

import java.util.List;

@WebMvcTest(MatchController.class)
public class MatchControllerTest {

    private final Pageable  pageable = PageRequest.of(0, 10);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MatchService matchService;

    @Test
    public void shouldReturnAllMatches_whenNoFiltersProvided() throws Exception {
        MatchResponseDto flamengoVsCorinthians = MatchMockUtils.flamengoVsCorinthiansAtMaracanaResponseDto();
        MatchResponseDto corinthiansVsGremio = MatchMockUtils.corinthiansVsGremioAtMorumbiResponseDto();

        Page<MatchResponseDto> matches = new PageImpl<>(List.of
                (flamengoVsCorinthians, corinthiansVsGremio), pageable, 2);

        Mockito.when(matchService.listMatchesByFilters(null, null, null, pageable))
                .thenReturn(matches);

        mockMvc.perform(get("/matches")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].homeClubName").value("Flamengo"))
                .andExpect(jsonPath("$.content[0].awayClubName").value("Corinthians"))
                .andExpect(jsonPath("$.content[1].awayClubName").value("Grêmio"));
    }

    @Test
    public void shouldReturn200AndMatchList_whenFilteringByClubId() throws Exception {
        Long clubId = 1L;

        MatchResponseDto corinthiansVsGremio = MatchMockUtils.corinthiansVsGremioAtMorumbiResponseDto();

        Page<MatchResponseDto> matches = new PageImpl<>(List.of(corinthiansVsGremio), pageable, 1);

        Mockito.when(matchService.listMatchesByFilters(clubId, null,null, pageable)).thenReturn(matches);

        mockMvc.perform(get("/matches")
                .param("clubId", String.valueOf(clubId))
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].homeClubName").value("Corinthians"))
                .andExpect(jsonPath("$.content[0].awayClubName").value("Grêmio"));
    }

    @Test
    public void shouldReturn200AndMatchList_whenFilteringByStadiumId() throws Exception {
        Long stadiumId = 2L;
        StadiumEntity morumbi = StadiumMockUtils.morumbi();
        morumbi.setId(stadiumId);

        MatchResponseDto flamengoVsCorinthians = MatchMockUtils.flamengoVsCorinthiansAtMaracanaResponseDto();

        Page<MatchResponseDto> matches = new PageImpl<>(List.of(flamengoVsCorinthians), pageable, 1);

        Mockito.when(matchService.listMatchesByFilters(null, stadiumId, null, pageable)).thenReturn(matches);

        mockMvc.perform(get("/matches")
                .param("stadiumId", String.valueOf(stadiumId))
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].homeClubName").value("Flamengo"))
                .andExpect(jsonPath("$.content[0].awayClubName").value("Corinthians"));
    }

    @Test
    public void shouldReturn200AndMatchList_whenFilteringByClubIdAndStadiumId() throws Exception {
        Long clubId = 1L;
        Long stadiumId = 2L;

        MatchResponseDto corinthiansVsGremioAtMorumbi = MatchMockUtils.corinthiansVsGremioAtMorumbiResponseDto();

        Page<MatchResponseDto> matches = new PageImpl<>(List.of(corinthiansVsGremioAtMorumbi), pageable, 1);

        Mockito.when(matchService.listMatchesByFilters(clubId, stadiumId, null, pageable)).thenReturn(matches);

        mockMvc.perform(get("/matches")
                .param("clubId", String.valueOf(clubId))
                .param("stadiumId", String.valueOf(stadiumId))
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].homeClubName").value("Corinthians"))
                .andExpect(jsonPath("$.content[0].stadiumName").value("Morumbi"));
    }

    @Test
    public void shouldReturn200AndMatchDetails_whenFindById() throws Exception {
        Long matchId = 1L;
        MatchResponseDto flamengoVsCorinthians = MatchMockUtils.flamengoVsCorinthiansAtMaracanaResponseDto();
        flamengoVsCorinthians.setId(matchId);

        Mockito.when(matchService.findById(matchId)).thenReturn(flamengoVsCorinthians);

        mockMvc.perform(get("/matches/{id}", matchId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.homeClubName").value("Flamengo"))
                .andExpect(jsonPath("$.awayClubName").value("Corinthians"))
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    public void shouldReturn404_whenFindIdWithInvalidId() throws Exception {
        Long invalidId = -1L;

        Mockito.when(matchService.findById(invalidId)).thenThrow(new EntityNotFoundException("Match not found"));

        mockMvc.perform(get("/matches/{id}", invalidId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Match not found"))
                .andExpect(jsonPath("$.error").value("Not Found"));
    }

    @Test
    public void shouldReturn201AndMatchDetails_whenCreateValidMatch() throws Exception {
        MatchRequestDto requestDto = MatchMockUtils.flamengoVsCorinthiansAtMaracanaRequestDto();
        MatchEntity entity= MatchMockUtils.flamengoVsCorinthiansAtMaracana();
        MatchResponseDto responseDto = MatchMockUtils.flamengoVsCorinthiansAtMaracanaResponseDto();

        Mockito.when(matchService.assembleMatchFromRequestDto(requestDto)).thenReturn(entity);
        Mockito.when(matchService.save(entity)).thenReturn(responseDto);

        mockMvc.perform(post("/matches")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.homeClubName").value("Flamengo"))
                .andExpect(jsonPath("$.awayClubName").value("Corinthians"));
    }

    @Test
    public void shouldReturn400_whenCreateWithMissingParameter() throws Exception {
        MatchRequestDto invalidDto = MatchMockUtils.customRequest(null, 1L,
                2, 1, null);

        mockMvc.perform(post("/matches")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturn200AndMatchDetails_whenUpdateValidMatch() throws Exception {
        Long matchId = 1L;
        Long homeClubId = 2L;
        String homeClubName = "Grêmio";
        Long awayClubId = 3L;
        String awayClubName = "Flamengo";
        int homeGoals = 2, awayGoals = 3;
        Long stadiumId = 4L;
        String stadiumName = "Morumbi";

        MatchRequestDto updateRequestDto = MatchMockUtils.customRequest(homeClubId, awayClubId,
                homeGoals, awayGoals, stadiumId);

        MatchResponseDto updatedResponseDto = MatchMockUtils.customResponse(matchId, homeClubId, homeClubName, awayClubId,
                awayClubName, homeGoals, awayGoals, stadiumId, stadiumName);

        Mockito.when(matchService.updateFromRequestDto(matchId, updateRequestDto)).thenReturn(updatedResponseDto);

        mockMvc.perform(put("/matches/{id}", matchId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.homeClubName").value("Grêmio"))
                .andExpect(jsonPath("$.awayClubName").value("Flamengo"))
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    public void shouldReturn204_whenDeleteMatch() throws Exception {
        Long matchId = 1L;

        mockMvc.perform(delete("/matches/{id}", matchId))
                .andExpect(status().isNoContent());

        Mockito.verify(matchService, Mockito.times(1)).delete(matchId);
    }

}
