package com.ddmtchr.dbarefactor.service;

import com.ddmtchr.dbarefactor.dto.producer.CheckDto;
import com.ddmtchr.dbarefactor.entity.CheckMessage;
import com.ddmtchr.dbarefactor.mapper.CheckMessageMapper;
import com.ddmtchr.dbarefactor.repository.CheckMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CheckMessageService {

    private final CheckMessageMapper mapper = CheckMessageMapper.INSTANCE;
    private final CheckMessageRepository checkMessageRepository;

    @Transactional
    public CheckDto save(CheckDto dto) {
        return mapper.toDto(checkMessageRepository.save(mapper.toEntity(dto)));
    }

    @Transactional
    public List<CheckMessage> findAllUnsent(int limit) {
        return checkMessageRepository.findAllBySentFalseOrderByBookingId(Limit.of(limit));
    }

    @Transactional
    public void markSentByIds(Collection<Long> ids) {
        checkMessageRepository.setSentByBookingIdIn(ids);
    }
}
