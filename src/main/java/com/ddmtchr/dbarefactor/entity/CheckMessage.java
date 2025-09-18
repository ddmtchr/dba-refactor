package com.ddmtchr.dbarefactor.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "check_message")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CheckMessage {

    @Id
    private Long bookingId;

    @Column(nullable = false)
    private String guest;

    @Column(nullable = false)
    private String estate;

    @Column(nullable = false)
    private Long amount;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private Boolean sent = false;
}
