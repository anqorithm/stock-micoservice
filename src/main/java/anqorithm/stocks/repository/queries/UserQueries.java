package anqorithm.stocks.repository.queries;

/**
 * Centralized SQL queries for User entity operations
 */
public final class UserQueries {
    
    // Table and column constants
    public static final String TABLE_NAME = "users";
    public static final String ID_COLUMN = "id";
    public static final String ALL_COLUMNS = "id, username, email, password, first_name, last_name, role, " +
                                           "enabled, account_non_expired, account_non_locked, credentials_non_expired, " +
                                           "created_at, updated_at";

    // Basic CRUD queries
    public static final String FIND_BY_ID = 
        "SELECT " + ALL_COLUMNS + " FROM " + TABLE_NAME + " WHERE " + ID_COLUMN + " = ?";
    
    public static final String FIND_ALL = 
        "SELECT " + ALL_COLUMNS + " FROM " + TABLE_NAME;
    
    public static final String COUNT_ALL = 
        "SELECT COUNT(*) FROM " + TABLE_NAME;
    
    public static final String EXISTS_BY_ID = 
        "SELECT COUNT(*) FROM " + TABLE_NAME + " WHERE " + ID_COLUMN + " = ?";
    

    // Username-based queries
    public static final String FIND_BY_USERNAME = 
        "SELECT " + ALL_COLUMNS + " FROM " + TABLE_NAME + " WHERE username = ?";
    
    public static final String EXISTS_BY_USERNAME = 
        "SELECT COUNT(*) FROM " + TABLE_NAME + " WHERE username = ?";
    
    public static final String FIND_ACTIVE_USER_BY_USERNAME = 
        "SELECT " + ALL_COLUMNS + " FROM " + TABLE_NAME + " WHERE enabled = true AND username = ?";

    // Email-based queries
    public static final String FIND_BY_EMAIL = 
        "SELECT " + ALL_COLUMNS + " FROM " + TABLE_NAME + " WHERE email = ?";
    
    public static final String EXISTS_BY_EMAIL = 
        "SELECT COUNT(*) FROM " + TABLE_NAME + " WHERE email = ?";

    // Combined queries
    public static final String FIND_BY_USERNAME_OR_EMAIL = 
        "SELECT " + ALL_COLUMNS + " FROM " + TABLE_NAME + " WHERE username = ? OR email = ?";

    // Role-based queries
    public static final String COUNT_BY_ROLE = 
        "SELECT COUNT(*) FROM " + TABLE_NAME + " WHERE role = ?";

    // Status-based queries
    public static final String COUNT_ACTIVE_USERS = 
        "SELECT COUNT(*) FROM " + TABLE_NAME + " WHERE enabled = true";
    
    public static final String FIND_ACTIVE_USERS = 
        "SELECT " + ALL_COLUMNS + " FROM " + TABLE_NAME + " WHERE enabled = true";
    
    public static final String FIND_INACTIVE_USERS = 
        "SELECT " + ALL_COLUMNS + " FROM " + TABLE_NAME + " WHERE enabled = false";

    // Utility method to add pagination to any query
    public static String addPagination(String baseQuery) {
        return baseQuery + " LIMIT ? OFFSET ?";
    }

    // Private constructor to prevent instantiation
    private UserQueries() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}