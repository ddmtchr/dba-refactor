package com.ddmtchr.dbarefactor.service;

import com.ddmtchr.dbarefactor.dto.request.BookingChangesDto;
import com.ddmtchr.dbarefactor.dto.request.BookingRequestDto;
import com.ddmtchr.dbarefactor.dto.response.BookingResponseDto;
import com.ddmtchr.dbarefactor.entity.Booking;
import com.ddmtchr.dbarefactor.entity.BookingStatus;
import com.ddmtchr.dbarefactor.entity.Estate;
import com.ddmtchr.dbarefactor.exception.InconsistentRequestException;
import com.ddmtchr.dbarefactor.exception.InsufficientFundsException;
import com.ddmtchr.dbarefactor.exception.NoPermissionException;
import com.ddmtchr.dbarefactor.exception.NotFoundException;
import com.ddmtchr.dbarefactor.repository.BookingRepository;
import com.ddmtchr.dbarefactor.repository.EstateRepository;
import com.ddmtchr.dbarefactor.security.entity.User;
import com.ddmtchr.dbarefactor.security.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @InjectMocks
    private BookingService service;

    @Mock
    private BookingRepository repository;
    @Mock
    private EstateRepository estateRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CheckMessageService checkMessageService;

    private User host;
    private User guest;
    private Estate estate;
    private Booking booking;

    @BeforeEach
    void init() {
        host = new User();
        host.setUsername("host");

        guest = new User();
        guest.setUsername("guest");
        guest.setMoney(1_000L);
        guest.setEmail("g@mail");

        estate = new Estate();
        estate.setOwner(host);
        estate.setPrice(100L);
        estate.setName("estate");

        booking = new Booking();
        booking.setId(1L);
        booking.setEstate(estate);
        booking.setGuest(guest);
        booking.setAmount(200L);
        booking.setStartDate(LocalDate.now());
        booking.setEndDate(LocalDate.now().plusDays(2));
    }

    @Test
    void addBooking_EstateNotFound_Throws() {
        BookingRequestDto dto = mock(BookingRequestDto.class);
        when(dto.getEstateId()).thenReturn(1L);
        when(estateRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.addBooking(dto));
    }

    @Test
    void addBooking_GuestNotFound_Throws() {
        BookingRequestDto dto = mock(BookingRequestDto.class);
        when(dto.getEstateId()).thenReturn(1L);
        when(dto.getGuestId()).thenReturn(2L);
        when(estateRepository.findById(1L)).thenReturn(Optional.of(estate));
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.addBooking(dto));
    }

    @Test
    void addBooking_Success() {
        BookingRequestDto dto = mock(BookingRequestDto.class);
        when(dto.getEstateId()).thenReturn(1L);
        when(dto.getGuestId()).thenReturn(2L);
        when(dto.getStartDate()).thenReturn(LocalDate.now());
        when(dto.getEndDate()).thenReturn(LocalDate.now().plusDays(2));

        when(estateRepository.findById(1L)).thenReturn(Optional.of(estate));
        when(userRepository.findById(2L)).thenReturn(Optional.of(guest));
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        BookingResponseDto result = service.addBooking(dto);

        assertNotNull(result);
    }

    @Test
    void findAllByHost_ReturnsList() {
        when(repository.findByEstateOwnerUsername("host")).thenReturn(List.of(booking));

        assertEquals(1, service.findAllByHost("host").size());
    }

    @Test
    void findAllByGuest_ReturnsList() {
        when(repository.findByGuestUsername("guest")).thenReturn(List.of(booking));

        assertEquals(1, service.findAllByGuest("guest").size());
    }

    @Test
    void findById_NotFound_Throws() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.findById(1L));
    }

    @Test
    void findById_Found_ReturnsDto() {
        when(repository.findById(1L)).thenReturn(Optional.of(booking));

        BookingResponseDto dto = service.findById(1L);

        assertNotNull(dto);
    }

    @Test
    void approveById_NoPermission_Throws() {
        booking.setStatus(BookingStatus.PENDING_HOST_REVIEW);
        when(repository.findById(1L)).thenReturn(Optional.of(booking));

        mockAuthenticatedUser("guest");

        assertThrows(NoPermissionException.class, () -> service.approveById(1L));
    }

    @Test
    void approveById_WrongStatus_Throws() {
        booking.setStatus(BookingStatus.REJECTED_BY_HOST);
        when(repository.findById(1L)).thenReturn(Optional.of(booking));

        mockAuthenticatedUser("host");

        assertThrows(InconsistentRequestException.class, () -> service.approveById(1L));
    }

    @Test
    void approveById_Success() {
        booking.setStatus(BookingStatus.PENDING_HOST_REVIEW);
        when(repository.findById(1L)).thenReturn(Optional.of(booking));
        when(repository.save(any())).thenReturn(booking);

        mockAuthenticatedUser("host");

        BookingResponseDto dto = service.approveById(1L);

        assertEquals(BookingStatus.PENDING_PAYMENT, booking.getStatus());
        assertNotNull(booking.getPaymentRequestTime());
        assertNotNull(dto);
    }

    @Test
    void approveById_NotFound_Throws() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> service.approveById(1L));
    }

    @Test
    void rejectById_Success() {
        booking.setStatus(BookingStatus.PENDING_HOST_REVIEW);
        when(repository.findById(1L)).thenReturn(Optional.of(booking));
        when(repository.save(any())).thenReturn(booking);

        mockAuthenticatedUser("host");

        BookingResponseDto dto = service.rejectById(1L);

        assertEquals(BookingStatus.REJECTED_BY_HOST, booking.getStatus());
        assertNotNull(dto);
    }

    @Test
    void rejectById_WrongStatus_Throws() {
        booking.setStatus(BookingStatus.PENDING_PAYMENT);
        when(repository.findById(1L)).thenReturn(Optional.of(booking));

        mockAuthenticatedUser("host");

        assertThrows(InconsistentRequestException.class,
                () -> service.rejectById(1L));
    }

    @Test
    void rejectById_NotFound_Throws() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> service.rejectById(1L));
    }

    @Test
    void changeById_WrongStatus_Throws() {
        booking.setStatus(BookingStatus.REJECTED_BY_HOST);
        when(repository.findById(1L)).thenReturn(Optional.of(booking));

        mockAuthenticatedUser("host");

        assertThrows(InconsistentRequestException.class,
                () -> service.changeById(1L, mock(BookingChangesDto.class)));
    }

    @Test
    void changeById_NotFound_Throws() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> service.changeById(1L, mock(BookingChangesDto.class)));
    }

    @Test
    void changeById_PendingHostReview_Success() {
        booking.setStatus(BookingStatus.PENDING_HOST_REVIEW);
        when(repository.findById(1L)).thenReturn(Optional.of(booking));
        when(repository.save(any())).thenReturn(booking);

        mockAuthenticatedUser("host");

        BookingResponseDto dto = service.changeById(1L, mock(BookingChangesDto.class));

        assertEquals(BookingStatus.PENDING_CHANGES_REVIEW, booking.getStatus());
        assertNotNull(dto);
    }

    @Test
    void changeById_PendingChangesReview_Success() {
        booking.setStatus(BookingStatus.PENDING_CHANGES_REVIEW);
        when(repository.findById(1L)).thenReturn(Optional.of(booking));
        when(repository.save(any())).thenReturn(booking);

        mockAuthenticatedUser("host");

        BookingResponseDto dto = service.changeById(1L, mock(BookingChangesDto.class));

        assertEquals(BookingStatus.PENDING_CHANGES_REVIEW, booking.getStatus());
        assertNotNull(dto);
    }

    @Test
    void approveChangesById_NoPermission_Throws() {
        booking.setStatus(BookingStatus.PENDING_CHANGES_REVIEW);
        when(repository.findById(1L)).thenReturn(Optional.of(booking));

        mockAuthenticatedUser("host");

        assertThrows(NoPermissionException.class,
                () -> service.approveChangesById(1L));
    }

    @Test
    void approveChangesById_WrongStatus_Throws() {
        booking.setStatus(BookingStatus.PENDING_PAYMENT);
        when(repository.findById(1L)).thenReturn(Optional.of(booking));

        mockAuthenticatedUser("guest");

        assertThrows(InconsistentRequestException.class,
                () -> service.approveChangesById(1L));
    }

    @Test
    void approveChangesById_NotFound_Throws() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> service.approveChangesById(1L));
    }

    @Test
    void approveChangesById_Success() {
        booking.setStatus(BookingStatus.PENDING_CHANGES_REVIEW);
        when(repository.findById(1L)).thenReturn(Optional.of(booking));
        when(repository.save(any())).thenReturn(booking);

        mockAuthenticatedUser("guest");

        BookingResponseDto dto = service.approveChangesById(1L);

        assertEquals(BookingStatus.PENDING_PAYMENT, booking.getStatus());
        assertNotNull(booking.getPaymentRequestTime());
        assertNotNull(dto);
    }

    @Test
    void rejectChangesById_Success() {
        booking.setStatus(BookingStatus.PENDING_CHANGES_REVIEW);
        when(repository.findById(1L)).thenReturn(Optional.of(booking));
        when(repository.save(any())).thenReturn(booking);

        mockAuthenticatedUser("guest");

        BookingResponseDto dto = service.rejectChangesById(1L);

        assertEquals(BookingStatus.REJECTED_BY_GUEST, booking.getStatus());
        assertNotNull(dto);
    }

    @Test
    void rejectChangesById_WrongStatus_Throws() {
        booking.setStatus(BookingStatus.PENDING_PAYMENT);
        when(repository.findById(1L)).thenReturn(Optional.of(booking));

        mockAuthenticatedUser("guest");

        assertThrows(InconsistentRequestException.class,
                () -> service.rejectChangesById(1L));
    }

    @Test
    void rejectChangesById_NotFound_Throws() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> service.rejectChangesById(1L));
    }

    @Test
    void payForBooking_WrongStatus_Throws() {
        booking.setStatus(BookingStatus.PENDING_HOST_REVIEW);
        when(repository.findById(1L)).thenReturn(Optional.of(booking));
        when(userRepository.findByUsername("guest")).thenReturn(Optional.of(guest));

        mockAuthenticatedUser("guest");

        assertThrows(InconsistentRequestException.class,
                () -> service.payForBooking(1L));
    }

    @Test
    void payForBooking_InsufficientFunds_Throws() {
        booking.setStatus(BookingStatus.PENDING_PAYMENT);
        guest.setMoney(100L);
        when(repository.findById(1L)).thenReturn(Optional.of(booking));
        when(userRepository.findByUsername("guest")).thenReturn(Optional.of(guest));

        mockAuthenticatedUser("guest");

        assertThrows(InsufficientFundsException.class,
                () -> service.payForBooking(1L));
    }

    @Test
    void payForBooking_NotFound_Throws() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> service.payForBooking(1L));
    }

    @Test
    void payForBooking_GuestNotFound_Throws() {
        booking.setStatus(BookingStatus.PENDING_PAYMENT);
        when(repository.findById(1L)).thenReturn(Optional.of(booking));
        when(userRepository.findByUsername("guest"))
                .thenReturn(Optional.empty());

        mockAuthenticatedUser("guest");

        assertThrows(NotFoundException.class,
                () -> service.payForBooking(1L));
    }

    @Test
    void payForBooking_Success() {
        booking.setStatus(BookingStatus.PENDING_PAYMENT);
        when(repository.findById(1L)).thenReturn(Optional.of(booking));
        when(userRepository.findByUsername("guest")).thenReturn(Optional.of(guest));

        mockAuthenticatedUser("guest");

        service.payForBooking(1L);

        assertEquals(BookingStatus.PENDING_CHECK_IN, booking.getStatus());
        verify(checkMessageService).save(any());
    }

    @Test
    void checkInById_WrongStatus_Throws() {
        booking.setStatus(BookingStatus.PENDING_PAYMENT);
        when(repository.findById(1L)).thenReturn(Optional.of(booking));

        mockAuthenticatedUser("guest");

        assertThrows(InconsistentRequestException.class,
                () -> service.checkInById(1L));
    }

    @Test
    void checkInById_NotFound_Throws() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> service.checkInById(1L));
    }

    @Test
    void checkInById_Success() {
        booking.setStatus(BookingStatus.PENDING_CHECK_IN);
        when(repository.findById(1L)).thenReturn(Optional.of(booking));
        when(repository.save(any())).thenReturn(booking);

        mockAuthenticatedUser("guest");

        BookingResponseDto dto = service.checkInById(1L);

        assertEquals(BookingStatus.PENDING_PAYMENT_TO_HOST, booking.getStatus());
        assertNotNull(booking.getPayoutScheduledAt());
        assertNotNull(dto);
    }
    
    private void mockAuthenticatedUser(String username) {
        UserDetails user = new org.springframework.security.core.userdetails.User(
                username,
                "",
                List.of()
        );

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(context);
    }
}
