package com.neocamp.soccer_matches.testUtils;

import com.neocamp.soccer_matches.entity.StateEntity;
import com.neocamp.soccer_matches.enums.StateCodeEnum;
import com.neocamp.soccer_matches.repository.StateRepository;

public class StateTestUtils {

    public static StateEntity getStateOrFail(StateRepository repository, StateCodeEnum code){
        return repository.findByCode(code).orElseThrow(
                ()->new AssertionError("State with code " + code + " not found in test database"));
    }
}
