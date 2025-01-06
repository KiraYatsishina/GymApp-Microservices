package micro.trainersworkload.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.security.Key;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilsTest {

    @InjectMocks
    private JwtUtils jwtUtils;

    private final String secret = "VGVzdGluZzEyMzRHZW5lcmF0ZWRTZWNyZXRLa2tleUluQmFzZTY0";
    private String token;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        var secretField = JwtUtils.class.getDeclaredField("secret");
        secretField.setAccessible(true);
        secretField.set(jwtUtils, secret);

        jwtUtils.initKey();

        Key key = Keys.hmacShaKeyFor(secret.getBytes());
        token = Jwts.builder()
                .setSubject("testuser")
                .claim("roles", List.of("ROLE_TRAINEE", "ROLE_TRAINER"))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1 час
                .signWith(key)
                .compact();
    }

    @Test
    void testInitKey() {
        assertNotNull(jwtUtils);
    }

    @Test
    void testGetClaims() {
        Claims claims = jwtUtils.getClaims(token);
        assertNotNull(claims);
        assertEquals("testuser", claims.getSubject());
        assertEquals(List.of("ROLE_TRAINEE", "ROLE_TRAINER"), claims.get("roles", List.class));
    }

    @Test
    void testIsExpired_NotExpired() {
        boolean isExpired = jwtUtils.isExpired(token);
        assertFalse(isExpired);
    }

}
