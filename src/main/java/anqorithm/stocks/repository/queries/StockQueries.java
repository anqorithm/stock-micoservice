package anqorithm.stocks.repository.queries;

/**
 * Centralized SQL queries for Stock entity operations
 */
public final class StockQueries {
    
    // Table and column constants
    public static final String TABLE_NAME = "stocks";
    public static final String ID_COLUMN = "id";
    public static final String ALL_COLUMNS = "id, symbol, name, current_price, market_cap, sector, industry, " +
                                           "volume, pe_ratio, dividend_yield, fifty_two_week_high, fifty_two_week_low, beta";

    // Basic CRUD queries
    public static final String FIND_BY_ID = 
        "SELECT " + ALL_COLUMNS + " FROM " + TABLE_NAME + " WHERE " + ID_COLUMN + " = ?";
    
    public static final String FIND_ALL = 
        "SELECT " + ALL_COLUMNS + " FROM " + TABLE_NAME;
    
    public static final String COUNT_ALL = 
        "SELECT COUNT(*) FROM " + TABLE_NAME;
    
    public static final String EXISTS_BY_ID = 
        "SELECT COUNT(*) FROM " + TABLE_NAME + " WHERE " + ID_COLUMN + " = ?";
    
    // Symbol-based queries
    public static final String FIND_BY_SYMBOL = 
        "SELECT " + ALL_COLUMNS + " FROM " + TABLE_NAME + " WHERE symbol = ?";
    
    public static final String EXISTS_BY_SYMBOL = 
        "SELECT COUNT(*) FROM " + TABLE_NAME + " WHERE symbol = ?";

    // Price-related queries
    public static final String FIND_BY_PRICE_RANGE = 
        "SELECT " + ALL_COLUMNS + " FROM " + TABLE_NAME + " WHERE current_price BETWEEN ? AND ?";
    
    public static final String COUNT_BY_PRICE_RANGE = 
        "SELECT COUNT(*) FROM " + TABLE_NAME + " WHERE current_price BETWEEN ? AND ?";

    // Market cap queries
    public static final String FIND_BY_MIN_MARKET_CAP = 
        "SELECT " + ALL_COLUMNS + " FROM " + TABLE_NAME + " WHERE market_cap >= ? ORDER BY market_cap DESC";
    
    public static final String COUNT_BY_MIN_MARKET_CAP = 
        "SELECT COUNT(*) FROM " + TABLE_NAME + " WHERE market_cap >= ?";

    // Sector and industry queries
    public static final String FIND_BY_SECTOR = 
        "SELECT " + ALL_COLUMNS + " FROM " + TABLE_NAME + " WHERE sector = ?";
    
    public static final String COUNT_BY_SECTOR = 
        "SELECT COUNT(*) FROM " + TABLE_NAME + " WHERE sector = ?";
    
    public static final String FIND_BY_INDUSTRY = 
        "SELECT " + ALL_COLUMNS + " FROM " + TABLE_NAME + " WHERE industry = ?";
    
    public static final String COUNT_BY_INDUSTRY = 
        "SELECT COUNT(*) FROM " + TABLE_NAME + " WHERE industry = ?";
    
    public static final String FIND_DISTINCT_SECTORS = 
        "SELECT DISTINCT sector FROM " + TABLE_NAME + " WHERE sector IS NOT NULL ORDER BY sector";
    
    public static final String FIND_DISTINCT_INDUSTRIES = 
        "SELECT DISTINCT industry FROM " + TABLE_NAME + " WHERE industry IS NOT NULL ORDER BY industry";

    // Search queries
    public static final String FIND_BY_NAME_CONTAINING = 
        "SELECT " + ALL_COLUMNS + " FROM " + TABLE_NAME + " WHERE UPPER(name) LIKE UPPER(?)";
    
    public static final String COUNT_BY_NAME_CONTAINING = 
        "SELECT COUNT(*) FROM " + TABLE_NAME + " WHERE UPPER(name) LIKE UPPER(?)";
    
    public static final String FIND_BY_SYMBOL_CONTAINING = 
        "SELECT " + ALL_COLUMNS + " FROM " + TABLE_NAME + " WHERE UPPER(symbol) LIKE UPPER(?)";

    // Financial metrics queries
    public static final String FIND_BY_DIVIDEND_YIELD = 
        "SELECT " + ALL_COLUMNS + " FROM " + TABLE_NAME + " WHERE dividend_yield >= ? ORDER BY dividend_yield DESC";
    
    public static final String FIND_BY_PE_RATIO_RANGE = 
        "SELECT " + ALL_COLUMNS + " FROM " + TABLE_NAME + " WHERE pe_ratio BETWEEN ? AND ? ORDER BY pe_ratio ASC";
    
    public static final String FIND_BY_BETA_RANGE = 
        "SELECT " + ALL_COLUMNS + " FROM " + TABLE_NAME + " WHERE beta BETWEEN ? AND ? ORDER BY beta ASC";

    // Sorting and ranking queries
    public static final String FIND_ALL_ORDER_BY_MARKET_CAP_DESC = 
        "SELECT " + ALL_COLUMNS + " FROM " + TABLE_NAME + " ORDER BY market_cap DESC";
    
    public static final String FIND_ALL_ORDER_BY_PRICE_DESC = 
        "SELECT " + ALL_COLUMNS + " FROM " + TABLE_NAME + " ORDER BY current_price DESC";
    
    public static final String FIND_ALL_ORDER_BY_VOLUME_DESC = 
        "SELECT " + ALL_COLUMNS + " FROM " + TABLE_NAME + " ORDER BY volume DESC";

    // 52-week high/low queries
    public static final String FIND_NEAR_52_WEEK_HIGH = 
        "SELECT " + ALL_COLUMNS + " FROM " + TABLE_NAME + " WHERE current_price >= fifty_two_week_high * 0.9";
    
    public static final String FIND_NEAR_52_WEEK_LOW = 
        "SELECT " + ALL_COLUMNS + " FROM " + TABLE_NAME + " WHERE current_price <= fifty_two_week_low * 1.1";

    // Utility method to add pagination to any query
    public static String addPagination(String baseQuery) {
        return baseQuery + " LIMIT ? OFFSET ?";
    }

    // Private constructor to prevent instantiation
    private StockQueries() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}