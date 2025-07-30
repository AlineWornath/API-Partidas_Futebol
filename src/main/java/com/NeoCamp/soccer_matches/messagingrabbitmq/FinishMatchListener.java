package com.neocamp.soccer_matches.messagingrabbitmq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neocamp.soccer_matches.dto.match.FinishMatchRequestDto;
import com.neocamp.soccer_matches.messagingrabbitmq.dto.FinishMatchMessageDto;
import com.neocamp.soccer_matches.service.MatchService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
@RequiredArgsConstructor
public class FinishMatchListener {

    private static final Logger LOG = LoggerFactory.getLogger(FinishMatchListener.class);
    private final MatchService matchService;
    private final ObjectMapper objectMapper;
    private Schema finishMatchSchema;

    @PostConstruct
    public void initSchema() {
        try (InputStream inputStream = getClass().getResourceAsStream("/schema/finish_match_schema.json")) {
            JSONObject rawSchema = new JSONObject(new JSONTokener(inputStream));
            this.finishMatchSchema = SchemaLoader.load(rawSchema);
        } catch (Exception e) {
            LOG.error("Error loading schema JSON", e);
            throw new RuntimeException("Error loading schema JSON", e);
        }
    }

    @RabbitListener(queues = "${queue.match.finish}")
    public void handleFinishMatch(FinishMatchMessageDto message) {
        LOG.info("Received finish match message: matchId={}, homeGoals={}, awayGoals={}, endAt={}", 
                message.getMatchId(), message.getHomeGoals(), message.getAwayGoals(), message.getEndAt());
        
        try {
            String messageJson = objectMapper.writeValueAsString(message);
            JSONObject jsonObject = new JSONObject(messageJson);
            finishMatchSchema.validate(jsonObject);

            matchService.finish(message.getMatchId(), new FinishMatchRequestDto(
                    message.getHomeGoals(), message.getAwayGoals(), message.getEndAt()));
        } catch (org.everit.json.schema.ValidationException e) {
            LOG.error("Invalid finish match message: {}", e.getAllMessages());
        }
        catch (Exception e) {
            LOG.error("Error processing finish match message:", e);
            throw new RuntimeException(e);
        }
    }
}
