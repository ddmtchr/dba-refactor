package com.ddmtchr.dbarefactor.service;

import com.ddmtchr.dbarefactor.security.entity.User;
import com.ddmtchr.dbarefactor.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final UserRepository userRepository;

    @Transactional
    public void payToHost(User host, Long amount) {
        host.setMoney(host.getMoney() + amount);
        this.userRepository.save(host);
    }
}
