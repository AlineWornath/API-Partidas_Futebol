package com.neocamp.soccer_matches.testUtils;

import com.neocamp.soccer_matches.dto.stadium.StadiumRequestDto;
import com.neocamp.soccer_matches.dto.stadium.StadiumResponseDto;
import com.neocamp.soccer_matches.entity.StadiumEntity;
import com.neocamp.soccer_matches.valueobject.Address;

public class StadiumMockUtils {
    public static StadiumEntity maracana() {
        Address address = new Address();
        address.setCity("Rio de Janeiro");
        return new StadiumEntity(2L, "Maracanã", address);
    }

    public static StadiumEntity morumbi() {
        Address address = new Address();
        address.setCity("São Paulo");
        return new StadiumEntity(3L, "Morumbi", address);
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
