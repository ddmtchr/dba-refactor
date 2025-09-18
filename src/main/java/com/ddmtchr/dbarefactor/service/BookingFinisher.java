package com.ddmtchr.dbarefactor.service;

import com.ddmtchr.dbarefactor.entity.BookingStatus;
import com.ddmtchr.dbarefactor.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class BookingFinisher {
    private final BookingRepository bookingRepository;

    @Transactional
    public void processFinish() {
        LocalDate now = LocalDate.now();

        this.bookingRepository.updateStatusByStatusAndEndDateBefore(BookingStatus.IN_PROGRESS, now, BookingStatus.FINISHED);
    }

}
