package com.neocamp.soccer_matches.dto.stadium;

import com.neocamp.soccer_matches.entity.StadiumEntity;
import com.neocamp.soccer_matches.valueobject.Address;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StadiumResponseDto {
    private Long id;
    private String name;
    private Address address;

    public StadiumResponseDto(StadiumEntity stadium) {
        this.id = stadium.getId();
        this.name = stadium.getName();
        this.address = stadium.getAddress();
    }
}
