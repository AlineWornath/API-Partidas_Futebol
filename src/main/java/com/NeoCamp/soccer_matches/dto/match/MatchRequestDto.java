package com.neocamp.soccer_matches.dto.match;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatchRequestDto {

    @NotNull(message = "Field home club is required")
    private Long homeClubId;

    @NotNull(message = "Field away club is required")
    private Long awayClubId;

    @NotNull(message = "Field home goals is required")
    @Min(value = 0, message = "Field home goals cannot be negative")
    private Integer homeGoals;

    @NotNull(message = "Field away goals is required")
    @Min(value = 0, message = "Field home goals cannot be negative")
    private Integer awayGoals;

    @NotNull(message = "Field stadium is required")
    private Long stadiumId;

    @NotNull(message = "Field match date and time is required")
    private LocalDateTime matchDatetime;
}