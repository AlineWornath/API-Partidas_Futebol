package com.neocamp.soccer_matches.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neocamp.soccer_matches.DesafioFutebolApplication;
import com.neocamp.soccer_matches.dto.club.ClubRequestDto;
import com.neocamp.soccer_matches.entity.ClubEntity;
import com.neocamp.soccer_matches.entity.StateEntity;
import com.neocamp.soccer_matches.enums.StateCode;
import com.neocamp.soccer_matches.repository.ClubRepository;
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
    private ClubRepository clubRepository;

    @Autowired
    private StateRepository stateRepository;

    private ClubEntity gremio, flamengo, inactiveClub;

    @BeforeEach
    public void setup() {
        StateEntity rs = StateTestUtils.getStateOrFail(stateRepository, StateCode.RS);
        StateEntity rj = StateTestUtils.getStateOrFail(stateRepository, StateCode.RJ);

        gremio = new ClubEntity("Grêmio", rs,
                LocalDate.of(1950, 3, 24), true);
        clubRepository.save(gremio);

        flamengo = new ClubEntity("Flamengo", rj,
                LocalDate.of(1935, 5, 12), true);
        clubRepository.save(flamengo);

        inactiveClub = new ClubEntity("inactiveClub", rj,
                LocalDate.of(1919, 10, 1), false);
        clubRepository.save(inactiveClub);
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
                .andExpect(jsonPath("$.content[0].name").value("inactiveClub"));
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
                .andExpect(jsonPath("$.creationDate").value(creationDate.toString()))
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
                .andExpect(jsonPath("$.creationDate").value(newCreationDate.toString()));
    }

    @Test
    public void shouldDeleteClub() throws Exception {
        mockMvc.perform(delete("/clubs/{id}", flamengo.getId()))
                .andExpect(status().isNoContent());
    }
}
