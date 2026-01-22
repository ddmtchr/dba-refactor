package com.ddmtchr.dbarefactor.security.service;

import com.ddmtchr.dbarefactor.dto.request.RegisterRequest;
import com.ddmtchr.dbarefactor.security.entity.User;
import com.ddmtchr.dbarefactor.security.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService service;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    void loadUserByUsername_Found_ReturnsUser() {
        User user = mock(User.class);
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));

        UserDetails result = service.loadUserByUsername("user");

        assertEquals(user, result);
    }

    @Test
    void loadUserByUsername_NotFound_Throws() {
        when(userRepository.findByUsername("user")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> service.loadUserByUsername("user"));
    }

    @Test
    void existsByUsername_ReturnsTrue() {
        when(userRepository.existsByUsername("user")).thenReturn(true);

        assertTrue(service.existsByUsername("user"));
    }

    @Test
    void addUser_EncodesPasswordAndSaves() {
        RegisterRequest request = mock(RegisterRequest.class);
        when(request.getUsername()).thenReturn("u");
        when(request.getPassword()).thenReturn("p");
        when(request.getEmail()).thenReturn("e");
        when(request.getMoney()).thenReturn(100L);
        when(request.getRoles()).thenReturn(Set.of("GUEST"));

        when(passwordEncoder.encode("p")).thenReturn("encoded");
        when(userRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        User result = service.addUser(request);

        assertEquals("u", result.getUsername());
        assertEquals("encoded", result.getPassword());
        assertEquals(1, result.getRoles().size());
    }
}
