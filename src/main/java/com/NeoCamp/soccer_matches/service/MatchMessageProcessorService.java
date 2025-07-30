package com.neocamp.soccer_matches.service;

import com.neocamp.soccer_matches.entity.MatchEntity;
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

import java.util.Optional;
import java.util.UUID;

import static com.neocamp.soccer_matches.utils.UuidUtils.parseUuid;

@Service
@RequiredArgsConstructor
public class MatchMessageProcessorService {
    private final MatchService matchService;
    private final ClubService clubService;
    private final StadiumService stadiumService;
    private final ExistenceValidator existenceValidator;
    private final Logger LOG = LoggerFactory.getLogger(MatchMessageProcessorService.class);
    
    @Transactional
    public void processMatchInfoMessage(MatchInfoMessageDto dto) {
        LOG.info("Processing MatchInfoMessageDto: {}", dto.getMatchUuid());
        validateStatus(dto);

        UUID matchUuid = parseUuid(dto.getMatchUuid(), "matchId");
        Optional<MatchEntity> existingMatch = matchService.findByUuid(matchUuid);

        if (existingMatch.isPresent()) {
            matchService.updateFromMessageDto(dto.getMatchUuid(), dto);
            LOG.info("Match updated: uuid={}, status={}", matchUuid, dto.getStatus());
        } else {
            MatchEntity entity = matchService.assembleMatchFromInfoMessageDto(dto);
            matchService.save(entity);
            LOG.info("Match saved: uuid={}, status={}", entity.getUuid(), entity.getStatus());
        }
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
                    messageDto.getMatchUuid(), messageDto);
            throw new IllegalArgumentException("Cannot process match with status FINISHED in 'match.info' queue.");
        }
    }
}
