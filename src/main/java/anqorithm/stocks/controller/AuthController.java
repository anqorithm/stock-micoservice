package anqorithm.stocks.controller;

import anqorithm.stocks.dto.AuthResponse;
import anqorithm.stocks.dto.LoginRequest;
import anqorithm.stocks.dto.RegisterRequest;
import anqorithm.stocks.entity.User;
import anqorithm.stocks.repository.UserRepository;
import anqorithm.stocks.security.JwtUtil;
import anqorithm.stocks.service.CustomUserDetailsService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            logger.info("Login attempt for user: {}", loginRequest.getUsername());

            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(), 
                    loginRequest.getPassword()
                )
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

            String token = jwtUtil.generateToken(userDetails);

            AuthResponse authResponse = new AuthResponse(
                token,
                user.getUsername(),
                user.getEmail(),
                user.getRole().name()
            );

            logger.info("Login successful for user: {}", loginRequest.getUsername());
            return ResponseEntity.ok(authResponse);

        } catch (BadCredentialsException e) {
            logger.warn("Login failed for user: {} - Invalid credentials", loginRequest.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new AuthResponse());
        } catch (Exception e) {
            logger.error("Login error for user: {} - {}", loginRequest.getUsername(), e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new AuthResponse());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            logger.info("Registration attempt for user: {}", registerRequest.getUsername());

            if (userRepository.existsByUsername(registerRequest.getUsername())) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Username already exists"));
            }

            if (userRepository.existsByEmail(registerRequest.getEmail())) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Email already exists"));
            }

            User user = new User();
            user.setUsername(registerRequest.getUsername());
            user.setEmail(registerRequest.getEmail());
            user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
            user.setFirstName(registerRequest.getFirstName());
            user.setLastName(registerRequest.getLastName());
            user.setRole(User.Role.USER);

            User savedUser = userRepository.save(user);

            String token = jwtUtil.generateToken(savedUser.getUsername());

            AuthResponse authResponse = new AuthResponse(
                token,
                savedUser.getUsername(),
                savedUser.getEmail(),
                savedUser.getRole().name()
            );

            Map<String, Object> response = new HashMap<>();
            response.put("message", "User registered successfully");
            response.put("user", authResponse);

            logger.info("Registration successful for user: {}", registerRequest.getUsername());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            logger.error("Registration error for user: {} - {}", registerRequest.getUsername(), e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Registration failed"));
        }
    }

    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateToken(@RequestHeader("Authorization") String token) {
        try {
            if (token != null && token.startsWith("Bearer ")) {
                String jwtToken = token.substring(7);
                String username = jwtUtil.extractUsername(jwtToken);
                
                if (jwtUtil.isTokenValid(jwtToken)) {
                    return ResponseEntity.ok(Map.of(
                        "valid", true,
                        "username", username
                    ));
                }
            }
            return ResponseEntity.ok(Map.of("valid", false));
        } catch (Exception e) {
            logger.error("Token validation error: {}", e.getMessage());
            return ResponseEntity.ok(Map.of("valid", false));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser(Authentication authentication) {
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Not authenticated"));
            }

            String username = authentication.getName();
            User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("username", user.getUsername());
            userInfo.put("email", user.getEmail());
            userInfo.put("firstName", user.getFirstName());
            userInfo.put("lastName", user.getLastName());
            userInfo.put("role", user.getRole().name());
            userInfo.put("createdAt", user.getCreatedAt());

            return ResponseEntity.ok(userInfo);

        } catch (Exception e) {
            logger.error("Error getting current user: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to get user information"));
        }
    }
}