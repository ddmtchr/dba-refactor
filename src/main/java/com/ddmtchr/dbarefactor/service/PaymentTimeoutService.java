package com.ddmtchr.dbarefactor.service;

import com.ddmtchr.dbarefactor.entity.BookingStatus;
import com.ddmtchr.dbarefactor.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentTimeoutService {
    private static final Duration PAYMENT_TIMEOUT = Duration.ofHours(1);

    private final BookingRepository bookingRepository;

    @Transactional
    public void processPaymentTimeout() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime deadline = now.minus(PAYMENT_TIMEOUT);

        this.bookingRepository.updateStatusByStatusAndPaymentRequestTimeBefore(BookingStatus.PENDING_PAYMENT, deadline, BookingStatus.REJECTED_BY_PAYMENT_TIMEOUT);
    }
}
