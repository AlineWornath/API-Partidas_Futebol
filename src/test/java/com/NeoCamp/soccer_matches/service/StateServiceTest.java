package com.neocamp.soccer_matches.service;

import com.neocamp.soccer_matches.entity.StateEntity;
import com.neocamp.soccer_matches.enums.StateCode;
import com.neocamp.soccer_matches.exception.BusinessException;
import com.neocamp.soccer_matches.repository.StateRepository;
import com.neocamp.soccer_matches.testUtils.StateMockUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class StateServiceTest {

    @Mock
    private StateRepository stateRepository;

    @InjectMocks
    private StateService stateService;

    @Test
    public void shouldFindStateByCodeSuccessfully() {
        StateEntity saoPaulo = StateMockUtils.sp();
        StateCode code = StateCode.SP;

        Mockito.when(stateRepository.findByCode(code)).thenReturn(Optional.of(saoPaulo));

        StateEntity result = stateService.findByCode(code);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("São Paulo", result.getName());
    }

    @Test
    public void shouldThrowException_whenInvalidStateCode() {
        StateCode code = StateCode.SP;

        Mockito.when(stateRepository.findByCode(code)).thenReturn(Optional.empty());

        BusinessException exception = Assertions.assertThrows(BusinessException.class,
                () -> stateService.findByCode(code));

        Assertions.assertNotNull(exception);
        Assertions.assertTrue(exception.getMessage().contains("Invalid state code: "));
    }
}
