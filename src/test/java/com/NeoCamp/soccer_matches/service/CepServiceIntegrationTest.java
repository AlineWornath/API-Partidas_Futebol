package com.neocamp.soccer_matches.service;

import com.neocamp.soccer_matches.exception.BusinessException;
import com.neocamp.soccer_matches.valueobject.Address;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CepServiceIntegrationTest {

    @Autowired
    private CepService cepService;

    @Test
    public void shouldReturnAddress_whenValidCep() {
        String cep = "04029-000";
        Address address = cepService.buildAddressFromCep(cep);

        Assertions.assertNotNull(address);
        Assertions.assertEquals("04029-000", address.getCep());
        Assertions.assertNotNull(address.getStreet());
        Assertions.assertEquals("São Paulo", address.getCity());
    }

    @Test
    public void shouldThrowException_whenInvalidCep() {
        String invalidCep = "00000-000";

        BusinessException exception = Assertions.assertThrows(BusinessException.class,
                () -> cepService.buildAddressFromCep(invalidCep));

        Assertions.assertNotNull(exception);
        Assertions.assertTrue(exception.getMessage().contains("CEP not found or invalid"));
    }
}
