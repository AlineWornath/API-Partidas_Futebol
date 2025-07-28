package com.neocamp.soccer_matches.messagingrabbitmq.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatchResultMessageDto {
    private String matchId;
    private String winnerId;
    private String winnerName;
    private List<ClubScoreDto> clubScores;
    private LocalDateTime endAt;
}