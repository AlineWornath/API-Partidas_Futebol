package com.neocamp.soccer_matches.messagingrabbitmq.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClubScoreDto {
    private String clubId;
    private String clubName;
    private int goals;
}
