package com.neocamp.soccer_matches.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.core.Queue;

@Configuration
public class RabbitMQConfig {

    @Value("${queue.match.info}")
    private String valueMatchInfo;

    @Value("${queue.match.finish}")
    private String valueMatchFinish;

    @Value("${queue.match.result}")
    private String valueMatchResult;

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return new Jackson2JsonMessageConverter(objectMapper);
    }
    
    @Bean
    public Queue matchInfoQueue() {
        return new Queue(valueMatchInfo, true);
    }

    @Bean
    public Queue matchFinishQueue() {
        return new Queue(valueMatchFinish, true);
    }

    @Bean
    public Queue matchResultQueue() {
        return new Queue(valueMatchResult, true);
    }
}
