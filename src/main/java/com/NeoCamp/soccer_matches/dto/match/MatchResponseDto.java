package com.neocamp.soccer_matches.dto.match;

import com.neocamp.soccer_matches.dto.club.ClubResponseDto;
import com.neocamp.soccer_matches.dto.stadium.StadiumResponseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatchResponseDto {
    private Long id;
    private Long homeClubId;
    private String homeClubName;
    private Long awayClubId;
    private String awayClubName;
    private Integer homeGoals;
    private Integer awayGoals;
    private Long stadiumId;
    private String stadiumName;
    private LocalDateTime matchDatetime;
}