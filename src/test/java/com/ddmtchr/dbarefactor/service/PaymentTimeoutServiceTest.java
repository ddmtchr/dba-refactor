package com.ddmtchr.dbarefactor.service;

import com.ddmtchr.dbarefactor.entity.BookingStatus;
import com.ddmtchr.dbarefactor.repository.BookingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PaymentTimeoutServiceTest {

    @InjectMocks
    private PaymentTimeoutService service;

    @Mock
    private BookingRepository repository;

    @Test
    void processPaymentTimeout_Always_UpdatesStatuses() {
        service.processPaymentTimeout();

        verify(repository).updateStatusByStatusAndPaymentRequestTimeBefore(
                eq(BookingStatus.PENDING_PAYMENT),
                any(LocalDateTime.class),
                eq(BookingStatus.REJECTED_BY_PAYMENT_TIMEOUT)
        );
    }
}