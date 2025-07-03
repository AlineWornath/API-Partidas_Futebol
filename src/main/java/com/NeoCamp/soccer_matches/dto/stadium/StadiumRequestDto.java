package com.neocamp.soccer_matches.dto.stadium;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StadiumRequestDto {

    @NotBlank(message = "Field name is required")
    @Size(min = 3, max = 100, message = "Field name must be between 3 and 100 characters")
    private String name;

    @Pattern(regexp = "^\\d{5}-?\\d{3}$", message = "Invalid CEP")
    private String cep;
}
