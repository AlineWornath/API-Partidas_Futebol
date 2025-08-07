package com.neocamp.soccer_matches.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.neocamp.soccer_matches.entity.ClubEntity;
import com.neocamp.soccer_matches.entity.MatchEntity;
import com.neocamp.soccer_matches.entity.StadiumEntity;
import com.neocamp.soccer_matches.enums.MatchStatusEnum;
import com.neocamp.soccer_matches.messagingrabbitmq.dto.ClubScoreDto;
import com.neocamp.soccer_matches.messagingrabbitmq.dto.FinishMatchMessageDto;
import com.neocamp.soccer_matches.messagingrabbitmq.dto.MatchInfoMessageDto;
import com.neocamp.soccer_matches.messagingrabbitmq.dto.MatchResultMessageDto;
import com.neocamp.soccer_matches.service.MatchService;
import com.neocamp.soccer_matches.testUtils.IntegrationTestUtils;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@ActiveProfiles("test")
public class MatchMessagingIntegrationTest {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private MatchService matchService;

    @Autowired
    private IntegrationTestUtils testUtils;

    private ClubEntity gremio, saoPaulo;
    private StadiumEntity morumbi;

    @BeforeEach
    public void setup() {
        gremio = testUtils.createClub("Grêmio", "RS", LocalDate.of(2003, 1, 10), true);
        saoPaulo = testUtils.createClub("Sao Paulo", "SP", LocalDate.of(1999, 11, 10), true);
        morumbi = testUtils.createStadium("Morumbi", "05653-070");
    }

    @Test
    public void shouldCreateAndFinalizeMatchAndPublishResultViaRabbitMq() {
        ClubEntity homeClub = saoPaulo;
        ClubEntity awayClub = gremio;
        StadiumEntity stadium = morumbi;
        Integer homeGoals = 2;
        Integer awayGoals = 3;
        UUID matchId = UUID.randomUUID();
        LocalDateTime matchDateTime = LocalDateTime.now();

        MatchInfoMessageDto messageDto = new MatchInfoMessageDto(
                matchId.toString(),
                homeClub.getUuid().toString(),
                awayClub.getUuid().toString(),
                stadium.getUuid().toString(),
                matchDateTime,
                MatchStatusEnum.IN_PROGRESS
        );
        rabbitTemplate.convertAndSend("match.info", messageDto);

        Awaitility.await()
                .atMost(5, TimeUnit.SECONDS)
                .pollInterval(500, TimeUnit.MILLISECONDS)
                .untilAsserted(() -> {
                    MatchEntity match = matchService.findByUuidOrThrow(matchId);
                    Assertions.assertEquals(MatchStatusEnum.IN_PROGRESS, match.getStatus());
                    Assertions.assertEquals(homeClub.getId(), match.getHomeClub().getId());
                    Assertions.assertEquals(awayClub.getId(), match.getAwayClub().getId());
                    Assertions.assertEquals(stadium.getId(), match.getStadium().getId());
                });

        LocalDateTime finishDateTime = LocalDateTime.now();
        FinishMatchMessageDto finishDto = new FinishMatchMessageDto(matchId.toString(), homeGoals, awayGoals,
                finishDateTime);
        rabbitTemplate.convertAndSend("match.finish", finishDto);

        Awaitility.await()
                .atMost(5, TimeUnit.SECONDS)
                .pollInterval(500, TimeUnit.MILLISECONDS)
                .untilAsserted(() -> {
                    MatchEntity match = matchService.findByUuidOrThrow(matchId);
                    Assertions.assertEquals(MatchStatusEnum.FINISHED, match.getStatus());
                    Assertions.assertEquals(homeGoals, match.getHomeGoals());
                    Assertions.assertEquals(awayGoals, match.getAwayGoals());
                });

        Awaitility.await()
                .atMost(5, TimeUnit.SECONDS)
                .pollInterval(500, TimeUnit.MILLISECONDS)
                .untilAsserted(() -> {
                    String jsonMessage = (String) rabbitTemplate.receiveAndConvert("match.result");
                    Assertions.assertNotNull(jsonMessage);

                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.registerModule(new JavaTimeModule());
                    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
                    MatchResultMessageDto resultDto = objectMapper.readValue(jsonMessage, MatchResultMessageDto.class);

                    Assertions.assertEquals(matchId.toString(), resultDto.getMatchId());
                    Assertions.assertEquals(gremio.getUuid().toString(), resultDto.getWinnerId());
                    Assertions.assertEquals(gremio.getName(), resultDto.getWinnerName());

                    List<ClubScoreDto> clubScores = resultDto.getClubScores();
                    Assertions.assertEquals(2, clubScores.size());

                    ClubScoreDto homeScore = clubScores.stream()
                            .filter(score -> score.getClubId().equals(homeClub.getUuid().toString()))
                            .findFirst()
                            .orElseThrow();
                    ClubScoreDto awayScore = clubScores.stream()
                            .filter(score -> score.getClubId().equals(awayClub.getUuid().toString()))
                            .findFirst()
                            .orElseThrow();

                    Assertions.assertEquals(homeGoals, homeScore.getGoals());
                    Assertions.assertEquals(awayGoals, awayScore.getGoals());
                });
    }
}
