package com.ddmtchr.dbarefactor.controller;

import com.ddmtchr.dbarefactor.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/pay")
@RequiredArgsConstructor
@Tag(name = "Payment", description = "Оплата")
public class PayController {
    private final BookingService bookingService;

    @Operation(
            summary = "Оплатить бронирование",
            description = "Списывает деньги со счета гостя и резервирует их в системе, роль - гость",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Оплата успешна"),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Неверный формат запроса, или недостаточно денег, или несоответствующий статус бронирования",
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
                            description = "Пользователь или бронирование не найдено",
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
    @PutMapping("/{id}")
    public ResponseEntity<Void> payForBooking(@PathVariable Long id) {
        this.bookingService.payForBooking(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
