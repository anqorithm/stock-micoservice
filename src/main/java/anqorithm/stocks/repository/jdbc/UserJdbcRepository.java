package anqorithm.stocks.repository.jdbc;

import anqorithm.stocks.entity.User;
import anqorithm.stocks.repository.queries.UserQueries;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * JDBC Repository for User entity - READ OPERATIONS ONLY
 * All write operations (Create, Update, Delete) should use JPA repositories
 */
@Repository
public class UserJdbcRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final RowMapper<User> USER_ROW_MAPPER = new UserRowMapper();

    // Basic read operations
    public Optional<User> findById(Long id) {
        List<User> users = jdbcTemplate.query(UserQueries.FIND_BY_ID, USER_ROW_MAPPER, id);
        return users.isEmpty() ? Optional.empty() : Optional.of(users.get(0));
    }

    public List<User> findAll() {
        return jdbcTemplate.query(UserQueries.FIND_ALL, USER_ROW_MAPPER);
    }

    public Page<User> findAll(Pageable pageable) {
        String sql = UserQueries.addPagination(UserQueries.FIND_ALL);
        List<User> users = jdbcTemplate.query(sql, USER_ROW_MAPPER, 
                pageable.getPageSize(), pageable.getOffset());
        
        Long total = jdbcTemplate.queryForObject(UserQueries.COUNT_ALL, Long.class);
        return new PageImpl<>(users, pageable, total != null ? total : 0);
    }

    public boolean existsById(Long id) {
        Long count = jdbcTemplate.queryForObject(UserQueries.EXISTS_BY_ID, Long.class, id);
        return count != null && count > 0;
    }

    public long count() {
        Long count = jdbcTemplate.queryForObject(UserQueries.COUNT_ALL, Long.class);
        return count != null ? count : 0;
    }

    // Username-based queries
    public Optional<User> findByUsername(String username) {
        List<User> users = jdbcTemplate.query(UserQueries.FIND_BY_USERNAME, USER_ROW_MAPPER, username);
        return users.isEmpty() ? Optional.empty() : Optional.of(users.get(0));
    }

    public boolean existsByUsername(String username) {
        Long count = jdbcTemplate.queryForObject(UserQueries.EXISTS_BY_USERNAME, Long.class, username);
        return count != null && count > 0;
    }

    public Optional<User> findActiveUserByUsername(String username) {
        List<User> users = jdbcTemplate.query(UserQueries.FIND_ACTIVE_USER_BY_USERNAME, USER_ROW_MAPPER, username);
        return users.isEmpty() ? Optional.empty() : Optional.of(users.get(0));
    }

    // Email-based queries
    public Optional<User> findByEmail(String email) {
        List<User> users = jdbcTemplate.query(UserQueries.FIND_BY_EMAIL, USER_ROW_MAPPER, email);
        return users.isEmpty() ? Optional.empty() : Optional.of(users.get(0));
    }

    public boolean existsByEmail(String email) {
        Long count = jdbcTemplate.queryForObject(UserQueries.EXISTS_BY_EMAIL, Long.class, email);
        return count != null && count > 0;
    }

    // Combined queries
    public Optional<User> findByUsernameOrEmail(String username, String email) {
        List<User> users = jdbcTemplate.query(UserQueries.FIND_BY_USERNAME_OR_EMAIL, USER_ROW_MAPPER, username, email);
        return users.isEmpty() ? Optional.empty() : Optional.of(users.get(0));
    }

    // Role-based queries
    public long countByRole(User.Role role) {
        Long count = jdbcTemplate.queryForObject(UserQueries.COUNT_BY_ROLE, Long.class, role.name());
        return count != null ? count : 0;
    }

    // Status-based queries
    public long countActiveUsers() {
        Long count = jdbcTemplate.queryForObject(UserQueries.COUNT_ACTIVE_USERS, Long.class);
        return count != null ? count : 0;
    }

    public List<User> findActiveUsers() {
        return jdbcTemplate.query(UserQueries.FIND_ACTIVE_USERS, USER_ROW_MAPPER);
    }

    public Page<User> findActiveUsers(Pageable pageable) {
        String sql = UserQueries.addPagination(UserQueries.FIND_ACTIVE_USERS);
        List<User> users = jdbcTemplate.query(sql, USER_ROW_MAPPER, 
                pageable.getPageSize(), pageable.getOffset());
        
        Long total = jdbcTemplate.queryForObject(UserQueries.COUNT_ACTIVE_USERS, Long.class);
        return new PageImpl<>(users, pageable, total != null ? total : 0);
    }

    public List<User> findInactiveUsers() {
        return jdbcTemplate.query(UserQueries.FIND_INACTIVE_USERS, USER_ROW_MAPPER);
    }

    public Page<User> findInactiveUsers(Pageable pageable) {
        String sql = UserQueries.addPagination(UserQueries.FIND_INACTIVE_USERS);
        List<User> users = jdbcTemplate.query(sql, USER_ROW_MAPPER, 
                pageable.getPageSize(), pageable.getOffset());
        
        Long total = count() - countActiveUsers(); // Total - Active = Inactive
        return new PageImpl<>(users, pageable, total);
    }

    // Inner class for row mapping
    private static class UserRowMapper implements RowMapper<User> {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
            user.setId(rs.getLong("id"));
            user.setUsername(rs.getString("username"));
            user.setEmail(rs.getString("email"));
            user.setPassword(rs.getString("password"));
            user.setFirstName(rs.getString("first_name"));
            user.setLastName(rs.getString("last_name"));
            user.setRole(User.Role.valueOf(rs.getString("role")));
            user.setEnabled(rs.getBoolean("enabled"));
            user.setAccountNonExpired(rs.getBoolean("account_non_expired"));
            user.setAccountNonLocked(rs.getBoolean("account_non_locked"));
            user.setCredentialsNonExpired(rs.getBoolean("credentials_non_expired"));
            
            // Note: createdAt and updatedAt are managed by Hibernate annotations
            // and don't have setter methods, so we skip them in JDBC mapping
            
            return user;
        }
    }
}