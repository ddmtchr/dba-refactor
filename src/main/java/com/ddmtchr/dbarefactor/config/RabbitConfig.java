package com.ddmtchr.dbarefactor.config;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

@Configuration
public class RabbitConfig {

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);

        template.setRetryTemplate(retryTemplate());

        template.setConfirmCallback((correlationData, ack, cause) -> {
            if (!ack) {
                throw new IllegalStateException("RabbitMQ nack: " + cause);
            }
        });

        return template;
    }

    private RetryTemplate retryTemplate() {
        RetryTemplate retry = new RetryTemplate();

        retry.setRetryPolicy(new SimpleRetryPolicy(5));

        ExponentialBackOffPolicy backOff = new ExponentialBackOffPolicy();
        backOff.setInitialInterval(1000);
        backOff.setMultiplier(2);
        backOff.setMaxInterval(10000);

        retry.setBackOffPolicy(backOff);
        return retry;
    }
}
