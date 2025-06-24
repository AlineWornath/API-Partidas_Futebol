package com.neocamp.soccer_matches.service;

import com.neocamp.soccer_matches.dto.stadium.StadiumRequestDto;
import com.neocamp.soccer_matches.dto.stadium.StadiumResponseDto;
import com.neocamp.soccer_matches.entity.StadiumEntity;
import com.neocamp.soccer_matches.mapper.StadiumMapper;
import com.neocamp.soccer_matches.repository.StadiumRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StadiumService {
    private final StadiumRepository stadiumRepository;
    private final StadiumMapper stadiumMapper;

    public Page<StadiumResponseDto> findAll(Pageable pageable) {
        Page<StadiumEntity> stadiums = stadiumRepository.findAll(pageable);
        return stadiums.map(StadiumResponseDto::new);
    }

    public StadiumResponseDto findById(Long id) {
        StadiumEntity stadium = stadiumRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Stadium not found: " + id));
        return stadiumMapper.toDto(stadium);
    }

    public StadiumEntity findEntityById(Long id) {
        return stadiumRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Stadium not found: " + id));
    }

    public StadiumResponseDto save(StadiumRequestDto stadiumRequestDto) {
        StadiumEntity stadium = stadiumMapper.toEntity(stadiumRequestDto);
        StadiumEntity savedStadium = stadiumRepository.save(stadium);
        return stadiumMapper.toDto(savedStadium);
    }

    public StadiumResponseDto update(Long id, StadiumRequestDto stadiumRequestDto) {
        StadiumEntity stadium = findEntityById(id);
        stadium.setName(stadiumRequestDto.getName());

        StadiumEntity savedStadium = stadiumRepository.save(stadium);
        return stadiumMapper.toDto(savedStadium);
    }
}
