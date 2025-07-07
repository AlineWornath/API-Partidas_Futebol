package com.neocamp.soccer_matches.controller;

import com.neocamp.soccer_matches.dto.match.MatchRequestDto;
import com.neocamp.soccer_matches.dto.match.MatchResponseDto;
import com.neocamp.soccer_matches.enums.MatchFilterEnum;
import com.neocamp.soccer_matches.service.MatchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/matches")
public class MatchController {

    private final MatchService matchService;

    @GetMapping
    public ResponseEntity<Page<MatchResponseDto>> listMatchesByFilters(
            @RequestParam(required = false) Long clubId,
            @RequestParam(required = false) Long stadiumId,
            @RequestParam(required = false) MatchFilterEnum filter,
            Pageable pageable) {
        Page<MatchResponseDto> matches = matchService.listMatchesByFilters(clubId, stadiumId, filter, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(matches);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MatchResponseDto> findById(@PathVariable Long id) {
        MatchResponseDto match = matchService.findById(id);
        return ResponseEntity.status(HttpStatus.OK).body(match);
    }

    @PostMapping
    public ResponseEntity<MatchResponseDto> create(@RequestBody @Valid MatchRequestDto dto) {
        MatchResponseDto match = matchService.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(match);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MatchResponseDto> update(@PathVariable Long id, @RequestBody @Valid MatchRequestDto dto) {
        MatchResponseDto match = matchService.update(id, dto);
        return ResponseEntity.status(HttpStatus.OK).body(match);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        matchService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
