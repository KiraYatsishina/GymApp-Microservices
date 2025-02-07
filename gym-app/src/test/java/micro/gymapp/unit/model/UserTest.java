package micro.gymapp.unit.model;

import micro.gymapp.model.Role;
import micro.gymapp.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User(1L, "John", "Doe", "john_doe", "password", true, Role.ROLE_TRAINEE);
    }

    @Test
    void testGetFullName() {
        String expectedFullName = "John Doe";
        assertEquals(expectedFullName, user.getFullName());
    }

    @Test
    void testEquals_SameObject() {
        assertTrue(user.equals(user));
    }

    @Test
    void testEquals_DifferentObject() {
        User anotherUser = new User(1L, "John", "Doe", "john_doe", "password", true, Role.ROLE_TRAINEE);
        assertTrue(user.equals(anotherUser));
    }

    @Test
    void testEquals_NullObject() {
        assertFalse(user.equals(null));
    }

    @Test
    void testHashCode() {
        User anotherUser = new User(1L, "John", "Doe", "john_doe", "password", true, Role.ROLE_TRAINER);
        assertEquals(user.hashCode(), anotherUser.hashCode());
    }

    @Test
    void testGetAuthorities() {
        assertNotNull(user.getAuthorities(), "Authorities should not be null");
        assertEquals(1, user.getAuthorities().size(), "User should have exactly one authority");
        assertTrue(user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_TRAINEE")));
    }

    @Test
    void testIsAccountNonExpired() {
        assertTrue(user.isAccountNonExpired());
    }

    @Test
    void testIsAccountNonLocked() {
        assertTrue(user.isAccountNonLocked());
    }

    @Test
    void testIsCredentialsNonExpired() {
        assertTrue(user.isCredentialsNonExpired());
    }

    @Test
    void testIsEnabled() {
        assertTrue(user.isEnabled());
    }

    @Test
    void testEquals_DifferentUserId() {
        User anotherUser = new User(2L, "Jane", "Doe", "jane_doe", "password", true, Role.ROLE_TRAINEE);
        assertFalse(user.equals(anotherUser));
    }

    @Test
    void testEquals_DifferentClass() {
        String randomObject = "Random Object";
        assertFalse(user.equals(randomObject));
    }
}
