package com.neocamp.soccer_matches.testUtils;

import com.neocamp.soccer_matches.dto.stadium.StadiumRequestDto;
import com.neocamp.soccer_matches.dto.stadium.StadiumResponseDto;
import com.neocamp.soccer_matches.entity.StadiumEntity;

public class StadiumMockUtils {
    public static StadiumEntity maracana() {
        return new StadiumEntity("Maracanã");
    }

    public static StadiumEntity morumbi() {
        return new StadiumEntity("Morumbi");
    }

    public static StadiumEntity custom(String name) {
        return new StadiumEntity(name);
    }

    public static StadiumRequestDto maracanaRequestDto() {
        return new StadiumRequestDto("Maracanã");
    }

    public static StadiumRequestDto morumbiRequestDto() {
        return new StadiumRequestDto("Morumbi");
    }

    public static StadiumRequestDto customRequestDto(String name) {
        return new StadiumRequestDto(name);
    }

    public static StadiumResponseDto maracanaResponseDto() {
        return new StadiumResponseDto(1L, "Maracanã");
    }

    public static StadiumResponseDto morumbiResponseDto() {
        return new StadiumResponseDto(2L, "Morumbi");
    }

    public static StadiumResponseDto customResponseDto(String name) {
        return new StadiumResponseDto(2L, name);
    }
}
