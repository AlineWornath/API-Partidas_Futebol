package com.neocamp.soccer_matches.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neocamp.soccer_matches.DesafioFutebolApplication;
import com.neocamp.soccer_matches.dto.club.ClubRequestDto;
import com.neocamp.soccer_matches.entity.ClubEntity;
import com.neocamp.soccer_matches.entity.MatchEntity;
import com.neocamp.soccer_matches.entity.StadiumEntity;
import com.neocamp.soccer_matches.enums.MatchStatusEnum;
import com.neocamp.soccer_matches.testUtils.IntegrationTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static com.neocamp.soccer_matches.utils.DateFormatter.CLUB_CREATION_DATE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.LocalDateTime;

@SpringBootTest(classes = DesafioFutebolApplication.class)
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class ClubControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private IntegrationTestUtils testUtils;

    private ClubEntity gremio, flamengo, inactiveClub;

    @BeforeEach
    public void setup() {
        gremio = testUtils.createClub("Grêmio", "RS", LocalDate.of(1920, 5, 30),
                true);

        flamengo = testUtils.createClub("Flamengo", "RJ", LocalDate.of(1970, 2, 10),
                true);

        inactiveClub = testUtils.createClub("Inactive Club", "SP",
                LocalDate.of(1950, 9, 27), false);

        StadiumEntity maracana = testUtils.createStadium("Maracanã", "");

        MatchEntity flamengoVsGremioAtMaracana = testUtils.createMatch(flamengo, gremio, 3, 1,
                maracana, LocalDateTime.of(2023, 3, 2, 15, 45),
                MatchStatusEnum.IN_PROGRESS);

        MatchEntity gremioVsInactiveClubAtMaracana = testUtils.createMatch(gremio, inactiveClub, 1,
                0, maracana, LocalDateTime.of(2020, 1, 25, 16, 30),
                MatchStatusEnum.IN_PROGRESS);
    }

    @Test
    public void shouldReturnAllClubs_whenNoFiltersProvided() throws Exception {
        mockMvc.perform(get("/clubs")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(3))
                .andExpect(jsonPath("$.content[0].id").value(gremio.getId()))
                .andExpect(jsonPath("$.content[0].name").value("Grêmio"))
                .andExpect(jsonPath("$.content[1].id").value(flamengo.getId()));
    }

    @Test
    public void shouldReturnClubsFilteredByName() throws Exception {
        mockMvc.perform(get("/clubs")
                .param("name", "Fla")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].id").value(flamengo.getId()))
                .andExpect(jsonPath("$.content[0].name").value("Flamengo"));
    }

    @Test
    public void shouldReturnClubsFilteredByStateCode() throws Exception {
        mockMvc.perform(get("/clubs")
                .param("stateCode", "RS")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].id").value(gremio.getId()))
                .andExpect(jsonPath("$.content[0].name").value("Grêmio"));
    }

    @Test
    public void shouldReturnClubsFilteredByActive() throws Exception {
        mockMvc.perform(get("/clubs")
                .param("active", "false")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].id").value(inactiveClub.getId()))
                .andExpect(jsonPath("$.content[0].name").value("Inactive Club"));
    }

    @Test
    public void shouldReturnClubsFilteredByNameAndStateCodeAndActive() throws Exception {
        mockMvc.perform(get("/clubs")
                .param("name", "i")
                .param("stateCode", "RS")
                .param("active", "true")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].id").value(gremio.getId()))
                .andExpect(jsonPath("$.content[0].name").value("Grêmio"));
    }

    @Test
    public void shouldGetClubById() throws Exception {
        mockMvc.perform(get("/clubs/{id}", flamengo.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Flamengo"))
                .andExpect(jsonPath("$.id").value(flamengo.getId()));
    }

    @Test
    public void shouldReturn404_whenGetClubNonExistent() throws Exception {
        Long invalidId = -1L;

        mockMvc.perform(get("/clubs/{id}",invalidId))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldReturn200AndClubStats() throws Exception {
        Long clubId = flamengo.getId();

        mockMvc.perform(get("/clubs/{id}/stats", clubId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalWins").value(1))
                .andExpect(jsonPath("$.totalLosses").value(0))
                .andExpect(jsonPath("$.goalsScored").value(3));
    }

    @Test
    public void shouldReturn200AndClubVersusOpponentsStats() throws Exception {
        Long clubId = gremio.getId();

        mockMvc.perform(get("/clubs/{id}/opponents/stats", clubId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].totalWins").value(0))
                .andExpect(jsonPath("$[0].totalLosses").value(1))
                .andExpect(jsonPath("$[0].goalsScored").value(1))
                .andExpect(jsonPath("$[1].totalWins").value(1))
                .andExpect(jsonPath("$[1].goalsConceded").value(0));
    }

    @Test
    public void shouldReturn200AndHeadToHeadStats() throws Exception {
        Long clubId = flamengo.getId();
        Long opponentId = gremio.getId();

        mockMvc.perform(get("/clubs/{clubId}/head-to-head/{opponentId}", clubId,  opponentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stats.totalWins").value(1))
                .andExpect(jsonPath("$.stats.goalsScored").value(3))
                .andExpect(jsonPath("$.stats.goalsConceded").value(1))
                .andExpect(jsonPath("$.matches.length()").value(1));
    }

    @Test
    public void shouldReturn200AndClubRanking_orderedByTotalGoals() throws Exception {
        mockMvc.perform(get("/clubs/ranking")
                .param("rankingOrderEnum", "GOALS"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].clubName").value("Flamengo"))
                .andExpect(jsonPath("$[1].clubName").value("Grêmio"));
    }

    @Test
    public void shouldCreateClub() throws Exception {
        String name = "Coritiba";
        String stateCode = "PR";
        LocalDate creationDate = LocalDate.of(2020, 1, 1);
        Boolean active = true;

        ClubRequestDto RequestDto = new ClubRequestDto(name, stateCode, creationDate, active);

        mockMvc.perform(post("/clubs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(RequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.creationDate").value(creationDate.format(CLUB_CREATION_DATE)))
                .andExpect(jsonPath("$.active").value(active));
    }

    @Test
    public void shouldReturn404_whenCreateWithMissingParameter() throws Exception {
        String stateCode = "TO";
        LocalDate creationDate = LocalDate.of(2000, 2, 15);
        ClubRequestDto invalidDto = new ClubRequestDto(null, stateCode, creationDate, null);

        mockMvc.perform(post("/clubs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldUpdateClub() throws Exception {
        Long toBeUpdatedId = gremio.getId();

        String newName = "Coritiba";
        String newStateCode = "PR";
        LocalDate newCreationDate = LocalDate.of(2020, 1, 1);
        Boolean active = true;
        ClubRequestDto updateRequest = new ClubRequestDto(newName, newStateCode, newCreationDate, active);

        mockMvc.perform(put("/clubs/{id}", toBeUpdatedId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(newName))
                .andExpect(jsonPath("$.creationDate").value(newCreationDate
                        .format(CLUB_CREATION_DATE)));
    }

    @Test
    public void shouldDeleteClub() throws Exception {
        mockMvc.perform(delete("/clubs/{id}", flamengo.getId()))
                .andExpect(status().isNoContent());
    }
}
