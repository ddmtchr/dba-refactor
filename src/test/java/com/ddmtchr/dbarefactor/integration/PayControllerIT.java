package com.ddmtchr.dbarefactor.integration;

import com.ddmtchr.dbarefactor.entity.Booking;
import com.ddmtchr.dbarefactor.entity.BookingStatus;
import com.ddmtchr.dbarefactor.entity.Estate;
import com.ddmtchr.dbarefactor.repository.BookingRepository;
import com.ddmtchr.dbarefactor.repository.EstateRepository;
import com.ddmtchr.dbarefactor.security.entity.User;
import com.ddmtchr.dbarefactor.security.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PayControllerIT extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EstateRepository estateRepository;

    @WithMockUser(authorities = {"GUEST"}, username = "guest")
    @Test
    void payForBooking_Success() throws Exception {
        User guest = userRepository.save(
                new User("guest", "pwd", "g@mail", 1000L)
        );

        User owner = userRepository.save(
                new User("owner", "pwd", "o@mail", 0L)
        );

        Estate estate = estateRepository.save(
                new Estate(
                        null,
                        "Nice flat",
                        "desc",
                        200L,
                        owner
                )
        );

        Booking booking = new Booking();
        booking.setGuest(guest);
        booking.setEstate(estate);
        booking.setStatus(BookingStatus.PENDING_PAYMENT);
        booking.setStartDate(LocalDate.now().plusDays(1));
        booking.setEndDate(LocalDate.now().plusDays(3));
        booking.setAmount(200L);

        booking = bookingRepository.save(booking);

        mockMvc.perform(put("/pay/{id}", booking.getId()))
                .andExpect(status().isNoContent());
    }

    @WithMockUser(authorities = {"GUEST"}, username = "guest")
    @Test
    void payForBooking_InsufficientFunds_400() throws Exception {
        User guest = userRepository.save(
                new User("guest", "pwd", "g@mail", 100L)
        );

        User owner = userRepository.save(
                new User("owner", "pwd", "o@mail", 0L)
        );

        Estate estate = estateRepository.save(
                new Estate(
                        null,
                        "Nice flat",
                        null,
                        200L,
                        owner
                )
        );

        Booking booking = new Booking();
        booking.setGuest(guest);
        booking.setEstate(estate);
        booking.setStatus(BookingStatus.PENDING_PAYMENT);
        booking.setStartDate(LocalDate.now().plusDays(1));
        booking.setEndDate(LocalDate.now().plusDays(3));
        booking.setAmount(200L);

        booking = bookingRepository.save(booking);

        mockMvc.perform(put("/pay/{id}", booking.getId()))
                .andExpect(status().isBadRequest());
    }
}
