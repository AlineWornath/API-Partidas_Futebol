package com.neocamp.soccer_matches.messagingrabbitmq.dto;

import com.neocamp.soccer_matches.enums.MatchStatusEnum;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatchInfoMessageDto {

    @NotNull
    private String matchUuid;

    @NotNull
    private String homeClubUuid;

    @NotNull
    private String awayClubUuid;

    @NotNull
    private String stadiumUuid;

    @NotNull
    private LocalDateTime matchDatetime;

    @NotNull
    private MatchStatusEnum status;
}