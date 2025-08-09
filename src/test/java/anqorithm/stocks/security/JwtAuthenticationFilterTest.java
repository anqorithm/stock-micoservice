package anqorithm.stocks.security;

import anqorithm.stocks.entity.User;
import anqorithm.stocks.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
class JwtAuthenticationFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.clearContext();
        
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password");
        testUser.setRole(User.Role.USER);
        testUser.setEnabled(true);
    }

    @Test
    void testDoFilterInternal_ValidToken() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("Bearer valid-jwt-token");
        when(jwtUtil.extractUsername("valid-jwt-token")).thenReturn("testuser");
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(testUser);
        when(jwtUtil.validateToken("valid-jwt-token", testUser)).thenReturn(true);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertEquals("testuser", authentication.getName());
        assertTrue(authentication.isAuthenticated());
        assertEquals(testUser, authentication.getPrincipal());
        assertNull(authentication.getCredentials());
        assertEquals(1, authentication.getAuthorities().size());

        verify(request).getHeader("Authorization");
        verify(jwtUtil).extractUsername("valid-jwt-token");
        verify(userDetailsService).loadUserByUsername("testuser");
        verify(jwtUtil).validateToken("valid-jwt-token", testUser);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_NoAuthorizationHeader() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn(null);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNull(authentication);

        verify(request).getHeader("Authorization");
        verifyNoInteractions(jwtUtil, userDetailsService);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_InvalidAuthorizationHeader() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("Invalid token format");

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNull(authentication);

        verify(request).getHeader("Authorization");
        verifyNoInteractions(jwtUtil, userDetailsService);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_TokenExtractionException() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("Bearer invalid-token");
        when(jwtUtil.extractUsername("invalid-token")).thenThrow(new RuntimeException("Invalid JWT"));

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNull(authentication);

        verify(request).getHeader("Authorization");
        verify(jwtUtil).extractUsername("invalid-token");
        verifyNoInteractions(userDetailsService);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_InvalidToken() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("Bearer invalid-jwt-token");
        when(jwtUtil.extractUsername("invalid-jwt-token")).thenReturn("testuser");
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(testUser);
        when(jwtUtil.validateToken("invalid-jwt-token", testUser)).thenReturn(false);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNull(authentication);

        verify(request).getHeader("Authorization");
        verify(jwtUtil).extractUsername("invalid-jwt-token");
        verify(userDetailsService).loadUserByUsername("testuser");
        verify(jwtUtil).validateToken("invalid-jwt-token", testUser);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_AlreadyAuthenticated() throws ServletException, IOException {
        // Set up existing authentication
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication existingAuth = mock(Authentication.class);
        when(securityContext.getAuthentication()).thenReturn(existingAuth);
        SecurityContextHolder.setContext(securityContext);

        when(request.getHeader("Authorization")).thenReturn("Bearer valid-jwt-token");
        when(jwtUtil.extractUsername("valid-jwt-token")).thenReturn("testuser");

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(request).getHeader("Authorization");
        verify(jwtUtil).extractUsername("valid-jwt-token");
        verifyNoInteractions(userDetailsService);
        verify(jwtUtil, never()).validateToken(anyString(), any(UserDetails.class));
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_UserDetailsLoadException() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("Bearer valid-jwt-token");
        when(jwtUtil.extractUsername("valid-jwt-token")).thenReturn("testuser");
        when(userDetailsService.loadUserByUsername("testuser"))
            .thenThrow(new RuntimeException("User not found"));

        // This should throw an exception since the filter doesn't handle it
        assertThrows(RuntimeException.class, () -> {
            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        });

        verify(request).getHeader("Authorization");
        verify(jwtUtil).extractUsername("valid-jwt-token");
        verify(userDetailsService).loadUserByUsername("testuser");
        verifyNoMoreInteractions(jwtUtil);
        // filterChain.doFilter is not called when exception occurs
    }

    @Test
    void testDoFilterInternal_NullUsername() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("Bearer valid-jwt-token");
        when(jwtUtil.extractUsername("valid-jwt-token")).thenReturn(null);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNull(authentication);

        verify(request).getHeader("Authorization");
        verify(jwtUtil).extractUsername("valid-jwt-token");
        verifyNoInteractions(userDetailsService);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_EmptyUsername() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("Bearer valid-jwt-token");
        when(jwtUtil.extractUsername("valid-jwt-token")).thenReturn("");
        when(userDetailsService.loadUserByUsername("")).thenReturn(testUser);
        when(jwtUtil.validateToken("valid-jwt-token", testUser)).thenReturn(false);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNull(authentication);

        verify(request).getHeader("Authorization");
        verify(jwtUtil).extractUsername("valid-jwt-token");
        // Empty username is still processed by the filter
        verify(userDetailsService).loadUserByUsername("");
        verify(jwtUtil).validateToken("valid-jwt-token", testUser);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_BearerTokenWithSpaces() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("Bearer   valid-jwt-token");
        when(jwtUtil.extractUsername("  valid-jwt-token")).thenReturn("testuser");
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(testUser);
        when(jwtUtil.validateToken("  valid-jwt-token", testUser)).thenReturn(true);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertEquals("testuser", authentication.getName());

        verify(request).getHeader("Authorization");
        verify(jwtUtil).extractUsername("  valid-jwt-token");
        verify(userDetailsService).loadUserByUsername("testuser");
        verify(jwtUtil).validateToken("  valid-jwt-token", testUser);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_AdminUser() throws ServletException, IOException {
        User adminUser = new User();
        adminUser.setUsername("admin");
        adminUser.setEmail("admin@example.com");
        adminUser.setPassword("password");
        adminUser.setRole(User.Role.ADMIN);
        adminUser.setEnabled(true);

        when(request.getHeader("Authorization")).thenReturn("Bearer admin-jwt-token");
        when(jwtUtil.extractUsername("admin-jwt-token")).thenReturn("admin");
        when(userDetailsService.loadUserByUsername("admin")).thenReturn(adminUser);
        when(jwtUtil.validateToken("admin-jwt-token", adminUser)).thenReturn(true);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertEquals("admin", authentication.getName());
        assertEquals(adminUser, authentication.getPrincipal());
        assertTrue(authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")));

        verify(request).getHeader("Authorization");
        verify(jwtUtil).extractUsername("admin-jwt-token");
        verify(userDetailsService).loadUserByUsername("admin");
        verify(jwtUtil).validateToken("admin-jwt-token", adminUser);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_FilterChainCalledOnJwtException() throws ServletException, IOException {
        // Test that filter chain is called when JWT extraction fails (exception is caught)
        when(request.getHeader("Authorization")).thenReturn("Bearer token");
        when(jwtUtil.extractUsername("token")).thenThrow(new RuntimeException("JWT error"));

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(userDetailsService);
    }

    @Test
    void testDoFilterInternal_SecurityContextDetails() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("Bearer valid-jwt-token");
        when(jwtUtil.extractUsername("valid-jwt-token")).thenReturn("testuser");
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(testUser);
        when(jwtUtil.validateToken("valid-jwt-token", testUser)).thenReturn(true);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertNotNull(authentication.getDetails());
        
        // Verify the authentication token was properly configured
        assertTrue(authentication instanceof org.springframework.security.authentication.UsernamePasswordAuthenticationToken);
        assertEquals(testUser, authentication.getPrincipal());
        assertNull(authentication.getCredentials());
        assertNotNull(authentication.getAuthorities());
        assertFalse(authentication.getAuthorities().isEmpty());

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_CaseInsensitiveBearerCheck() throws ServletException, IOException {
        // Test different case variations of "Bearer"
        when(request.getHeader("Authorization")).thenReturn("bearer valid-jwt-token");

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNull(authentication); // Should not authenticate with lowercase "bearer"

        verify(request).getHeader("Authorization");
        verifyNoInteractions(jwtUtil, userDetailsService);
        verify(filterChain).doFilter(request, response);
    }
}