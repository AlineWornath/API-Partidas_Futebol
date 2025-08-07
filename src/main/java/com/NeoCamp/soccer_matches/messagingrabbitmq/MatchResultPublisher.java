package com.neocamp.soccer_matches.messagingrabbitmq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neocamp.soccer_matches.messagingrabbitmq.dto.MatchResultMessageDto;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
@RequiredArgsConstructor
public class MatchResultPublisher {

    @Value("${queue.match.result}")
    private String valueMatchResult;
    private static final Logger LOG = LoggerFactory.getLogger(MatchResultPublisher.class);
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;
    
    private Schema matchResultSchema;

    @PostConstruct
    public void initSchema() {
        try (InputStream inputStream = getClass().getResourceAsStream("/schema/match_result_schema.json")) {
            JSONObject rawSchema = new JSONObject(new JSONTokener(inputStream));
            this.matchResultSchema = SchemaLoader.load(rawSchema);
        } catch (Exception e) {
            LOG.error("Error loading schema JSON", e);
            throw new RuntimeException("Error loading schema JSON", e);
        }
    }

    public void sendMatchResult(MatchResultMessageDto message) {
        try{
            String json = objectMapper.writeValueAsString(message);

            JSONObject jsonObject = new JSONObject(json);
            matchResultSchema.validate(jsonObject);

            rabbitTemplate.convertAndSend(valueMatchResult, json);

        } catch (org.everit.json.schema.ValidationException e) {
            LOG.error("Error validating match result message: {}", e.getAllMessages());
        } catch (Exception e) {
            LOG.error("Error publishing match result message: {}", e.getMessage());
        }
    }
}
