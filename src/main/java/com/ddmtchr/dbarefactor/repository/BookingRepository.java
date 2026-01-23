package com.ddmtchr.dbarefactor.repository;

import com.ddmtchr.dbarefactor.entity.Booking;
import com.ddmtchr.dbarefactor.entity.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByEstateOwnerUsername(String username);

    List<Booking> findByGuestUsername(String username);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Booking b set b.status = :statusToSet " +
            "where b.status = :statusToFind " +
            "and b.paymentRequestTime < :time")
    Integer updateStatusByStatusAndPaymentRequestTimeBefore(@Param("statusToFind") BookingStatus statusToFind, @Param("time") LocalDateTime time, @Param("statusToSet") BookingStatus statusToSet);


    List<Booking> findByStatusAndPayoutScheduledAtBefore(@Param("status") BookingStatus status, @Param("time") LocalDateTime time);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Booking b set b.status = :status " +
            "where b.id in :ids " +
            "and b.payoutScheduledAt < :time")
    Integer updateStatusByIdsAndPayoutScheduledAtBefore(@Param("ids") Collection<Long> ids, @Param("time") LocalDateTime time, @Param("status") BookingStatus status);


    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Booking b set b.status = :statusToSet " +
            "where b.status = :statusToFind " +
            "and b.endDate < :date")
    Integer updateStatusByStatusAndEndDateBefore(@Param("statusToFind") BookingStatus statusToFind, @Param("date") LocalDate date, @Param("statusToSet") BookingStatus statusToSet);
}
