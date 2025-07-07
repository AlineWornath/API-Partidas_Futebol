package com.neocamp.soccer_matches.valueobject;

import com.neocamp.soccer_matches.enums.StateCode;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Address {
    private String cep;
    private String street;
    private String neighborhood;
    private String city;

    @Enumerated(EnumType.STRING)
    private StateCode stateCode;
}
