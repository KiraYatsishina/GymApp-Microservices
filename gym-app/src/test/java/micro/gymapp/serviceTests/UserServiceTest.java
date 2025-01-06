package micro.gymapp.serviceTests;

import micro.gymapp.model.Role;
import micro.gymapp.model.User;
import micro.gymapp.repository.UserRepository;
import micro.gymapp.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    void testFindByUsername_userExists_returnsUser() {
        User user = new User();
        user.setUsername("testUser");

        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));

        Optional<User> result = userService.findByUsername("testUser");

        assertTrue(result.isPresent());
        assertEquals("testUser", result.get().getUsername());
        verify(userRepository, times(1)).findByUsername("testUser");
    }

    @Test
    void testFindByUsername_userDoesNotExist_returnsEmpty() {
        when(userRepository.findByUsername("nonexistentUser")).thenReturn(Optional.empty());

        Optional<User> result = userService.findByUsername("nonexistentUser");

        assertFalse(result.isPresent());
        verify(userRepository, times(1)).findByUsername("nonexistentUser");
    }

    @Test
    void testLoadUserByUsername_userExists_returnsUserDetails() {
        User user = new User();
        user.setUsername("testUser");
        user.setPassword("password123");
        user.setRole(Role.ROLE_TRAINEE);

        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));

        var userDetails = userService.loadUserByUsername("testUser");

        assertNotNull(userDetails);
        assertEquals("testUser", userDetails.getUsername());
        assertEquals("password123", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_TRAINEE")));
        verify(userRepository, times(1)).findByUsername("testUser");
    }

    @Test
    void testLoadUserByUsername_userDoesNotExist_throwsException() {
        when(userRepository.findByUsername("nonexistentUser")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername("nonexistentUser"));
        verify(userRepository, times(1)).findByUsername("nonexistentUser");
    }

    @Test
    void testDeleteUserByUsername_userExists_returnsTrue() {
        User user = new User();
        user.setUsername("testUser");

        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));

        boolean result = userService.deleteUserByUsername("testUser");

        assertTrue(result);
        verify(userRepository, times(1)).findByUsername("testUser");
        verify(userRepository, times(1)).deleteByUsername("testUser");
    }

    @Test
    void testDeleteUserByUsername_userDoesNotExist_returnsFalse() {
        when(userRepository.findByUsername("nonexistentUser")).thenReturn(Optional.empty());

        boolean result = userService.deleteUserByUsername("nonexistentUser");

        assertFalse(result);
        verify(userRepository, times(1)).findByUsername("nonexistentUser");
        verify(userRepository, never()).deleteByUsername(anyString());
    }

    @Test
    void testChangeStatusByUsername_userExists_updatesStatusAndReturnsTrue() {
        User user = new User();
        user.setUsername("testUser");
        user.setActive(false);

        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));

        boolean result = userService.changeStatusByUsername("testUser", true);

        assertTrue(result);
        assertTrue(user.isActive());
        verify(userRepository, times(1)).findByUsername("testUser");
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testChangeStatusByUsername_userDoesNotExist_returnsFalse() {
        when(userRepository.findByUsername("nonexistentUser")).thenReturn(Optional.empty());

        boolean result = userService.changeStatusByUsername("nonexistentUser", true);

        assertFalse(result);
        verify(userRepository, times(1)).findByUsername("nonexistentUser");
        verify(userRepository, never()).save(any(User.class));
    }


    @Test
    void testChangePassword_Success() {
        User mockUser = new User();
        mockUser.setPassword("encodedOldPassword");
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches("oldPassword", "encodedOldPassword")).thenReturn(true);
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");

        boolean result = userService.changePassword("testUser", "oldPassword", "newPassword");

        assertTrue(result);
        assertEquals("encodedNewPassword", mockUser.getPassword());
    }

    @Test
    void testChangePassword_InvalidOldPassword() {
        User mockUser = new User();
        mockUser.setPassword("encodedOldPassword");
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches("wrongPassword", "encodedOldPassword")).thenReturn(false);

        boolean result = userService.changePassword("testUser", "wrongPassword", "newPassword");

        assertFalse(result);
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void testChangePassword_UserNotFound() {
        when(userRepository.findByUsername("nonexistentUser")).thenReturn(Optional.empty());

        boolean result = userService.changePassword("nonexistentUser", "oldPassword", "newPassword");

        assertFalse(result);
    }

    @Test
    void testSignUpUser_Success() {
        User mockUser = new User();
        mockUser.setFirstName("John");
        mockUser.setLastName("Doe");

        when(userRepository.countByFirstNameAndLastName("John", "Doe")).thenReturn(0L);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        var result = userService.signUpUser(mockUser, Role.ROLE_TRAINEE, user -> userRepository.save(user));

        assertTrue(result.isPresent());
        assertEquals("John.Doe", mockUser.getUsername());
        assertEquals("encodedPassword", mockUser.getPassword());
        assertEquals(Role.ROLE_TRAINEE, mockUser.getRole());
    }

    @Test
    void testGenerateUniqueUsername() {
        String username = userService.generateUniqueUsername("John", "Doe", 2);
        assertEquals("John.Doe2", username);
    }

    @Test
    void testGeneratePassword() {
        String password = userService.generatePassword();
        assertNotNull(password);
        assertEquals(10, password.length());
    }
}
