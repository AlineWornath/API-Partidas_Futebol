package com.neocamp.soccer_matches.service;

import com.neocamp.soccer_matches.entity.StateEntity;
import com.neocamp.soccer_matches.enums.StateCodeEnum;
import com.neocamp.soccer_matches.exception.BusinessException;
import com.neocamp.soccer_matches.repository.StateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StateService {
    private final StateRepository stateRepository;

    public StateEntity findByCode(StateCodeEnum code) {
        return stateRepository.findByCode(code)
                .orElseThrow(() -> new BusinessException("Invalid state code: " + code));
    }
}
