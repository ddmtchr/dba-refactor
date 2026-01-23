package com.ddmtchr.dbarefactor.controller;

import com.ddmtchr.dbarefactor.dto.request.EstateRequestDto;
import com.ddmtchr.dbarefactor.dto.response.EstateResponseDto;
import com.ddmtchr.dbarefactor.service.EstateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/estate")
@RequiredArgsConstructor
@Tag(name = "Estate", description = "Жилье")
public class EstateController {
    private final EstateService estateService;

    @Operation(
            summary = "Создать жилье",
            description = "Создать жилье, роль - хост",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Созданное жилье"),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Неверный формат запроса",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Недостаточно прав",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Пользователь не найден",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Внутренняя ошибка сервера",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ProblemDetail.class)))
            }
    )
    @PostMapping
    public ResponseEntity<EstateResponseDto> addEstate(@RequestBody @Valid EstateRequestDto dto) {
        return new ResponseEntity<>(this.estateService.addEstate(dto), HttpStatus.OK);
    }

    @Operation(
            summary = "Получить список доступного жилья",
            description = "Получить список доступного жилья. Вход необязательный",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Список жилья"),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Неверный формат запроса",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Внутренняя ошибка сервера",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ProblemDetail.class)))
            }
    )
    @GetMapping
    public ResponseEntity<List<EstateResponseDto>> findAllEstate() {
        return new ResponseEntity<>(this.estateService.findAll(), HttpStatus.OK);
    }
}
