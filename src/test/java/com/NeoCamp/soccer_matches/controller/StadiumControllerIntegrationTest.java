package com.neocamp.soccer_matches.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neocamp.soccer_matches.DesafioFutebolApplication;
import com.neocamp.soccer_matches.dto.stadium.StadiumRequestDto;
import com.neocamp.soccer_matches.entity.StadiumEntity;
import com.neocamp.soccer_matches.repository.StadiumRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = DesafioFutebolApplication.class)
@AutoConfigureMockMvc
@Transactional
public class StadiumControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private StadiumRepository stadiumRepository;

    @Test
    public void shouldListAllStadiums() throws Exception {
        StadiumEntity beiraRio = new StadiumEntity("Beira-Rio");
        StadiumEntity morumbi = new StadiumEntity("Morumbi");
        stadiumRepository.save(beiraRio);
        stadiumRepository.save(morumbi);

        mockMvc.perform(get("/stadiums")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].name").value("Beira-Rio"))
                .andExpect(jsonPath("$.content[1].name").value("Morumbi"));
    }

    @Test
    public void shouldGetStadiumById() throws Exception {
        StadiumEntity morumbi = new StadiumEntity("Morumbi");
        stadiumRepository.save(morumbi);

        mockMvc.perform(get("/stadiums/{id}", morumbi.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(morumbi.getId()))
                .andExpect(jsonPath("$.name").value("Morumbi"));
    }

    @Test
    public void shouldReturn404_whenGetStadiumNonexistent() throws Exception {
        Long invalidId = -1L;
        mockMvc.perform(get("/stadiums/{id}", invalidId))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldCreateStadium() throws Exception {
        StadiumRequestDto requestDto = new StadiumRequestDto("Morumbi", null);

        mockMvc.perform(post("/stadiums")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Morumbi"));
    }

    @Test
    public void shouldCreateStadiumWithAddressFromViaCep() throws Exception {
        StadiumRequestDto requestDto = new StadiumRequestDto("Morumbi", "05676120");

        mockMvc.perform(post("/stadiums")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Morumbi"))
                .andExpect(jsonPath("$.address.cep").value("05676-120"))
                .andExpect(jsonPath("$.address.city").value("São Paulo"));
    }

    @Test
    public void shouldReturn400_whenCreateWithMissingParameter() throws Exception {
        StadiumRequestDto invalidDto = new StadiumRequestDto(null, null);

        mockMvc.perform(post("/stadiums")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldUpdateStadium() throws Exception {
        StadiumEntity beiraRio = new StadiumEntity("Beira-Rio");
        stadiumRepository.save(beiraRio);

        StadiumRequestDto updateRequest = new StadiumRequestDto("Maracanã", null);

        mockMvc.perform(put("/stadiums/{id}", beiraRio.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(beiraRio.getId()))
                .andExpect(jsonPath("$.name").value("Maracanã"));
    }
}
