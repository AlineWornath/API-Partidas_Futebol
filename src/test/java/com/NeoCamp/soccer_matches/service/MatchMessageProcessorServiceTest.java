package com.neocamp.soccer_matches.service;

import com.neocamp.soccer_matches.entity.MatchEntity;
import com.neocamp.soccer_matches.enums.MatchStatusEnum;
import com.neocamp.soccer_matches.messagingrabbitmq.dto.FinishMatchMessageDto;
import com.neocamp.soccer_matches.messagingrabbitmq.dto.MatchInfoMessageDto;
import com.neocamp.soccer_matches.dto.match.FinishMatchRequestDto;
import com.neocamp.soccer_matches.testUtils.ClubMockUtils;
import com.neocamp.soccer_matches.testUtils.MatchMockUtils;
import com.neocamp.soccer_matches.testUtils.StadiumMockUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class MatchMessageProcessorServiceTest {

    @Mock
    private MatchService matchService;
    
    @InjectMocks
    private MatchMessageProcessorService messageProcessor;

    @Test
    public void shouldCreateMatch_whenProcessMatchInfoMessageWithNewUuid() {
        UUID matchId = UUID.randomUUID();
        UUID homeClubId = ClubMockUtils.FLAMENGO_UUID;
        UUID awayClubId = ClubMockUtils.CORINTHIANS_UUID;
        UUID stadiumId = StadiumMockUtils.MARACANA_UUID;
        LocalDateTime matchDateTime = LocalDateTime.now();
        MatchStatusEnum status = MatchStatusEnum.SCHEDULED;

        MatchInfoMessageDto messageDto = new MatchInfoMessageDto(matchId.toString(), homeClubId.toString(),
                awayClubId.toString(), stadiumId.toString(), matchDateTime, status);

        MatchEntity entity = MatchMockUtils.flamengoVsCorinthiansAtMaracana();
       entity.setUuid(matchId);

        Mockito.when(matchService.findByUuid(matchId)).thenReturn(Optional.empty());
        Mockito.when(matchService.assembleMatchFromInfoMessageDto(messageDto)).thenReturn(entity);

        messageProcessor.processMatchInfoMessage(messageDto);

        verify(matchService).save(entity);
    }
    
    @Test
    public void shouldThrowException_whenProcessMatchInfoMessageWithFinishedStatus() {
        UUID matchId = UUID.randomUUID();
        UUID homeClubId = ClubMockUtils.FLAMENGO_UUID;
        UUID awayClubId = ClubMockUtils.CORINTHIANS_UUID;
        UUID stadiumId = StadiumMockUtils.MARACANA_UUID;
        LocalDateTime matchDateTime = LocalDateTime.now();
        MatchStatusEnum status = MatchStatusEnum.FINISHED;

        MatchInfoMessageDto messageDto = new MatchInfoMessageDto(matchId.toString(), homeClubId.toString(),
                awayClubId.toString(), stadiumId.toString(), matchDateTime, status);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            messageProcessor.processMatchInfoMessage(messageDto));
        
        assertEquals("Cannot process match with status FINISHED in 'match.info' queue.", exception.getMessage());
    }
    
    @Test
    public void shouldCallMatchServiceFinish_whenProcessFinishMatchMessage() {
        String matchId = UUID.randomUUID().toString();
        int homeGoals = 2;
        int awayGoals = 1;
        LocalDateTime endAt = LocalDateTime.now();
        
        FinishMatchMessageDto messageDto = new FinishMatchMessageDto(matchId, homeGoals, awayGoals, endAt);

        messageProcessor.processFinishMatchMessage(messageDto);

        ArgumentCaptor<FinishMatchRequestDto> requestDtoCaptor = ArgumentCaptor.forClass(FinishMatchRequestDto.class);
        verify(matchService).finish(Mockito.eq(matchId), requestDtoCaptor.capture());
        
        FinishMatchRequestDto capturedDto = requestDtoCaptor.getValue();
        assertEquals(homeGoals, capturedDto.getHomeGoals());
        assertEquals(awayGoals, capturedDto.getAwayGoals());
        assertEquals(endAt, capturedDto.getEndAt());
    }
}
