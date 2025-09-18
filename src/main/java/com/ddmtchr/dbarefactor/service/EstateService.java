package com.ddmtchr.dbarefactor.service;

import com.ddmtchr.dbarefactor.dto.request.EstateRequestDto;
import com.ddmtchr.dbarefactor.dto.response.EstateResponseDto;
import com.ddmtchr.dbarefactor.entity.Estate;
import com.ddmtchr.dbarefactor.exception.NotFoundException;
import com.ddmtchr.dbarefactor.mapper.EstateMapper;
import com.ddmtchr.dbarefactor.repository.EstateRepository;
import com.ddmtchr.dbarefactor.security.entity.User;
import com.ddmtchr.dbarefactor.security.repository.UserRepository;
import com.ddmtchr.dbarefactor.security.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EstateService {
    private final EstateRepository estateRepository;
    private final UserRepository userRepository;
    private final EstateMapper mapper = EstateMapper.INSTANCE;

    public EstateResponseDto addEstate(EstateRequestDto dto) {
        String username = SecurityUtil.getCurrentUser().getUsername();
        User owner = this.userRepository.findByUsername(username).orElseThrow(() -> new NotFoundException(String.format("User with username=%s was not found", username)));
        Estate entity = this.mapper.toEntity(dto);
        entity.setOwner(owner);
        return this.mapper.toResponseDto(this.estateRepository.save(entity));
    }

    public List<EstateResponseDto> findAll() {
        return this.estateRepository.findAll().stream().map(this.mapper::toResponseDto).toList();
    }

}
