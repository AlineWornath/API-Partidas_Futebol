package com.neocamp.soccer_matches.repository;

import com.neocamp.soccer_matches.entity.StateEntity;
import com.neocamp.soccer_matches.enums.StateCodeEnum;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

@DataJpaTest
public class StateRepositoryTest {

    @Autowired
    private StateRepository stateRepository;

    @Test
    public void shouldFindStateByCode_whenExists(){
        Optional<StateEntity> foundState = stateRepository.findByCode(StateCodeEnum.SP);

        Assertions.assertTrue(foundState.isPresent());
        Assertions.assertEquals(StateCodeEnum.SP, foundState.get().getCode());
        Assertions.assertEquals("São Paulo", foundState.get().getName());
    }
}
