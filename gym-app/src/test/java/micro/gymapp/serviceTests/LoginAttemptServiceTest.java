package micro.gymapp.serviceTests;

import micro.gymapp.service.LoginAttemptService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Field;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoginAttemptServiceTest {

    private LoginAttemptService loginAttemptService;

    @BeforeEach
    void setUp() {
        loginAttemptService = new LoginAttemptService();
    }

    @Test
    void testLoginFailedIncrementsAttempts() {
        String username = "testUser";

        loginAttemptService.loginFailed(username);
        loginAttemptService.loginFailed(username);

        assertFalse(loginAttemptService.isBlocked(username));
    }

    @Test
    void testLoginFailedBlocksAfterMaxAttempts() {
        String username = "testUser";

        loginAttemptService.loginFailed(username);
        loginAttemptService.loginFailed(username);
        loginAttemptService.loginFailed(username);

        assertTrue(loginAttemptService.isBlocked(username));
    }

    @Test
    void testLoginSucceededClearsAttempts() {
        String username = "testUser";

        loginAttemptService.loginFailed(username);
        loginAttemptService.loginSucceeded(username);

        assertFalse(loginAttemptService.isBlocked(username));
    }

    @Test
    void testIsBlockedUnlocksAfterLockTime() throws Exception {
        String username = "testUser";

        loginAttemptService.loginFailed(username);
        loginAttemptService.loginFailed(username);
        loginAttemptService.loginFailed(username);

        assertTrue(loginAttemptService.isBlocked(username));

        Field attemptsField = LoginAttemptService.class.getDeclaredField("attempts");
        attemptsField.setAccessible(true);
        var attemptsMap = (Map<String, ?>) attemptsField.get(loginAttemptService);
        var loginAttempt = (LoginAttemptService.LoginAttempt) attemptsMap.get(username);

        Field lockTimeField = LoginAttemptService.LoginAttempt.class.getDeclaredField("lockTime");
        lockTimeField.setAccessible(true);
        lockTimeField.set(loginAttempt, System.currentTimeMillis() - (7 * 60 * 1000));

        assertFalse(loginAttemptService.isBlocked(username));
    }

    @Test
    void testIsBlockedWhenUserNotExists() {
        String username = "nonExistentUser";

        assertFalse(loginAttemptService.isBlocked(username));
    }
}
