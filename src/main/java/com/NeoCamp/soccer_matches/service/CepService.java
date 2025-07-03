package com.neocamp.soccer_matches.service;

import com.neocamp.soccer_matches.enums.StateCode;
import com.neocamp.soccer_matches.exception.BusinessException;
import com.neocamp.soccer_matches.integration.ViaCepClient;
import com.neocamp.soccer_matches.integration.ViaCepResponse;
import com.neocamp.soccer_matches.valueobject.Address;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CepService {

    private final ViaCepClient viaCepClient;

    public Address buildAddressFromCep(String cep) {
        ViaCepResponse response = viaCepClient.consultarCep(cep);

        if (response == null || response.getCep() == null) {
            throw new BusinessException("CEP not found or invalid.");
        }
        return new Address(
                response.getCep(),
                response.getLogradouro(),
                response.getBairro(),
                response.getLocalidade(),
                StateCode.valueOf(response.getUf().toUpperCase()));
    }
}
