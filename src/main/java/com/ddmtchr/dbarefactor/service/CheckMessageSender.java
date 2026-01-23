package com.ddmtchr.dbarefactor.service;

import com.ddmtchr.dbarefactor.dto.CheckMessageBatch;
import com.ddmtchr.dbarefactor.entity.CheckMessage;
import com.ddmtchr.dbarefactor.exception.MessageSendException;
import com.ddmtchr.dbarefactor.mapper.CheckMessageMapper;
import com.ddmtchr.dbarefactor.service.producer.AmqpMessageSender;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CheckMessageSender {

    private static final int BATCH_SIZE = 100;

    private final AmqpMessageSender messageSender;
    private final ObjectMapper objectMapper;
    private final CheckMessageService checkMessageService;
    private final CheckMessageMapper mapper = CheckMessageMapper.INSTANCE;

    public void processCheckMessages() {
        List<CheckMessage> batch = checkMessageService.findAllUnsent(BATCH_SIZE);
        if (batch.isEmpty()) {
            return;
        }

        CheckMessageBatch dto = new CheckMessageBatch(batch.stream()
                .map(mapper::toDto)
                .toList());

        try {
            messageSender.send(objectMapper.writeValueAsString(dto));
        } catch (MessageSendException e) {
            log.warn("Batch send failed, will retry later. {}", e.getMessage());
            return;
        } catch (JsonProcessingException e) {
            log.warn("JSON processing exception. Batch send failed, will retry later. {}", e.getMessage());
            return;
        }

        Set<Long> checkIds = batch.stream().map(CheckMessage::getBookingId).collect(Collectors.toSet());
        checkMessageService.markSentByIds(checkIds);
        log.info("Number of checks sent: {}", checkIds.size());
    }
}
