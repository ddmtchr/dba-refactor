package com.ddmtchr.dbarefactor.service;

import com.ddmtchr.dbarefactor.entity.BookingStatus;
import com.ddmtchr.dbarefactor.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingFinisher {
    private final BookingRepository bookingRepository;

    @Transactional
    public void processFinish() {
        LocalDate now = LocalDate.now();
        int updated = this.bookingRepository.updateStatusByStatusAndEndDateBefore(BookingStatus.IN_PROGRESS, now, BookingStatus.FINISHED);
        log.info("Number of bookings marked as finished: {}", updated);
    }

}
