package com.ddmtchr.dbarefactor.service;

import com.ddmtchr.dbarefactor.entity.BookingStatus;
import com.ddmtchr.dbarefactor.repository.BookingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BookingFinisherTest {

    @InjectMocks
    private BookingFinisher service;

    @Mock
    private BookingRepository repository;

    @Test
    void processFinish_Always_UpdatesStatuses() {
        service.processFinish();

        verify(repository).updateStatusByStatusAndEndDateBefore(
                eq(BookingStatus.IN_PROGRESS),
                any(LocalDate.class),
                eq(BookingStatus.FINISHED)
        );
    }
}
