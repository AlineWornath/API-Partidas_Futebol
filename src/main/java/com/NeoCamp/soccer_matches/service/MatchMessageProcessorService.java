package com.neocamp.soccer_matches.service;

import com.neocamp.soccer_matches.entity.ClubEntity;
import com.neocamp.soccer_matches.entity.MatchEntity;
import com.neocamp.soccer_matches.entity.StadiumEntity;
import com.neocamp.soccer_matches.enums.MatchStatusEnum;
import com.neocamp.soccer_matches.messagingrabbitmq.dto.FinishMatchMessageDto;
import com.neocamp.soccer_matches.messagingrabbitmq.dto.MatchInfoMessageDto;
import com.neocamp.soccer_matches.validator.ExistenceValidator;
import com.neocamp.soccer_matches.dto.match.FinishMatchRequestDto;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MatchMessageProcessorService {
    private final MatchService matchService;
    private final ClubService clubService;
    private final StadiumService stadiumService;
    private final ExistenceValidator existenceValidator;
    private final Logger LOG = LoggerFactory.getLogger(MatchMessageProcessorService.class);
    
    @Transactional
    public void processMatchInfoMessage(MatchInfoMessageDto messageDto) {
        LOG.info("Processing MatchInfoMessageDto: {}", messageDto.getMatchId());
        validateStatus(messageDto);

        UUID matchUuid = parseUuid(messageDto.getMatchId(), "matchId");
        UUID homeClubUuid = parseUuid(messageDto.getHomeClubId(), "homeClubId");
        UUID awayClubUuid = parseUuid(messageDto.getAwayClubId(), "awayClubId");
        UUID stadiumUuid = parseUuid(messageDto.getStadiumId(), "stadiumId");

        existenceValidator.validateClubExistsByUuid(homeClubUuid);
        existenceValidator.validateClubExistsByUuid(awayClubUuid);
        existenceValidator.validateStadiumExistsByUuid(stadiumUuid);

        ClubEntity homeClub = clubService.findByUuid(homeClubUuid);
        ClubEntity awayClub = clubService.findByUuid(awayClubUuid);
        StadiumEntity stadium = stadiumService.findByUuid(stadiumUuid);

        MatchEntity match = matchService.findOrCreateMatch(matchUuid, messageDto, homeClub, awayClub, stadium);
        LOG.info("Match saved: uuid={}, status={}", match.getUuid(), match.getStatus());
    }
    
    @Transactional
    public void processFinishMatchMessage(FinishMatchMessageDto messageDto) {
        LOG.info("Processing message match.finish: matchId={}", messageDto.getMatchId());
        FinishMatchRequestDto requestDto = new FinishMatchRequestDto(
            messageDto.getHomeGoals(),
            messageDto.getAwayGoals(),
            messageDto.getEndAt()
        );
        matchService.finish(messageDto.getMatchId(), requestDto);
        
        LOG.info("Match finished successfully: matchId={}", messageDto.getMatchId());
    }

    private void validateStatus(MatchInfoMessageDto messageDto) {
        MatchStatusEnum status = messageDto.getStatus();
        if (status == MatchStatusEnum.FINISHED) {
            LOG.error("Rejected message in 'match.info' queue: status FINISHED is not allowed. matchId={}, payload={}",
                    messageDto.getMatchId(), messageDto);
            throw new IllegalArgumentException("Cannot process match with status FINISHED in 'match.info' queue.");
        }
    }

    private UUID parseUuid(String uuidStr, String fieldName) {
        try{
            return UUID.fromString(uuidStr);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(fieldName + " is not a valid UUID: " + uuidStr, e);
        }
    }
}
