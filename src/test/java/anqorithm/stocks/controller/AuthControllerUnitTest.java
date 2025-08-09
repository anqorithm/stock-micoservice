package anqorithm.stocks.controller;

import anqorithm.stocks.dto.AuthResponse;
import anqorithm.stocks.dto.LoginRequest;
import anqorithm.stocks.dto.RegisterRequest;
import anqorithm.stocks.entity.User;
import anqorithm.stocks.repository.jpa.UserRepository;
import anqorithm.stocks.security.JwtUtil;
import anqorithm.stocks.service.CustomUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
class AuthControllerUnitTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthController authController;

    private User testUser;
    private LoginRequest loginRequest;
    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setRole(User.Role.USER);
        testUser.setEnabled(true);

        loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");

        registerRequest = new RegisterRequest();
        registerRequest.setUsername("newuser");
        registerRequest.setEmail("newuser@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setFirstName("New");
        registerRequest.setLastName("User");
    }

    @Test
    void testLoginSuccess() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(testUser);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(authentication);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(jwtUtil.generateToken(any(UserDetails.class))).thenReturn("jwt-token");

        ResponseEntity<AuthResponse> response = authController.login(loginRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("jwt-token", response.getBody().getToken());
        assertEquals("testuser", response.getBody().getUsername());
        assertEquals("test@example.com", response.getBody().getEmail());
        assertEquals("USER", response.getBody().getRole());

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository).findByUsername("testuser");
        verify(jwtUtil).generateToken(any(UserDetails.class));
    }

    @Test
    void testLoginBadCredentials() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenThrow(new BadCredentialsException("Bad credentials"));

        ResponseEntity<AuthResponse> response = authController.login(loginRequest);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNull(response.getBody().getToken());
        assertNull(response.getBody().getUsername());

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verifyNoInteractions(userRepository);
        verifyNoInteractions(jwtUtil);
    }

    @Test
    void testLoginUserNotFound() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(testUser);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(authentication);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        ResponseEntity<AuthResponse> response = authController.login(loginRequest);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNull(response.getBody().getToken());

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository).findByUsername("testuser");
        verifyNoInteractions(jwtUtil);
    }

    @Test
    void testRegisterSuccess() {
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("newuser@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtUtil.generateToken("testuser")).thenReturn("jwt-token");

        ResponseEntity<Map<String, Object>> response = authController.register(registerRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("User registered successfully", response.getBody().get("message"));
        
        @SuppressWarnings("unchecked")
        AuthResponse authResponse = (AuthResponse) response.getBody().get("user");
        assertNotNull(authResponse);
        assertEquals("jwt-token", authResponse.getToken());

        verify(userRepository).existsByUsername("newuser");
        verify(userRepository).existsByEmail("newuser@example.com");
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(User.class));
        verify(jwtUtil).generateToken("testuser");
    }

    @Test
    void testRegisterUsernameAlreadyExists() {
        when(userRepository.existsByUsername("newuser")).thenReturn(true);

        ResponseEntity<Map<String, Object>> response = authController.register(registerRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Username already exists", response.getBody().get("error"));

        verify(userRepository).existsByUsername("newuser");
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(passwordEncoder, jwtUtil);
    }

    @Test
    void testRegisterEmailAlreadyExists() {
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("newuser@example.com")).thenReturn(true);

        ResponseEntity<Map<String, Object>> response = authController.register(registerRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Email already exists", response.getBody().get("error"));

        verify(userRepository).existsByUsername("newuser");
        verify(userRepository).existsByEmail("newuser@example.com");
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(passwordEncoder, jwtUtil);
    }

    @Test
    void testRegisterException() {
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("newuser@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenThrow(new RuntimeException("Database error"));

        ResponseEntity<Map<String, Object>> response = authController.register(registerRequest);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Registration failed", response.getBody().get("error"));

        verify(userRepository).existsByUsername("newuser");
        verify(userRepository).existsByEmail("newuser@example.com");
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(User.class));
        verifyNoInteractions(jwtUtil);
    }

    @Test
    void testValidateTokenSuccess() {
        when(jwtUtil.extractUsername("valid-token")).thenReturn("testuser");
        when(jwtUtil.isTokenValid("valid-token")).thenReturn(true);

        ResponseEntity<Map<String, Object>> response = authController.validateToken("Bearer valid-token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(true, response.getBody().get("valid"));
        assertEquals("testuser", response.getBody().get("username"));

        verify(jwtUtil).extractUsername("valid-token");
        verify(jwtUtil).isTokenValid("valid-token");
    }

    @Test
    void testValidateTokenInvalid() {
        when(jwtUtil.extractUsername("invalid-token")).thenReturn("testuser");
        when(jwtUtil.isTokenValid("invalid-token")).thenReturn(false);

        ResponseEntity<Map<String, Object>> response = authController.validateToken("Bearer invalid-token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(false, response.getBody().get("valid"));
        assertNull(response.getBody().get("username"));

        verify(jwtUtil).extractUsername("invalid-token");
        verify(jwtUtil).isTokenValid("invalid-token");
    }

    @Test
    void testValidateTokenMissingBearer() {
        ResponseEntity<Map<String, Object>> response = authController.validateToken("invalid-token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(false, response.getBody().get("valid"));

        verifyNoInteractions(jwtUtil);
    }

    @Test
    void testValidateTokenNoHeader() {
        ResponseEntity<Map<String, Object>> response = authController.validateToken(null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(false, response.getBody().get("valid"));

        verifyNoInteractions(jwtUtil);
    }

    @Test
    void testValidateTokenException() {
        when(jwtUtil.extractUsername("error-token")).thenThrow(new RuntimeException("JWT error"));

        ResponseEntity<Map<String, Object>> response = authController.validateToken("Bearer error-token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(false, response.getBody().get("valid"));

        verify(jwtUtil).extractUsername("error-token");
    }

    @Test
    void testAuthenticationManagerInteraction() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(testUser);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(authentication);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(jwtUtil.generateToken(any(UserDetails.class))).thenReturn("jwt-token");

        authController.login(loginRequest);

        verify(authenticationManager).authenticate(argThat(token -> 
            token instanceof UsernamePasswordAuthenticationToken &&
            token.getPrincipal().equals("testuser") &&
            token.getCredentials().equals("password123")
        ));
    }

    @Test
    void testPasswordEncodingInteraction() {
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("newuser@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtUtil.generateToken("testuser")).thenReturn("jwt-token");

        authController.register(registerRequest);

        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(argThat(user -> 
            user.getPassword().equals("encodedPassword") &&
            user.getUsername().equals("newuser") &&
            user.getEmail().equals("newuser@example.com") &&
            user.getRole() == User.Role.USER
        ));
    }
}