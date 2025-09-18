package com.ddmtchr.dbarefactor.controller;

import com.ddmtchr.dbarefactor.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pay")
@RequiredArgsConstructor
public class PayController {
    private final BookingService bookingService;

    @PutMapping("/{id}")
    public ResponseEntity<Void> payForBooking(@PathVariable Long id) {
        this.bookingService.payForBooking(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
