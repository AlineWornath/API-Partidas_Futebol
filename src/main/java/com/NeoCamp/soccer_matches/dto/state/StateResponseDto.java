package com.neocamp.soccer_matches.dto.state;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StateResponseDto {
    private Long id;
    private String name;
    private String code;
}
