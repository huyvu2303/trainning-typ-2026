package com.courseshop.courseshop;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.courseshop.courseshop.model.User;
import com.courseshop.courseshop.exception.EmailAlreadyExistsException;
import com.courseshop.courseshop.repository.UserRepository;
import com.courseshop.courseshop.service.UserService;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void handleCreateUser_throwsException_whenEmailAlreadyExists() {
        User user = new User("Huy Vu", "huy@example.com", LocalDate.of(2006, 3, 23), false);
        when(userRepository.existsByEmail("huy@example.com")).thenReturn(true);

        EmailAlreadyExistsException exception = assertThrows(
                EmailAlreadyExistsException.class,
                () -> userService.handleCreateUser(user));

        assertEquals("Email already exists", exception.getMessage());
        verify(userRepository, never()).save(user);
    }

    @Test
    void handleCreateUser_savesUser_whenEmailDoesNotExist() {
        User user = new User("Huy Vu", "huy@example.com", LocalDate.of(2006, 3, 23), false);
        when(userRepository.existsByEmail("huy@example.com")).thenReturn(false);
        when(userRepository.save(user)).thenReturn(user);

        User savedUser = userService.handleCreateUser(user);

        assertSame(user, savedUser);
        verify(userRepository).save(user);
    }
}
