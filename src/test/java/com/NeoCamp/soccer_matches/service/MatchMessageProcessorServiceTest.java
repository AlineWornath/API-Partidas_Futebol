package com.neocamp.soccer_matches.service;

import com.neocamp.soccer_matches.entity.ClubEntity;
import com.neocamp.soccer_matches.entity.MatchEntity;
import com.neocamp.soccer_matches.entity.StadiumEntity;
import com.neocamp.soccer_matches.enums.MatchStatusEnum;
import com.neocamp.soccer_matches.messagingrabbitmq.dto.FinishMatchMessageDto;
import com.neocamp.soccer_matches.messagingrabbitmq.dto.MatchInfoMessageDto;
import com.neocamp.soccer_matches.dto.match.FinishMatchRequestDto;
import com.neocamp.soccer_matches.testUtils.ClubMockUtils;
import com.neocamp.soccer_matches.testUtils.MatchMockUtils;
import com.neocamp.soccer_matches.testUtils.StadiumMockUtils;
import com.neocamp.soccer_matches.validator.ExistenceValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MatchMessageProcessorServiceTest {

    @Mock
    private MatchService matchService;
    
    @Mock
    private ClubService clubService;
    
    @Mock
    private StadiumService stadiumService;
    
    @Mock
    private ExistenceValidator existenceValidator;
    
    @InjectMocks
    private MatchMessageProcessorService messageProcessor;
    
    private ClubEntity flamengoEntity;
    private ClubEntity corinthiansEntity;
    private StadiumEntity maracanaEntity;
    
    @BeforeEach
    public void setUp() {
        flamengoEntity = ClubMockUtils.flamengo();
        corinthiansEntity = ClubMockUtils.corinthians();
        maracanaEntity = StadiumMockUtils.maracana();
    }
    
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

        MatchEntity expectedMatch = MatchMockUtils.flamengoVsCorinthiansAtMaracana();
        expectedMatch.setUuid(matchId);

        Mockito.doNothing().when(existenceValidator).validateClubExistsByUuid(homeClubId);
        Mockito.doNothing().when(existenceValidator).validateClubExistsByUuid(awayClubId);
        Mockito.doNothing().when(existenceValidator).validateStadiumExistsByUuid(stadiumId);

        when(clubService.findByUuid(homeClubId)).thenReturn(flamengoEntity);
        when(clubService.findByUuid(awayClubId)).thenReturn(corinthiansEntity);
        when(stadiumService.findByUuid(stadiumId)).thenReturn(maracanaEntity);
        when(matchService.findOrCreateMatch(matchId, messageDto, flamengoEntity, corinthiansEntity, maracanaEntity))
                .thenReturn(expectedMatch);

        messageProcessor.processMatchInfoMessage(messageDto);

        verify(matchService).findOrCreateMatch(matchId, messageDto, flamengoEntity, corinthiansEntity, maracanaEntity);
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
