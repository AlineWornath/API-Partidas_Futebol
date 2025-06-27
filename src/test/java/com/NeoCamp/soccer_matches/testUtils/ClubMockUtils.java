package com.neocamp.soccer_matches.testUtils;

import com.neocamp.soccer_matches.dto.club.ClubRequestDto;
import com.neocamp.soccer_matches.dto.club.ClubResponseDto;
import com.neocamp.soccer_matches.dto.state.StateResponseDto;
import com.neocamp.soccer_matches.entity.ClubEntity;
import com.neocamp.soccer_matches.entity.StateEntity;

import java.time.LocalDate;

public class ClubMockUtils {
    public static ClubEntity gremio() {
        return new ClubEntity(1L, "Grêmio", StateMockUtils.rs(),
                LocalDate.of(1920, 5, 30), true);
    }

    public static ClubEntity flamengo() {
        return new ClubEntity(2L,"Flamengo", StateMockUtils.rj(),
                LocalDate.of(1950, 7,15), true);
    }

    public static ClubEntity corinthians() {
        return new ClubEntity(3L,"Corinthians", StateMockUtils.sp(),
                LocalDate.of(1971, 6, 23), true);
    }

    public static ClubEntity inativoSp() {
        return new ClubEntity("inativo", StateMockUtils.sp(),
                LocalDate.of(2000, 3, 17), false);
    }

    public static ClubEntity customEntity(String name, StateEntity homeState,
                                          LocalDate creationDate, Boolean active) {
        return new ClubEntity(name, homeState, creationDate, active);
    }


    public static ClubResponseDto gremioResponseDto() {
        return new ClubResponseDto(1L, "Grêmio", StateMockUtils.rsDto(),
                LocalDate.of(2003, 1, 10), true);
    }

    public static ClubResponseDto flamengoResponseDto() {
        return new ClubResponseDto(2L, "Flamengo", StateMockUtils.rjDto(),
                LocalDate.of(1900, 2, 25), true);
    }

    public static ClubResponseDto corinthiansResponseDto() {
        return new ClubResponseDto(3L, "Corinthians", StateMockUtils.spDto(),
                LocalDate.of(1930, 4, 19), true);
    }

    public static ClubResponseDto customResponseDto(String name, StateResponseDto homeState,
                                               LocalDate creationDate, Boolean active) {
        return new ClubResponseDto(null, name, homeState, creationDate, active);
    }


    public static ClubRequestDto gremioRequestDto() {
        return new ClubRequestDto("Grêmio", "RS",
                LocalDate.of(2005, 9, 15), true);
    }

    public static ClubRequestDto flamengoRequestDto() {
        return new ClubRequestDto("Flamengo", "RJ",
                LocalDate.of(2003, 1, 10), true);
    }

    public static ClubRequestDto corinthiansRequestDto() {
        return new ClubRequestDto("Corinthians", "SP",
                LocalDate.of(1920, 6, 23), true);
    }

    public static ClubRequestDto customRequestDto(String name, String stateCode, LocalDate creationDate, Boolean active) {
        return new ClubRequestDto(name, stateCode, creationDate, active);
    }
}
