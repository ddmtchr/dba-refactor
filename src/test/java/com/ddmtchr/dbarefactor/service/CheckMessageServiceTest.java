package com.ddmtchr.dbarefactor.service;

import com.ddmtchr.dbarefactor.dto.producer.CheckDto;
import com.ddmtchr.dbarefactor.entity.CheckMessage;
import com.ddmtchr.dbarefactor.repository.CheckMessageRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CheckMessageServiceTest {

    @InjectMocks
    private CheckMessageService service;

    @Mock
    private CheckMessageRepository repository;

    @Test
    void save_Success() {
        CheckDto dto = mock(CheckDto.class);
        CheckMessage entity = mock(CheckMessage.class);

        when(repository.save(any())).thenReturn(entity);

        assertNotNull(service.save(dto));
    }

    @Test
    void findAllUnsent_ReturnsList() {
        when(repository.findAllBySentFalseOrderByBookingId(any()))
                .thenReturn(List.of());

        assertNotNull(service.findAllUnsent(10));
    }

    @Test
    void markSentByIds_CallsRepository() {
        Set<Long> ids = Set.of(1L, 2L);

        service.markSentByIds(ids);

        verify(repository).setSentByBookingIdIn(ids);
    }
}