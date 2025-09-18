package com.ddmtchr.dbarefactor.controller;

import com.ddmtchr.dbarefactor.dto.request.EstateRequestDto;
import com.ddmtchr.dbarefactor.dto.response.EstateResponseDto;
import com.ddmtchr.dbarefactor.service.EstateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/estate")
@RequiredArgsConstructor
public class EstateController {
    private final EstateService estateService;

    @PostMapping
    public ResponseEntity<EstateResponseDto> addEstate(@RequestBody @Valid EstateRequestDto dto) {
        return new ResponseEntity<>(this.estateService.addEstate(dto), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<EstateResponseDto>> findAllEstate() {
        return new ResponseEntity<>(this.estateService.findAll(), HttpStatus.OK);
    }
}
