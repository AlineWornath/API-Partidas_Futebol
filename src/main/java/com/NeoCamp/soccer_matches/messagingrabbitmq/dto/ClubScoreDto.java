package com.neocamp.soccer_matches.messagingrabbitmq.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ClubScoreDto {
    private String clubId;
    private String clubName;
    private int points;
}
