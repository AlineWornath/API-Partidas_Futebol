package com.neocamp.soccer_matches.testUtils;

import com.neocamp.soccer_matches.dto.club.ClubRequestDto;
import com.neocamp.soccer_matches.dto.club.ClubResponseDto;
import com.neocamp.soccer_matches.dto.match.MatchRequestDto;
import com.neocamp.soccer_matches.dto.match.MatchResponseDto;
import com.neocamp.soccer_matches.dto.stadium.StadiumRequestDto;
import com.neocamp.soccer_matches.dto.stadium.StadiumResponseDto;
import com.neocamp.soccer_matches.entity.ClubEntity;
import com.neocamp.soccer_matches.entity.MatchEntity;
import com.neocamp.soccer_matches.entity.StadiumEntity;
import com.neocamp.soccer_matches.entity.StateEntity;
import com.neocamp.soccer_matches.enums.MatchStatusEnum;
import com.neocamp.soccer_matches.enums.StateCodeEnum;
import com.neocamp.soccer_matches.repository.StateRepository;
import com.neocamp.soccer_matches.service.ClubService;
import com.neocamp.soccer_matches.service.MatchService;
import com.neocamp.soccer_matches.service.StadiumService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class IntegrationTestUtils {
    private final ClubService clubService;
    private final StadiumService stadiumService;
    private final MatchService matchService;
    private final StateRepository stateRepository;

    public ClubEntity createClub(String name, String state, LocalDate creationDate, Boolean active) {
        ClubRequestDto requestDto = new ClubRequestDto(name, state, creationDate, active);
        ClubResponseDto saved = clubService.save(requestDto);
        return clubService.findEntityById(saved.getId());
    }

    public StadiumEntity createStadium(String name, String cep) {
        StadiumRequestDto requestDto = new StadiumRequestDto(name, cep);
        StadiumResponseDto saved = stadiumService.save(requestDto);
        return stadiumService.findEntityById(saved.getId());
    }
    
    public MatchEntity createMatch(ClubEntity homeClub, ClubEntity awayClub, int homeGoals, int awayGoals,
                                   StadiumEntity stadium, LocalDateTime matchDateTime, MatchStatusEnum status) {
        MatchRequestDto requestDto = new MatchRequestDto(homeClub.getId(), awayClub.getId(), homeGoals, awayGoals,
                stadium.getId(), matchDateTime);
        
        MatchResponseDto saved = matchService.save(requestDto);
        return matchService.findEntityById(saved.getId());
    }
    
    public StateEntity createState(String name, StateCodeEnum code) {
        StateEntity state = new StateEntity(name, code);
        return stateRepository.save(state);
    }
    
    public StateEntity getOrCreateState(StateCodeEnum code) {
        return stateRepository.findByCode(code)
                .orElseGet(() -> {
                    String name = getStateNameFromCode(code);
                    return createState(name, code);
                });
    }
    
    public StateEntity getStateOrFail(StateCodeEnum code) {
        return StateTestUtils.getStateOrFail(stateRepository, code);
    }
    
    private String getStateNameFromCode(StateCodeEnum code) {
        return switch (code) {
            case RS -> "Rio Grande do Sul";
            case SP -> "São Paulo";
            case RJ -> "Rio de Janeiro";
            case MG -> "Minas Gerais";
            case PR -> "Paraná";
            case SC -> "Santa Catarina";
            default -> code.name();
        };
    }
}
