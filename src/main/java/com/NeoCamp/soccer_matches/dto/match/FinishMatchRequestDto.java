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
public class FinishMatchRequestDto {

    @NotNull(message = "Field home goals is required")
    @Min(value = 0, message = "Field home goals cannot be negative")
    private Integer homeGoals;

    @NotNull(message = "Field away goals is required")
    @Min(value = 0, message = "Field away goals cannot be negative")
    private Integer awayGoals;

    private LocalDateTime endAt;
}
