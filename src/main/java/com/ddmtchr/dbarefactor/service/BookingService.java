package com.ddmtchr.dbarefactor.service;

import com.ddmtchr.dbarefactor.dto.producer.CheckDto;
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
import com.ddmtchr.dbarefactor.mapper.BookingMapper;
import com.ddmtchr.dbarefactor.repository.BookingRepository;
import com.ddmtchr.dbarefactor.repository.EstateRepository;
import com.ddmtchr.dbarefactor.security.entity.User;
import com.ddmtchr.dbarefactor.security.repository.UserRepository;
import com.ddmtchr.dbarefactor.security.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingMapper mapper = BookingMapper.INSTANCE;
    private final BookingRepository repository;
    private final EstateRepository estateRepository;
    private final UserRepository userRepository;
    private final CheckMessageService checkMessageService;

    @Transactional
    public BookingResponseDto addBooking(BookingRequestDto dto) {
        Booking entity = this.mapper.toEntity(dto);
        Estate estate = this.estateRepository.findById(dto.getEstateId()).orElseThrow(() -> new NotFoundException(String.format("Estate with id=%s was not found", dto.getEstateId())));
        User guest = this.userRepository.findById(dto.getGuestId()).orElseThrow(() -> new NotFoundException(String.format("User with id=%s was not found", dto.getGuestId())));

        Long amountToPay = estate.getPrice() * ChronoUnit.DAYS.between(dto.getStartDate(), dto.getEndDate());
        entity.setAmount(amountToPay);
        entity.setEstate(estate);
        entity.setGuest(guest);
        entity.setStatus(BookingStatus.PENDING_HOST_REVIEW);
        Booking saved = this.repository.save(entity);
        log.info("Created new booking. Guest: {}, estate: {}", saved.getGuest().getUsername(), saved.getEstate().getName());
        return this.mapper.toResponseDto(saved);
    }

    public List<BookingResponseDto> findAllByHost(String username) {
        return this.repository.findByEstateOwnerUsername(username).stream().map(this.mapper::toResponseDto).toList();
    }

    public List<BookingResponseDto> findAllByGuest(String username) {
        return this.repository.findByGuestUsername(username).stream().map(this.mapper::toResponseDto).toList();
    }

    public BookingResponseDto findById(Long id) {
        return this.repository.findById(id).map(this.mapper::toResponseDto).orElseThrow(() -> new NotFoundException(String.format("Booking with id=%s was not found", id)));
    }

    @Transactional
    public BookingResponseDto approveById(Long id) {
        Booking booking = this.repository.findById(id).orElseThrow(() -> new NotFoundException(String.format("Booking with id=%s was not found", id)));

        checkHostPermission(booking);
        if (!booking.getStatus().equals(BookingStatus.PENDING_HOST_REVIEW)) {
            throw new InconsistentRequestException(String.format("Booking with id=%s cannot be approved", id));
        }

        booking.setStatus(BookingStatus.PENDING_PAYMENT);
        booking.setPaymentRequestTime(LocalDateTime.now());
        Booking saved = this.repository.save(booking);
        log.info("Booking with id={} has been approved by host", saved.getId());
        return this.mapper.toResponseDto(saved);
    }

    @Transactional
    public BookingResponseDto rejectById(Long id) {
        Booking booking = this.repository.findById(id).orElseThrow(() -> new NotFoundException(String.format("Booking with id=%s was not found", id)));

        checkHostPermission(booking);
        if (!booking.getStatus().equals(BookingStatus.PENDING_HOST_REVIEW)) {
            throw new InconsistentRequestException(String.format("Booking with id=%s cannot be rejected", id));
        }

        booking.setStatus(BookingStatus.REJECTED_BY_HOST);
        Booking saved = this.repository.save(booking);
        log.info("Booking with id={} has been rejected by host", saved.getId());
        return this.mapper.toResponseDto(saved);
    }

    @Transactional
    public BookingResponseDto changeById(Long id, BookingChangesDto dto) {
        Booking booking = this.repository.findById(id).orElseThrow(() -> new NotFoundException(String.format("Booking with id=%s was not found", id)));

        checkHostPermission(booking);
        if (!(booking.getStatus().equals(BookingStatus.PENDING_HOST_REVIEW) || booking.getStatus().equals(BookingStatus.PENDING_CHANGES_REVIEW))) {
            throw new InconsistentRequestException(String.format("Booking with id=%s cannot be changed", id));
        }

        this.mapper.updateBooking(dto, booking);
        booking.setStatus(BookingStatus.PENDING_CHANGES_REVIEW);
        Booking saved = this.repository.save(booking);
        log.info("Changes to booking with id={} have been suggested by guest", saved.getId());
        return this.mapper.toResponseDto(saved);
    }

    @Transactional
    public BookingResponseDto approveChangesById(Long id) {
        Booking booking = this.repository.findById(id).orElseThrow(() -> new NotFoundException(String.format("Booking with id=%s was not found", id)));

        checkGuestPermission(booking);
        if (!booking.getStatus().equals(BookingStatus.PENDING_CHANGES_REVIEW)) {
            throw new InconsistentRequestException(String.format("Booking with id=%s cannot be approved by guest", id));
        }

        booking.setStatus(BookingStatus.PENDING_PAYMENT);
        booking.setPaymentRequestTime(LocalDateTime.now());
        Booking saved = this.repository.save(booking);
        log.info("Changes to booking with id={} have been approved by host", saved.getId());
        return this.mapper.toResponseDto(saved);
    }

    @Transactional
    public BookingResponseDto rejectChangesById(Long id) {
        Booking booking = this.repository.findById(id).orElseThrow(() -> new NotFoundException(String.format("Booking with id=%s was not found", id)));

        checkGuestPermission(booking);
        if (!booking.getStatus().equals(BookingStatus.PENDING_CHANGES_REVIEW)) {
            throw new InconsistentRequestException(String.format("Booking with id=%s cannot be rejected by guest", id));
        }

        booking.setStatus(BookingStatus.REJECTED_BY_GUEST);
        Booking saved = this.repository.save(booking);
        log.info("Changes to booking with id={} have been rejected by host", saved.getId());
        return this.mapper.toResponseDto(saved);
    }

    @Transactional
    public void payForBooking(Long id) {
        Booking booking = this.repository.findById(id).orElseThrow(() -> new NotFoundException(String.format("Booking with id=%s was not found", id)));
        String username = SecurityUtil.getCurrentUser().getUsername();
        User guest = this.userRepository.findByUsername(username).orElseThrow(() -> new NotFoundException(String.format("User with username=%s was not found", username)));

        checkGuestPermission(booking);
        if (!booking.getStatus().equals(BookingStatus.PENDING_PAYMENT)) {
            throw new InconsistentRequestException(String.format("Booking with id=%s cannot be paid by guest", id));
        }

        Long amountToPay = booking.getAmount();
        if (guest.getMoney() < amountToPay) {
            throw new InsufficientFundsException("Insufficient funds");
        }
        guest.setMoney(guest.getMoney() - amountToPay);
        booking.setStatus(BookingStatus.PENDING_CHECK_IN);
        this.repository.save(booking);
        this.userRepository.save(guest);

        log.info("Guest {} was charged for the booking with id={}. Amount: {}", guest.getUsername(), booking.getId(), amountToPay);

        this.checkMessageService.save(new CheckDto(
                booking.getId(),
                guest.getUsername(),
                booking.getEstate().getName(),
                amountToPay,
                guest.getEmail(),
                booking.getStartDate(),
                booking.getEndDate()));
    }

    @Transactional
    public BookingResponseDto checkInById(Long id) {
        Booking booking = this.repository.findById(id).orElseThrow(() -> new NotFoundException(String.format("Booking with id=%s was not found", id)));

        checkGuestPermission(booking);
        if (!booking.getStatus().equals(BookingStatus.PENDING_CHECK_IN)) {
            throw new InconsistentRequestException(String.format("Booking with id=%s cannot be checked in", id));
        }

        booking.setStatus(BookingStatus.PENDING_PAYMENT_TO_HOST);
        booking.setPayoutScheduledAt(LocalDateTime.now().plusMinutes(1));
        Booking saved = this.repository.save(booking);
        log.info("Guest for booking with id={} has checked in successfully", saved.getId());
        return this.mapper.toResponseDto(saved);
    }


    private void checkHostPermission(Booking booking) {
        if (!booking.getEstate().getOwner().getUsername().equals(SecurityUtil.getCurrentUser().getUsername())) {
            throw new NoPermissionException(String.format("No permission to edit booking with id=%s", booking.getId()));
        }
    }

    private void checkGuestPermission(Booking booking) {
        if (!booking.getGuest().getUsername().equals(SecurityUtil.getCurrentUser().getUsername())) {
            throw new NoPermissionException(String.format("No permission to edit booking with id=%s", booking.getId()));
        }
    }
}
