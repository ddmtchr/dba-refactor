package com.ddmtchr.dbarefactor.service.producer;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

@Slf4j
@Service
public class StompMessageSender {

    @Value("${rabbitmq.url}")
    private String url;

    @Value("${rabbitmq.username}")
    private String username;

    @Value("${rabbitmq.password}")
    private String password;

    @Value("${rabbitmq.destination}")
    private String destination;

    private final WebSocketStompClient stompClient;
    private volatile StompSession stompSession;

    public StompMessageSender() {
        this.stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        this.stompClient.setMessageConverter(new MappingJackson2MessageConverter());
    }

    @PostConstruct
    public void connect() {
        StompHeaders connectHeaders = new StompHeaders();
        connectHeaders.setLogin(username);
        connectHeaders.setPasscode(password);

        StompSessionHandler sessionHandler = new CustomStompSessionHandler(stompClient, url, connectHeaders,
                session -> this.stompSession = session);

        stompClient.connectAsync(url, new WebSocketHttpHeaders(), connectHeaders, sessionHandler);
    }

    public void sendMessage(Object dto) {
        if (stompSession != null && stompSession.isConnected()) {
            stompSession.send(destination, dto);
            log.info("Message sent");
        } else {
            log.error("STOMP Session is unavailable, message wasn't sent");
        }
    }
}

