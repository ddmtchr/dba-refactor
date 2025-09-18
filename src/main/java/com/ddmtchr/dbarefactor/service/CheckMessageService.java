package com.ddmtchr.dbarefactor.service;

import com.ddmtchr.dbarefactor.dto.producer.CheckDto;
import com.ddmtchr.dbarefactor.mapper.CheckMessageMapper;
import com.ddmtchr.dbarefactor.repository.CheckMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CheckMessageService {

    private final CheckMessageMapper mapper = CheckMessageMapper.INSTANCE;
    private final CheckMessageRepository checkMessageRepository;

    @Transactional
    public CheckDto save(CheckDto dto) {
        return mapper.toDto(checkMessageRepository.save(mapper.toEntity(dto)));
    }

}
