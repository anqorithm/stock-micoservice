package anqorithm.stocks.service;

import anqorithm.stocks.entity.User;
import anqorithm.stocks.repository.jpa.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService userDetailsService;

    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password123");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setRole(User.Role.USER);
        testUser.setEnabled(true);
    }

    @Test
    void testLoadUserByUsername_Success() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        UserDetails result = userDetailsService.loadUserByUsername("testuser");

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("password123", result.getPassword());
        assertTrue(result.isEnabled());
        assertEquals(1, result.getAuthorities().size());
        assertTrue(result.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));
        
        verify(userRepository, times(1)).findByUsername("testuser");
    }

    @Test
    void testLoadUserByUsername_UserNotFound() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> 
            userDetailsService.loadUserByUsername("nonexistent"));

        assertEquals("User not found: nonexistent", exception.getMessage());
        verify(userRepository, times(1)).findByUsername("nonexistent");
    }

    @Test
    void testLoadUserByUsername_NullUsername() {
        when(userRepository.findByUsername(null)).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> 
            userDetailsService.loadUserByUsername(null));

        assertEquals("User not found: null", exception.getMessage());
        verify(userRepository, times(1)).findByUsername(null);
    }

    @Test
    void testLoadUserByUsername_EmptyUsername() {
        when(userRepository.findByUsername("")).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> 
            userDetailsService.loadUserByUsername(""));

        assertEquals("User not found: ", exception.getMessage());
        verify(userRepository, times(1)).findByUsername("");
    }

    @Test
    void testLoadUserByEmail_Success() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        UserDetails result = userDetailsService.loadUserByEmail("test@example.com");

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("test@example.com", ((User) result).getEmail());
        assertTrue(result.isEnabled());
        
        verify(userRepository, times(1)).findByEmail("test@example.com");
    }

    @Test
    void testLoadUserByEmail_UserNotFound() {
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> 
            userDetailsService.loadUserByEmail("nonexistent@example.com"));

        assertEquals("User not found with email: nonexistent@example.com", exception.getMessage());
        verify(userRepository, times(1)).findByEmail("nonexistent@example.com");
    }

    @Test
    void testLoadUserByEmail_NullEmail() {
        when(userRepository.findByEmail(null)).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> 
            userDetailsService.loadUserByEmail(null));

        assertEquals("User not found with email: null", exception.getMessage());
        verify(userRepository, times(1)).findByEmail(null);
    }

    @Test
    void testLoadUserByUsernameOrEmail_FoundByUsername() {
        when(userRepository.findByUsernameOrEmail("testuser", "testuser")).thenReturn(Optional.of(testUser));

        UserDetails result = userDetailsService.loadUserByUsernameOrEmail("testuser");

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(userRepository, times(1)).findByUsernameOrEmail("testuser", "testuser");
    }

    @Test
    void testLoadUserByUsernameOrEmail_FoundByEmail() {
        when(userRepository.findByUsernameOrEmail("test@example.com", "test@example.com")).thenReturn(Optional.of(testUser));

        UserDetails result = userDetailsService.loadUserByUsernameOrEmail("test@example.com");

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("test@example.com", ((User) result).getEmail());
        verify(userRepository, times(1)).findByUsernameOrEmail("test@example.com", "test@example.com");
    }

    @Test
    void testLoadUserByUsernameOrEmail_NotFound() {
        when(userRepository.findByUsernameOrEmail("nonexistent", "nonexistent")).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> 
            userDetailsService.loadUserByUsernameOrEmail("nonexistent"));

        assertEquals("User not found: nonexistent", exception.getMessage());
        verify(userRepository, times(1)).findByUsernameOrEmail("nonexistent", "nonexistent");
    }

    @Test
    void testLoadUserByUsernameOrEmail_NullInput() {
        when(userRepository.findByUsernameOrEmail(null, null)).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> 
            userDetailsService.loadUserByUsernameOrEmail(null));

        assertEquals("User not found: null", exception.getMessage());
        verify(userRepository, times(1)).findByUsernameOrEmail(null, null);
    }

    @Test
    void testLoadUserByUsername_AdminUser() {
        User adminUser = new User();
        adminUser.setId(2L);
        adminUser.setUsername("admin");
        adminUser.setEmail("admin@example.com");
        adminUser.setPassword("adminpass");
        adminUser.setRole(User.Role.ADMIN);
        adminUser.setEnabled(true);

        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(adminUser));

        UserDetails result = userDetailsService.loadUserByUsername("admin");

        assertNotNull(result);
        assertEquals("admin", result.getUsername());
        assertTrue(result.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")));
        
        verify(userRepository, times(1)).findByUsername("admin");
    }

    @Test
    void testLoadUserByUsername_DisabledUser() {
        User disabledUser = new User();
        disabledUser.setId(3L);
        disabledUser.setUsername("disabled");
        disabledUser.setEmail("disabled@example.com");
        disabledUser.setPassword("password");
        disabledUser.setRole(User.Role.USER);
        disabledUser.setEnabled(false);

        when(userRepository.findByUsername("disabled")).thenReturn(Optional.of(disabledUser));

        UserDetails result = userDetailsService.loadUserByUsername("disabled");

        assertNotNull(result);
        assertEquals("disabled", result.getUsername());
        assertFalse(result.isEnabled());
        
        verify(userRepository, times(1)).findByUsername("disabled");
    }

    @Test
    void testRepositoryInteraction_VerifyCorrectMethodCalls() {
        // Test that the service properly delegates to repository methods
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(testUser));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(userRepository.findByUsernameOrEmail(anyString(), anyString())).thenReturn(Optional.of(testUser));

        userDetailsService.loadUserByUsername("test");
        userDetailsService.loadUserByEmail("test@example.com");
        userDetailsService.loadUserByUsernameOrEmail("test");

        verify(userRepository, times(1)).findByUsername("test");
        verify(userRepository, times(1)).findByEmail("test@example.com");
        verify(userRepository, times(1)).findByUsernameOrEmail("test", "test");
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void testUserDetailsInterfaceCompliance() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        UserDetails result = userDetailsService.loadUserByUsername("testuser");

        // Verify that returned object implements UserDetails properly
        assertTrue(result instanceof UserDetails);
        assertTrue(result instanceof User);
        
        // Test all UserDetails methods
        assertNotNull(result.getAuthorities());
        assertNotNull(result.getPassword());
        assertNotNull(result.getUsername());
        assertTrue(result.isAccountNonExpired());
        assertTrue(result.isAccountNonLocked());
        assertTrue(result.isCredentialsNonExpired());
        assertTrue(result.isEnabled());
    }
}