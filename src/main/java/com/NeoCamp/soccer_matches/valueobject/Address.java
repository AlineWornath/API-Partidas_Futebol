package com.neocamp.soccer_matches.valueobject;

import com.neocamp.soccer_matches.enums.StateCodeEnum;
import jakarta.persistence.Embeddable;
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
    private StateCodeEnum stateCodeEnum;
}
