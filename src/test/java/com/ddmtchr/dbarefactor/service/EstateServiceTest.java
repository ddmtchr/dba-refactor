package com.ddmtchr.dbarefactor.service;

import com.ddmtchr.dbarefactor.dto.request.EstateRequestDto;
import com.ddmtchr.dbarefactor.dto.response.EstateResponseDto;
import com.ddmtchr.dbarefactor.entity.Estate;
import com.ddmtchr.dbarefactor.exception.NotFoundException;
import com.ddmtchr.dbarefactor.repository.EstateRepository;
import com.ddmtchr.dbarefactor.security.entity.User;
import com.ddmtchr.dbarefactor.security.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EstateServiceTest {

    @InjectMocks
    private EstateService service;

    @Mock
    private EstateRepository estateRepository;
    @Mock
    private UserRepository userRepository;

    @AfterEach
    void clearSecurity() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void addEstate_Success() {
        User owner = new User();
        owner.setUsername("owner");

        Estate estate = new Estate();

        when(userRepository.findByUsername("owner"))
                .thenReturn(Optional.of(owner));
        when(estateRepository.save(any()))
                .thenAnswer(i -> i.getArgument(0));

        mockAuthenticatedUser("owner");

        EstateResponseDto dto = service.addEstate(mock(EstateRequestDto.class));

        assertNotNull(dto);
        verify(estateRepository).save(argThat(e -> e.getOwner().equals(owner)));
    }

    @Test
    void addEstate_UserNotFound_Throws() {
        mockAuthenticatedUser("u");
        when(userRepository.findByUsername("u")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> service.addEstate(mock(EstateRequestDto.class)));
    }

    @Test
    void findAll_ReturnsList() {
        when(estateRepository.findAll()).thenReturn(List.of());

        assertNotNull(service.findAll());
    }

    private void mockAuthenticatedUser(String username) {
        UserDetails user = new org.springframework.security.core.userdetails.User(
                username,
                "",
                List.of()
        );

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(context);
    }
}