package com.neocamp.soccer_matches.messagingrabbitmq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neocamp.soccer_matches.messagingrabbitmq.dto.MatchInfoMessageDto;
import com.neocamp.soccer_matches.service.MatchMessageProcessorService;
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
public class MatchInfoListener {

    private static final Logger LOG = LoggerFactory.getLogger(MatchInfoListener.class);
    private final MatchMessageProcessorService messageProcessor;
    private final ObjectMapper objectMapper;
    private Schema matchInfoSchema;

    @PostConstruct
    public void initSchema() {
        try (InputStream inputStream = getClass().getResourceAsStream("/schema/match_info_schema.json")) {
            JSONObject rawSchema = new JSONObject(new JSONTokener(inputStream));
            this.matchInfoSchema = SchemaLoader.load(rawSchema);
        } catch (Exception e) {
            LOG.error("Error loading schema JSON", e);
            throw new RuntimeException("Error loading schema JSON", e);
        }
    }

    @RabbitListener(queues = "${queue.match.info}")
    public void receiveMatchInfo(MatchInfoMessageDto message) {
        try {
            String messageJson = objectMapper.writeValueAsString(message);
            JSONObject jsonObject = new JSONObject(messageJson);
            this.matchInfoSchema.validate(jsonObject);

            messageProcessor.processMatchInfoMessage(message);
        } catch (org.everit.json.schema.ValidationException e) {
            LOG.error("Invalid match info message: {}", e.getAllMessages());
        } catch (Exception e) {
            LOG.error("Error processing match info message", e);
            throw new RuntimeException(e);
        }
    }
}
