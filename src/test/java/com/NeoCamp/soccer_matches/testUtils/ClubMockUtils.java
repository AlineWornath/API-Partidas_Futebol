package com.neocamp.soccer_matches.testUtils;

import com.neocamp.soccer_matches.dto.club.ClubRequestDto;
import com.neocamp.soccer_matches.dto.club.ClubResponseDto;
import com.neocamp.soccer_matches.entity.ClubEntity;
import com.neocamp.soccer_matches.entity.StateEntity;

import java.time.LocalDate;
import java.util.UUID;

public class ClubMockUtils {

    public static final UUID GREMIO_UUID = UUID.randomUUID();
    public static final UUID FLAMENGO_UUID = UUID.randomUUID();
    public static final UUID CORINTHIANS_UUID = UUID.randomUUID();

    public static ClubEntity gremio() {
        return new ClubEntity(1L, GREMIO_UUID,"Grêmio", StateMockUtils.rs(),
                LocalDate.of(1920, 5, 30), true);
    }

    public static ClubEntity flamengo() {
        return new ClubEntity(2L, FLAMENGO_UUID,"Flamengo", StateMockUtils.rj(),
                LocalDate.of(1950, 7,15), true);
    }

    public static ClubEntity corinthians() {
        return new ClubEntity(3L,CORINTHIANS_UUID, "Corinthians", StateMockUtils.sp(),
                LocalDate.of(1971, 6, 23), true);
    }

    public static ClubEntity customEntity(String name, StateEntity homeState,
                                          LocalDate creationDate, Boolean active) {
        return new ClubEntity(name, homeState, creationDate, active);
    }


    public static ClubResponseDto gremioResponseDto() {
        return new ClubResponseDto(1L, GREMIO_UUID.toString(), "Grêmio", "RS",
                LocalDate.of(2003, 1, 10), true);
    }

    public static ClubResponseDto flamengoResponseDto() {
        return new ClubResponseDto(2L, FLAMENGO_UUID.toString(), "Flamengo", "RJ",
                LocalDate.of(1900, 2, 25), true);
    }

    public static ClubResponseDto corinthiansResponseDto() {
        return new ClubResponseDto(3L, CORINTHIANS_UUID.toString(),"Corinthians", "SP",
                LocalDate.of(1930, 4, 19), true);
    }

    public static ClubResponseDto customResponse(String name, String homeStateCode,
                                               LocalDate creationDate, Boolean active) {
        return new ClubResponseDto(null, null, name, homeStateCode, creationDate, active);
    }


    public static ClubRequestDto gremioRequestDto() {
        return new ClubRequestDto("Grêmio", "RS",
                LocalDate.of(2005, 9, 15), true);
    }

    public static ClubRequestDto customRequest(String name, String stateCode, LocalDate creationDate, Boolean active) {
        return new ClubRequestDto(name, stateCode, creationDate, active);
    }
}
