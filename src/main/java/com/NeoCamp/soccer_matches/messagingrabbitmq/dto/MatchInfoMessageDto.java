package com.neocamp.soccer_matches.messagingrabbitmq.dto;

import com.neocamp.soccer_matches.enums.MatchStatusEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class MatchInfoMessageDto {
    private String matchId;
    private String homeClubId;
    private String awayClubId;
    private LocalDateTime startAt;
    private MatchStatusEnum status;
}