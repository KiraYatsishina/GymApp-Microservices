package micro.authserver.service;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class LoginAttemptService {

    private final Map<String, LoginAttempt> attempts = new ConcurrentHashMap<>();

    private static final int MAX_ATTEMPTS = 3;
    private static final long LOCK_TIME_MS = 5 * 60 * 1000;

    public void loginFailed(String username) {
        LoginAttempt attempt = attempts.getOrDefault(username, new LoginAttempt());
        attempt.incrementAttempts();
        if (attempt.getAttempts() >= MAX_ATTEMPTS) {
            attempt.setLockTime(System.currentTimeMillis());
        }
        attempts.put(username, attempt);
    }

    public void loginSucceeded(String username) {
        attempts.remove(username);
    }

    public boolean isBlocked(String username) {
        LoginAttempt attempt = attempts.get(username);
        if (attempt == null) {
            return false;
        }
        if (attempt.getAttempts() >= MAX_ATTEMPTS) {
            long timeSinceLock = System.currentTimeMillis() - attempt.getLockTime();
            if (timeSinceLock > LOCK_TIME_MS) {
                attempts.remove(username);
                return false;
            }
            return true;
        }
        return false;
    }

    public static class LoginAttempt {
        private int attempts;
        private long lockTime;

        public void incrementAttempts() {
            attempts++;
        }

        public int getAttempts() {
            return attempts;
        }

        public long getLockTime() {
            return lockTime;
        }

        public void setLockTime(long lockTime) {
            this.lockTime = lockTime;
        }
    }
}
