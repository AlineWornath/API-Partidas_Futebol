package com.neocamp.soccer_matches.testUtils;

import com.neocamp.soccer_matches.dto.stadium.StadiumRequestDto;
import com.neocamp.soccer_matches.dto.stadium.StadiumResponseDto;
import com.neocamp.soccer_matches.entity.StadiumEntity;

public class StadiumMockUtils {
    public static StadiumEntity maracana() {
        return new StadiumEntity(2L, "Maracanã", null);
    }

    public static StadiumEntity morumbi() {
        return new StadiumEntity(3L, "Morumbi", null);
    }

    public static StadiumEntity custom(String name) {
        return new StadiumEntity(name);
    }

    public static StadiumRequestDto maracanaRequestDto() {
        return new StadiumRequestDto("Maracanã", null);
    }

    public static StadiumRequestDto morumbiRequestDto() {
        return new StadiumRequestDto("Morumbi", null);
    }

    public static StadiumRequestDto customRequest(String name) {
        return new StadiumRequestDto(name, null);
    }

    public static StadiumResponseDto maracanaResponseDto() {
        return new StadiumResponseDto(1L, "Maracanã", null);
    }

    public static StadiumResponseDto morumbiResponseDto() {
        return new StadiumResponseDto(2L, "Morumbi", null);
    }

    public static StadiumResponseDto customResponse(String name) {
        return new StadiumResponseDto(2L, name, null);
    }
}
