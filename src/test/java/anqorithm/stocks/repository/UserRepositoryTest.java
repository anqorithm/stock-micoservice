package anqorithm.stocks.repository;

import anqorithm.stocks.repository.jpa.UserRepository;
import anqorithm.stocks.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User user1;
    private User user2;
    private User adminUser;

    @BeforeEach
    void setUp() {
        user1 = new User();
        user1.setUsername("user1");
        user1.setEmail("user1@example.com");
        user1.setPassword("password123");
        user1.setFirstName("User");
        user1.setLastName("One");
        user1.setRole(User.Role.USER);

        user2 = new User();
        user2.setUsername("user2");
        user2.setEmail("user2@example.com");
        user2.setPassword("password123");
        user2.setFirstName("User");
        user2.setLastName("Two");
        user2.setRole(User.Role.USER);
        user2.setEnabled(false);

        adminUser = new User();
        adminUser.setUsername("admin");
        adminUser.setEmail("admin@example.com");
        adminUser.setPassword("password123");
        adminUser.setFirstName("Admin");
        adminUser.setLastName("User");
        adminUser.setRole(User.Role.ADMIN);

        entityManager.persistAndFlush(user1);
        entityManager.persistAndFlush(user2);
        entityManager.persistAndFlush(adminUser);
    }

    @Test
    void testFindByUsername() {
        Optional<User> found = userRepository.findByUsername("user1");
        
        assertTrue(found.isPresent());
        assertEquals("user1", found.get().getUsername());
        assertEquals("user1@example.com", found.get().getEmail());
    }

    @Test
    void testFindByUsernameNotFound() {
        Optional<User> found = userRepository.findByUsername("nonexistent");
        
        assertFalse(found.isPresent());
    }

    @Test
    void testFindByEmail() {
        Optional<User> found = userRepository.findByEmail("user1@example.com");
        
        assertTrue(found.isPresent());
        assertEquals("user1", found.get().getUsername());
        assertEquals("user1@example.com", found.get().getEmail());
    }

    @Test
    void testFindByEmailNotFound() {
        Optional<User> found = userRepository.findByEmail("nonexistent@example.com");
        
        assertFalse(found.isPresent());
    }

    @Test
    void testFindByUsernameOrEmail() {
        Optional<User> foundByUsername = userRepository.findByUsernameOrEmail("user1", "wrong@email.com");
        assertTrue(foundByUsername.isPresent());
        assertEquals("user1", foundByUsername.get().getUsername());
        
        Optional<User> foundByEmail = userRepository.findByUsernameOrEmail("wrongusername", "user1@example.com");
        assertTrue(foundByEmail.isPresent());
        assertEquals("user1", foundByEmail.get().getUsername());
        
        Optional<User> notFound = userRepository.findByUsernameOrEmail("wrong", "wrong@email.com");
        assertFalse(notFound.isPresent());
    }

    @Test
    void testExistsByUsername() {
        assertTrue(userRepository.existsByUsername("user1"));
        assertFalse(userRepository.existsByUsername("nonexistent"));
    }

    @Test
    void testExistsByEmail() {
        assertTrue(userRepository.existsByEmail("user1@example.com"));
        assertFalse(userRepository.existsByEmail("nonexistent@example.com"));
    }

    // Note: Methods findActiveUserByUsername, countByRole, and countActiveUsers
    // have been moved to UserJdbcRepository for read operations only

    @Test
    void testSaveUser() {
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setEmail("newuser@example.com");
        newUser.setPassword("password123");
        newUser.setRole(User.Role.USER);
        
        User saved = userRepository.save(newUser);
        
        assertNotNull(saved.getId());
        assertEquals("newuser", saved.getUsername());
        assertNotNull(saved.getCreatedAt());
        assertNotNull(saved.getUpdatedAt());
    }

    @Test
    void testUpdateUser() {
        User user = userRepository.findByUsername("user1").orElseThrow();
        user.setFirstName("Updated");
        
        User updated = userRepository.save(user);
        
        assertEquals("Updated", updated.getFirstName());
        assertNotNull(updated.getUpdatedAt());
    }

    @Test
    void testDeleteUser() {
        User user = userRepository.findByUsername("user1").orElseThrow();
        Long userId = user.getId();
        
        userRepository.delete(user);
        
        Optional<User> deleted = userRepository.findById(userId);
        assertFalse(deleted.isPresent());
    }

    @Test
    void testUserConstraints() {
        User duplicateUsername = new User();
        duplicateUsername.setUsername("user1"); // duplicate username
        duplicateUsername.setEmail("unique@example.com");
        duplicateUsername.setPassword("password123");
        
        assertThrows(Exception.class, () -> {
            userRepository.saveAndFlush(duplicateUsername);
        });
        
        User duplicateEmail = new User();
        duplicateEmail.setUsername("uniqueuser");
        duplicateEmail.setEmail("user1@example.com"); // duplicate email
        duplicateEmail.setPassword("password123");
        
        assertThrows(Exception.class, () -> {
            userRepository.saveAndFlush(duplicateEmail);
        });
    }

    @Test
    void testUserDefaults() {
        User newUser = new User();
        newUser.setUsername("defaultuser");
        newUser.setEmail("default@example.com");
        newUser.setPassword("password123");
        
        User saved = userRepository.save(newUser);
        
        assertEquals(User.Role.USER, saved.getRole());
        assertTrue(saved.isEnabled());
        assertTrue(saved.isAccountNonExpired());
        assertTrue(saved.isAccountNonLocked());
        assertTrue(saved.isCredentialsNonExpired());
    }
}