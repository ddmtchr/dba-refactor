package com.ddmtchr.dbarefactor.service;

import com.ddmtchr.dbarefactor.entity.CheckMessage;
import com.ddmtchr.dbarefactor.exception.MessageSendException;
import com.ddmtchr.dbarefactor.service.producer.AmqpMessageSender;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.ConnectException;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CheckMessageSenderTest {

    @InjectMocks
    private CheckMessageSender sender;

    @Mock
    private AmqpMessageSender messageSender;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private CheckMessageService service;

    @Test
    void processCheckMessages_Success_MarksSent() throws Exception {
        CheckMessage msg = mock(CheckMessage.class);
        when(msg.getBookingId()).thenReturn(1L);

        when(service.findAllUnsent(100)).thenReturn(List.of(msg));
        when(objectMapper.writeValueAsString(any())).thenReturn("{}");

        sender.processCheckMessages();

        verify(messageSender).send("{}");
        verify(service).markSentByIds(Set.of(1L));
    }

    @Test
    void processCheckMessages_EmptyBatch_DoesNothing() {
        when(service.findAllUnsent(100)).thenReturn(List.of());

        sender.processCheckMessages();

        verify(messageSender, never()).send(any());
    }

    @Test
    void processCheckMessages_MessageSendException_DoesNotMark() throws Exception {
        when(service.findAllUnsent(100)).thenReturn(List.of(mock(CheckMessage.class)));
        when(objectMapper.writeValueAsString(any())).thenReturn("{}");
        doThrow(new MessageSendException(new ConnectException()))
                .when(messageSender).send(any());

        sender.processCheckMessages();

        verify(service, never()).markSentByIds(any());
    }

    @Test
    void processCheckMessages_SendFails_DoesNotMarkSent() throws Exception {
        CheckMessage msg = mock(CheckMessage.class);
        when(service.findAllUnsent(100)).thenReturn(List.of(msg));
        when(objectMapper.writeValueAsString(any()))
                .thenThrow(new JsonProcessingException("") {});

        sender.processCheckMessages();

        verify(service, never()).markSentByIds(any());
    }
}