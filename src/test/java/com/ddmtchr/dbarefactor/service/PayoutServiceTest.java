package com.ddmtchr.dbarefactor.service;

import com.ddmtchr.dbarefactor.entity.Booking;
import com.ddmtchr.dbarefactor.entity.BookingStatus;
import com.ddmtchr.dbarefactor.entity.Estate;
import com.ddmtchr.dbarefactor.repository.BookingRepository;
import com.ddmtchr.dbarefactor.security.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PayoutServiceTest {

    @InjectMocks
    private PayoutService service;

    @Mock
    private PaymentService paymentService;
    @Mock
    private BookingRepository bookingRepository;

    @Test
    void processPayouts_NoBookings_NoCalls() {
        when(bookingRepository.findByStatusAndPayoutScheduledAtBefore(any(), any()))
                .thenReturn(List.of());

        service.processPayouts();

        verify(paymentService, never()).payToHost(any(), any());
    }

    @Test
    void processPayouts_GroupedByHost_PaysOnce() {
        User host = mock(User.class);
        Estate estate = mock(Estate.class);
        Booking b1 = mock(Booking.class);
        Booking b2 = mock(Booking.class);

        when(b1.getEstate()).thenReturn(estate);
        when(b2.getEstate()).thenReturn(estate);
        when(estate.getOwner()).thenReturn(host);
        when(b1.getAmount()).thenReturn(100L);
        when(b2.getAmount()).thenReturn(50L);
        when(b1.getId()).thenReturn(1L);
        when(b2.getId()).thenReturn(2L);

        when(bookingRepository.findByStatusAndPayoutScheduledAtBefore(any(), any()))
                .thenReturn(List.of(b1, b2));

        service.processPayouts();

        verify(paymentService).payToHost(host, 150L);
        verify(bookingRepository).updateStatusByIdsAndPayoutScheduledAtBefore(
                eq(Set.of(1L, 2L)),
                any(),
                eq(BookingStatus.IN_PROGRESS)
        );
    }
}