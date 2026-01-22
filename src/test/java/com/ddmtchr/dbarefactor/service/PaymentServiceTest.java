package com.ddmtchr.dbarefactor.service;

import com.ddmtchr.dbarefactor.security.entity.User;
import com.ddmtchr.dbarefactor.security.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @InjectMocks
    private PaymentService service;

    @Mock
    private UserRepository userRepository;

    @Test
    void payToHost_AddsMoneyAndSaves() {
        User host = mock(User.class);
        when(host.getMoney()).thenReturn(100L);

        service.payToHost(host, 50L);

        verify(host).setMoney(150L);
        verify(userRepository).save(host);
    }
}