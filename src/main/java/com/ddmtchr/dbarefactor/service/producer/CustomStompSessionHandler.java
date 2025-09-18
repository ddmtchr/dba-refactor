package com.ddmtchr.dbarefactor.service.producer;

import jakarta.websocket.DeploymentException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.messaging.simp.stomp.ConnectionLostException;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

@Slf4j
@RequiredArgsConstructor
public class CustomStompSessionHandler extends StompSessionHandlerAdapter {

    private final WebSocketStompClient stompClient;

    private final String url;

    private final StompHeaders connectHeaders;

    private final Consumer<StompSession> sessionConsumer;

    AtomicBoolean reconnecting = new AtomicBoolean(false);

    @Override
    public void afterConnected(@NonNull StompSession session, @NonNull StompHeaders connectedHeaders) {
        log.info("STOMP Connected to {}", url);
        sessionConsumer.accept(session);
    }

    @Override
    public void handleTransportError(@NonNull StompSession session, @NonNull Throwable exception) {
        if (exception instanceof ConnectionLostException
                && !session.isConnected()
                && reconnecting.compareAndSet(false, true)) {
            log.info("Connection to {} lost, trying to reconnect", url);
            reconnect();
        } else if (exception.getCause() instanceof DeploymentException) {
            log.info("Couldn't connect to {}, trying to reconnect", url);
            Executors.newSingleThreadScheduledExecutor().schedule(this::reconnect, 5, TimeUnit.SECONDS);
        } else {
            log.error("Transport error: {}", exception.getMessage());
        }
    }

    private void reconnect() {
        Executors.newSingleThreadExecutor().submit(() -> {
            while (true) {
                try {
                    TimeUnit.SECONDS.sleep(2);
                    stompClient.connectAsync(url, new WebSocketHttpHeaders(), connectHeaders, this);
                    break;
                } catch (Exception e) {
                    log.warn("Reconnect error: {}", e.getMessage());
                }
            }
            reconnecting.set(false);
        });
    }
}

