package com.neocamp.soccer_matches.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neocamp.soccer_matches.DesafioFutebolApplication;
import com.neocamp.soccer_matches.dto.match.MatchRequestDto;
import com.neocamp.soccer_matches.entity.ClubEntity;
import com.neocamp.soccer_matches.entity.MatchEntity;
import com.neocamp.soccer_matches.entity.StadiumEntity;
import com.neocamp.soccer_matches.entity.StateEntity;
import com.neocamp.soccer_matches.enums.StateCodeEnum;
import com.neocamp.soccer_matches.repository.ClubRepository;
import com.neocamp.soccer_matches.repository.MatchRepository;
import com.neocamp.soccer_matches.repository.StadiumRepository;
import com.neocamp.soccer_matches.repository.StateRepository;
import com.neocamp.soccer_matches.testUtils.StateTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.LocalDateTime;

@SpringBootTest(classes = DesafioFutebolApplication.class)
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class MatchControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private StateRepository stateRepository;

    @Autowired
    private ClubRepository clubRepository;

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private StadiumRepository stadiumRepository;

    private ClubEntity gremio, flamengo, saoPaulo;
    private StadiumEntity maracana, morumbi;
    private MatchEntity flamengoVsGremioAtMaracana;

    @BeforeEach
    public void setup() {
        StateEntity rs = StateTestUtils.getStateOrFail(stateRepository, StateCodeEnum.RS);
        StateEntity rj = StateTestUtils.getStateOrFail(stateRepository, StateCodeEnum.RJ);
        StateEntity sp = StateTestUtils.getStateOrFail(stateRepository, StateCodeEnum.SP);

        gremio = new ClubEntity("Grêmio", rs,
                LocalDate.of(1950, 3, 24), true);
        clubRepository.save(gremio);

        flamengo = new ClubEntity("Flamengo", rj,
                LocalDate.of(1935, 5, 12), true);
        clubRepository.save(flamengo);

        saoPaulo = new ClubEntity("São Paulo", sp,
                LocalDate.of(1919, 12, 3), true);
        clubRepository.save(saoPaulo);

        maracana = new StadiumEntity("Maracanã");
        stadiumRepository.save(maracana);

        morumbi = new StadiumEntity("Morumbi");
        stadiumRepository.save(morumbi);

        flamengoVsGremioAtMaracana = new MatchEntity(flamengo, gremio, 1, 0,
                maracana, LocalDateTime.of(2015, 1, 14, 15, 45));
        matchRepository.save(flamengoVsGremioAtMaracana);

        MatchEntity saoPauloVsGremioAtMorumbi = new MatchEntity(saoPaulo, gremio, 2, 3,
                morumbi, LocalDateTime.of(2002, 7, 18, 16, 30));
        matchRepository.save(saoPauloVsGremioAtMorumbi);
    }

    @Test
    public void shouldReturnAllMatches_whenNoFiltersProvided() throws Exception {
        mockMvc.perform(get("/matches")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].homeClubName").value("Flamengo"))
                .andExpect(jsonPath("$.content[0].awayClubName").value("Grêmio"))
                .andExpect(jsonPath("$.content[1].stadiumName").value("Morumbi"));
    }

    @Test
    public void shouldReturnMatchesFilteredByClubId() throws Exception {
        Long clubId = saoPaulo.getId();

        mockMvc.perform(get("/matches")
                .param("clubId", clubId.toString())
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].homeClubName").value("São Paulo"))
                .andExpect(jsonPath("$.content[0].awayClubName").value("Grêmio"))
                .andExpect(jsonPath("$.content[0].stadiumName").value("Morumbi"));
    }

    @Test
    public void shouldReturnMatchesFilteredByStadiumId() throws Exception {
        Long stadiumId = maracana.getId();

        mockMvc.perform(get("/matches")
                .param("stadiumId", stadiumId.toString())
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].stadiumName").value("Maracanã"))
                .andExpect(jsonPath("$.content[0].homeClubName").value("Flamengo"));
    }

    @Test
    public void shouldReturnMatchesFilteredByClubIdAndStadiumId() throws Exception {
        Long clubId = saoPaulo.getId();
        Long stadiumId = maracana.getId();

        mockMvc.perform(get("/matches")
                .param("clubId", clubId.toString())
                .param("stadiumId", stadiumId.toString())
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(0));
    }

    @Test
    public void shouldGetMatchById() throws Exception {
        mockMvc.perform(get("/matches/{id}", flamengoVsGremioAtMaracana.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.homeClubName").value("Flamengo"))
                .andExpect(jsonPath("$.awayClubId").value(gremio.getId()))
                .andExpect(jsonPath("$.stadiumName").value("Maracanã"));
    }

    @Test
    public void shouldReturn404_whenGetMatchNonexistent() throws Exception {
        Long invalidId = -2L;

        mockMvc.perform(get("/matches/{id}", invalidId))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldCreateMatch() throws Exception {
        Long homeClubId = flamengo.getId();
        Long awayClubId = saoPaulo.getId();
        int homeGoals = 1;
        int awayGoals = 2;
        Long stadiumId = maracana.getId();
        LocalDateTime matchDateTime = LocalDateTime.now();

        MatchRequestDto requestDto = new MatchRequestDto(homeClubId, awayClubId, homeGoals, awayGoals,
                stadiumId, matchDateTime);

        mockMvc.perform(post("/matches")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.homeClubName").value("Flamengo"))
                .andExpect(jsonPath("$.homeGoals").value(homeGoals))
                .andExpect(jsonPath("$.stadiumName").value("Maracanã"));
    }

    @Test
    public void shouldReturn404_whenCreateWithMissingParameter() throws Exception {
        Long homeClubId = gremio.getId();
        Long StadiumId = morumbi.getId();
        int homeGoals = 1;
        int awayGoals = 3;

        MatchRequestDto invalidDto = new MatchRequestDto(homeClubId, null, homeGoals, awayGoals,
                StadiumId, null);

        mockMvc.perform(post("/matches")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldUpdateMatch() throws Exception {
        Long toBeUpdatedId = flamengoVsGremioAtMaracana.getId();

        Long newHomeClubId = saoPaulo.getId();
        Long newAwayClubId = flamengo.getId();
        int newHomeGoals = 0;
        int newAwayGoals = 0;
        Long newStadiumId = morumbi.getId();
        LocalDateTime newMatchDateTime = LocalDateTime.now();

        MatchRequestDto updateRequest = new MatchRequestDto(newHomeClubId, newAwayClubId, newHomeGoals,
                newAwayGoals, newStadiumId, newMatchDateTime);

        mockMvc.perform(put("/matches/{id}", toBeUpdatedId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.homeClubName").value("São Paulo"))
                .andExpect(jsonPath("$.awayClubName").value("Flamengo"))
                .andExpect(jsonPath("$.stadiumName").value("Morumbi"));
    }

    @Test
    public void shouldDeleteMatch() throws Exception {
        Long toBeDeletedId = flamengoVsGremioAtMaracana.getId();

        mockMvc.perform(delete("/matches/{id}", toBeDeletedId))
                .andExpect(status().isNoContent());
    }
}
