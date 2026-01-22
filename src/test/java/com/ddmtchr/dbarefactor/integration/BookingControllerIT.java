package com.ddmtchr.dbarefactor.integration;

import com.ddmtchr.dbarefactor.entity.Booking;
import com.ddmtchr.dbarefactor.entity.BookingStatus;
import com.ddmtchr.dbarefactor.entity.Estate;
import com.ddmtchr.dbarefactor.repository.BookingRepository;
import com.ddmtchr.dbarefactor.repository.EstateRepository;
import com.ddmtchr.dbarefactor.security.entity.User;
import com.ddmtchr.dbarefactor.security.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class BookingControllerIT extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EstateRepository estateRepository;
    @Autowired
    private BookingRepository bookingRepository;

    private Estate estate;
    private User host;
    private User guest;

    @BeforeEach
    void setup() {
        host = userRepository.save(new User("host", "pwd", "h@mail", 0L));
        guest = userRepository.save(new User("guest", "pwd", "g@mail", 1000L));

        estate = new Estate();
        estate.setName("Estate");
        estate.setPrice(100L);
        estate.setOwner(host);
        estate = estateRepository.save(estate);
    }

    @WithMockUser(authorities = {"GUEST"}, username = "guest")
    @Test
    void createBooking_Success() throws Exception {
        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "estateId": %d,
                                  "guestId": %d,
                                  "startDate": "2025-01-01",
                                  "endDate": "2025-01-03"
                                }
                                """.formatted(estate.getId(), guest.getId())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDING_HOST_REVIEW"));
    }

    @WithMockUser(authorities = {"HOST"}, username = "host")
    @Test
    void findByHost_Success() throws Exception {
        createBookingEntity();

        mockMvc.perform(get("/bookings/byHost"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @WithMockUser(authorities = {"HOST"}, username = "host")
    @Test
    void approveBooking_Success() throws Exception {
        Booking booking = createBookingEntity();

        mockMvc.perform(put("/bookings/{id}/approve", booking.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING_PAYMENT"));
    }

    @WithMockUser(authorities = {"GUEST"}, username = "guest")
    @Test
    void approveBooking_NoPermission_403() throws Exception {
        Booking booking = createBookingEntity();

        mockMvc.perform(put("/bookings/{id}/approve", booking.getId()))
                .andExpect(status().isForbidden());
    }

    private Booking createBookingEntity() {
        Booking b = new Booking();
        b.setEstate(estate);
        b.setGuest(guest);
        b.setStartDate(LocalDate.now());
        b.setEndDate(LocalDate.now().plusDays(2));
        b.setAmount(200L);
        b.setStatus(BookingStatus.PENDING_HOST_REVIEW);
        return bookingRepository.save(b);
    }
}

