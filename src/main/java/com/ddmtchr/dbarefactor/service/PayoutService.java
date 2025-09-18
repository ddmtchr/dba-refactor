package com.ddmtchr.dbarefactor.service;

import com.ddmtchr.dbarefactor.entity.Booking;
import com.ddmtchr.dbarefactor.entity.BookingStatus;
import com.ddmtchr.dbarefactor.repository.BookingRepository;
import com.ddmtchr.dbarefactor.security.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PayoutService {

    private final PaymentService paymentService;
    private final BookingRepository bookingRepository;

    @Transactional
    public void processPayouts() {
        LocalDateTime now = LocalDateTime.now();

        List<Booking> bookingsForPayout = this.bookingRepository.findByStatusAndPayoutScheduledAtBefore(BookingStatus.PENDING_PAYMENT_TO_HOST, now);
        Set<Long> paidBookingIds = new HashSet<>();
        Map<User, Long> paymentsByHost = new HashMap<>();

        bookingsForPayout.forEach(b -> {
            User host = b.getEstate().getOwner();
            Long amount = b.getAmount();

            paymentsByHost.put(host, paymentsByHost.getOrDefault(host, 0L) + amount);
            paidBookingIds.add(b.getId());
        });

        paymentsByHost.forEach(this.paymentService::payToHost);

        this.bookingRepository.updateStatusByIdsAndPayoutScheduledAtBefore(paidBookingIds, now, BookingStatus.IN_PROGRESS);
    }
}
