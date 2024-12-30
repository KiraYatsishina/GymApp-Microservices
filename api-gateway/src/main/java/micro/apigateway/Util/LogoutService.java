package micro.apigateway.Util;

import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Service
public class LogoutService {

    private final Set<String> invalidTokens = Collections.synchronizedSet(new HashSet<>());

    public void addInvalidToken(String token) {
        invalidTokens.add(token);
    }

    public boolean isTokenInvalid(String token) {
        return invalidTokens.contains(token);
    }
}
