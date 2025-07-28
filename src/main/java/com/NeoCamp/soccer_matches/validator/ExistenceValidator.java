package com.neocamp.soccer_matches.validator;

import com.neocamp.soccer_matches.enums.StateCodeEnum;
import com.neocamp.soccer_matches.exception.BusinessException;
import com.neocamp.soccer_matches.repository.ClubRepository;
import com.neocamp.soccer_matches.repository.StadiumRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ExistenceValidator {

    private final ClubRepository clubRepository;
    private final StadiumRepository stadiumRepository;

    public void validateClubExists(Long id) {
        if (!clubRepository.existsById(id)) {
            throw new EntityNotFoundException("Club not found: "+ id);
        }
    }

    public void validateClubExistsByUuid(UUID uuid) {
        if (!clubRepository.existsByUuid(uuid)) {
            throw new EntityNotFoundException("Club not found: "+ uuid);
        }
    }

    public StateCodeEnum validateStateCode(String code) {
        try {
            return StateCodeEnum.valueOf(code.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Invalid state code: " + code);
        }
    }

    public void validateStadiumExists(Long id) {
        if (!stadiumRepository.existsById(id)) {
            throw new EntityNotFoundException("Stadium not found: " + id);
        }
    }

    public void validateStadiumExistsByUuid(UUID uuid) {
        if (!stadiumRepository.existsByUuid(uuid)) {
            throw new EntityNotFoundException("Stadium not found: " + uuid);
        }
    }
}
