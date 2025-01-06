package micro.trainersworkload.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class JwtAuthenticationFilterTest {

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.clearContext();
    }

    @Test
    void testDoFilterInternal_WithValidToken() throws Exception {
        String validToken = "valid.jwt.token";
        String username = "testuser";
        List<String> roles = List.of("ROLE_TRAINEE");

        when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);
        when(jwtUtils.isExpired(validToken)).thenReturn(false);
        when(jwtUtils.getUsername(validToken)).thenReturn(username);
        when(jwtUtils.getRoles(validToken)).thenReturn(roles);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(jwtUtils, times(1)).isExpired(validToken);
        verify(jwtUtils, times(1)).getUsername(validToken);
        verify(jwtUtils, times(1)).getRoles(validToken);
        verify(filterChain, times(1)).doFilter(request, response);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(username, SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }

    @Test
    void testDoFilterInternal_WithExpiredToken() throws Exception {
        String expiredToken = "expired.jwt.token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + expiredToken);
        when(jwtUtils.isExpired(expiredToken)).thenReturn(true);

        StringWriter responseWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(jwtUtils, times(1)).isExpired(expiredToken);
        verify(response, times(1)).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        assertTrue(responseWriter.toString().contains("Invalid token: Token is expired"));
        verify(filterChain, never()).doFilter(any(), any());
    }

    @Test
    void testDoFilterInternal_WithoutAuthorizationHeader() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(jwtUtils, never()).isExpired(anyString());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_WithInvalidToken() throws Exception {
        String invalidToken = "invalid.jwt.token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + invalidToken);
        when(jwtUtils.isExpired(invalidToken)).thenThrow(new RuntimeException("Invalid token"));

        StringWriter responseWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(jwtUtils, times(1)).isExpired(invalidToken);
        verify(response, times(1)).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        assertTrue(responseWriter.toString().contains("Invalid token: Invalid token"));
        verify(filterChain, never()).doFilter(any(), any());
    }
}