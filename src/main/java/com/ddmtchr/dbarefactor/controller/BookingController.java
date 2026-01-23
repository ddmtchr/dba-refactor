package com.ddmtchr.dbarefactor.controller;

import com.ddmtchr.dbarefactor.dto.request.BookingChangesDto;
import com.ddmtchr.dbarefactor.dto.request.BookingRequestDto;
import com.ddmtchr.dbarefactor.dto.response.BookingResponseDto;
import com.ddmtchr.dbarefactor.security.util.SecurityUtil;
import com.ddmtchr.dbarefactor.service.BookingService;
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
@RequestMapping("/bookings")
@RequiredArgsConstructor
@Tag(name = "Booking", description = "Бронирования")
public class BookingController {

    private final BookingService bookingService;

    @Operation(
            summary = "Создать бронирование",
            description = "Создать бронирование",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Созданное бронирование"),
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
                            description = "Гость или жильё не найдено",
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
    public ResponseEntity<BookingResponseDto> createBooking(@RequestBody @Valid BookingRequestDto bookingRequestDto) {
        return new ResponseEntity<>(this.bookingService.addBooking(bookingRequestDto), HttpStatus.CREATED);
    }

    @Operation(
            summary = "Получить все свои бронирования (роль - хост)",
            description = "Получить все бронирования текущего пользователя, если у него есть роль хост",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Список бронирований"),
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
                            responseCode = "500",
                            description = "Внутренняя ошибка сервера",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ProblemDetail.class)))
            }
    )
    @GetMapping("/byHost")
    public ResponseEntity<List<BookingResponseDto>> findAllBookingsOfHost() {
        String username = SecurityUtil.getCurrentUser().getUsername();
        return new ResponseEntity<>(this.bookingService.findAllByHost(username), HttpStatus.OK);
    }

    @Operation(
            summary = "Получить все свои бронирования (роль - гость)",
            description = "Получить все бронирования текущего пользователя, если у него есть роль гость",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Список бронирований"),
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
                            responseCode = "500",
                            description = "Внутренняя ошибка сервера",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ProblemDetail.class)))
            }
    )
    @GetMapping("/byGuest")
    public ResponseEntity<List<BookingResponseDto>> findAllBookingsOfGuest() {
        String username = SecurityUtil.getCurrentUser().getUsername();
        return new ResponseEntity<>(this.bookingService.findAllByGuest(username), HttpStatus.OK);
    }

    @Operation(
            summary = "Получить бронирование по id",
            description = "Получить бронирование по id",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Бронирование"),
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
                            description = "Бронирование не найдено",
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
    @GetMapping("/{id}")
    public ResponseEntity<BookingResponseDto> findBookingById(@PathVariable Long id) {
        return new ResponseEntity<>(this.bookingService.findById(id), HttpStatus.OK);
    }

    @Operation(
            summary = "Принять бронирование по id",
            description = "Помечает бронирование как принятое, роль - хост",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Бронирование"),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Неверный формат запроса или несоответствующий статус бронирования",
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
                            description = "Бронирование не найдено",
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
    @PutMapping("/{id}/approve")
    public ResponseEntity<BookingResponseDto> approveBookingById(@PathVariable Long id) {
        return new ResponseEntity<>(this.bookingService.approveById(id), HttpStatus.OK);
    }

    @Operation(
            summary = "Отклонить бронирование по id",
            description = "Помечает бронирование как отклоненное, роль - хост",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Бронирование"),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Неверный формат запроса или несоответствующий статус бронирования",
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
                            description = "Бронирование не найдено",
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
    @PutMapping("/{id}/reject")
    public ResponseEntity<BookingResponseDto> rejectBookingById(@PathVariable Long id) {
        return new ResponseEntity<>(this.bookingService.rejectById(id), HttpStatus.OK);
    }

    @Operation(
            summary = "Предложить изменения по бронированию",
            description = "Изменяет бронирование, выставляет ему статус для проверки хостом, роль - гость",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Бронирование"),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Неверный формат запроса или несоответствующий статус бронирования",
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
                            description = "Бронирование не найдено",
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
    @PutMapping("/{id}/suggestChanges")
    public ResponseEntity<BookingResponseDto> suggestBookingChangesById(@PathVariable Long id, @RequestBody @Valid BookingChangesDto dto) {
        return new ResponseEntity<>(this.bookingService.changeById(id, dto), HttpStatus.OK);
    }

    @Operation(
            summary = "Принять изменения по бронированию",
            description = "Помечает бронирование как принятое после предложения изменений, роль - хост",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Бронирование"),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Неверный формат запроса или несоответствующий статус бронирования",
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
                            description = "Бронирование не найдено",
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
    @PutMapping("/{id}/approveChanges")
    public ResponseEntity<BookingResponseDto> approveBookingChangesById(@PathVariable Long id) {
        return new ResponseEntity<>(this.bookingService.approveChangesById(id), HttpStatus.OK);
    }

    @Operation(
            summary = "Отклонить изменения по бронированию",
            description = "Помечает бронирование как отклоненное и закрывает его после предложения изменений, роль - хост",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Бронирование"),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Неверный формат запроса или несоответствующий статус бронирования",
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
                            description = "Бронирование не найдено",
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
    @PutMapping("/{id}/rejectChanges")
    public ResponseEntity<BookingResponseDto> rejectBookingChangesById(@PathVariable Long id) {
        return new ResponseEntity<>(this.bookingService.rejectChangesById(id), HttpStatus.OK);
    }

    @Operation(
            summary = "Заехать в жильё",
            description = "Помечает бронирование как начатое и запускает процесс выплаты денег хосту, роль - гость",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Бронирование"),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Неверный формат запроса или несоответствующий статус бронирования",
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
                            description = "Бронирование не найдено",
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
    @PutMapping("/{id}/checkIn")
    public ResponseEntity<BookingResponseDto> checkInById(@PathVariable Long id) {
        return new ResponseEntity<>(this.bookingService.checkInById(id), HttpStatus.OK);
    }
}
