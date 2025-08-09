package anqorithm.stocks.repository.jdbc;

import anqorithm.stocks.entity.Stock;
import anqorithm.stocks.repository.queries.StockQueries;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * JDBC Repository for Stock entity - READ OPERATIONS ONLY
 * All write operations (Create, Update, Delete) should use JPA repositories
 */
@Repository
public class StockJdbcRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final RowMapper<Stock> STOCK_ROW_MAPPER = new StockRowMapper();

    // Basic read operations
    public Optional<Stock> findById(Long id) {
        List<Stock> results = jdbcTemplate.query(StockQueries.FIND_BY_ID, STOCK_ROW_MAPPER, id);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public List<Stock> findAll() {
        return jdbcTemplate.query(StockQueries.FIND_ALL, STOCK_ROW_MAPPER);
    }

    public Page<Stock> findAll(Pageable pageable) {
        String sql = StockQueries.addPagination(StockQueries.FIND_ALL);
        List<Stock> results = jdbcTemplate.query(sql, STOCK_ROW_MAPPER, 
                pageable.getPageSize(), pageable.getOffset());
        
        Long total = jdbcTemplate.queryForObject(StockQueries.COUNT_ALL, Long.class);
        return new PageImpl<>(results, pageable, total != null ? total : 0);
    }

    public boolean existsById(Long id) {
        Long count = jdbcTemplate.queryForObject(StockQueries.EXISTS_BY_ID, Long.class, id);
        return count != null && count > 0;
    }

    public long count() {
        Long count = jdbcTemplate.queryForObject(StockQueries.COUNT_ALL, Long.class);
        return count != null ? count : 0;
    }

    // Symbol-based queries
    public Optional<Stock> findBySymbol(String symbol) {
        List<Stock> results = jdbcTemplate.query(StockQueries.FIND_BY_SYMBOL, STOCK_ROW_MAPPER, symbol);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public boolean existsBySymbol(String symbol) {
        Long count = jdbcTemplate.queryForObject(StockQueries.EXISTS_BY_SYMBOL, Long.class, symbol);
        return count != null && count > 0;
    }

    // Price-related queries
    public List<Stock> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return jdbcTemplate.query(StockQueries.FIND_BY_PRICE_RANGE, STOCK_ROW_MAPPER, minPrice, maxPrice);
    }

    public Page<Stock> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        String sql = StockQueries.addPagination(StockQueries.FIND_BY_PRICE_RANGE);
        List<Stock> results = jdbcTemplate.query(sql, STOCK_ROW_MAPPER, 
                minPrice, maxPrice, pageable.getPageSize(), pageable.getOffset());
        
        Long total = jdbcTemplate.queryForObject(StockQueries.COUNT_BY_PRICE_RANGE, Long.class, minPrice, maxPrice);
        return new PageImpl<>(results, pageable, total != null ? total : 0);
    }

    // Market cap queries
    public List<Stock> findByMinimumMarketCap(Long minMarketCap) {
        return jdbcTemplate.query(StockQueries.FIND_BY_MIN_MARKET_CAP, STOCK_ROW_MAPPER, minMarketCap);
    }

    public Page<Stock> findByMinimumMarketCap(Long minMarketCap, Pageable pageable) {
        String sql = StockQueries.addPagination(StockQueries.FIND_BY_MIN_MARKET_CAP);
        List<Stock> results = jdbcTemplate.query(sql, STOCK_ROW_MAPPER, 
                minMarketCap, pageable.getPageSize(), pageable.getOffset());
        
        Long total = jdbcTemplate.queryForObject(StockQueries.COUNT_BY_MIN_MARKET_CAP, Long.class, minMarketCap);
        return new PageImpl<>(results, pageable, total != null ? total : 0);
    }

    // Sector and industry queries
    public List<Stock> findBySector(String sector) {
        return jdbcTemplate.query(StockQueries.FIND_BY_SECTOR, STOCK_ROW_MAPPER, sector);
    }

    public Page<Stock> findBySector(String sector, Pageable pageable) {
        String sql = StockQueries.addPagination(StockQueries.FIND_BY_SECTOR);
        List<Stock> results = jdbcTemplate.query(sql, STOCK_ROW_MAPPER, 
                sector, pageable.getPageSize(), pageable.getOffset());
        
        Long total = jdbcTemplate.queryForObject(StockQueries.COUNT_BY_SECTOR, Long.class, sector);
        return new PageImpl<>(results, pageable, total != null ? total : 0);
    }

    public List<Stock> findByIndustry(String industry) {
        return jdbcTemplate.query(StockQueries.FIND_BY_INDUSTRY, STOCK_ROW_MAPPER, industry);
    }

    public Page<Stock> findByIndustry(String industry, Pageable pageable) {
        String sql = StockQueries.addPagination(StockQueries.FIND_BY_INDUSTRY);
        List<Stock> results = jdbcTemplate.query(sql, STOCK_ROW_MAPPER, 
                industry, pageable.getPageSize(), pageable.getOffset());
        
        Long total = jdbcTemplate.queryForObject(StockQueries.COUNT_BY_INDUSTRY, Long.class, industry);
        return new PageImpl<>(results, pageable, total != null ? total : 0);
    }

    public Long countBySector(String sector) {
        return jdbcTemplate.queryForObject(StockQueries.COUNT_BY_SECTOR, Long.class, sector);
    }

    public List<String> findDistinctSectors() {
        return jdbcTemplate.queryForList(StockQueries.FIND_DISTINCT_SECTORS, String.class);
    }

    public List<String> findDistinctIndustries() {
        return jdbcTemplate.queryForList(StockQueries.FIND_DISTINCT_INDUSTRIES, String.class);
    }

    // Search queries
    public List<Stock> findByNameContainingIgnoreCase(String name) {
        return jdbcTemplate.query(StockQueries.FIND_BY_NAME_CONTAINING, STOCK_ROW_MAPPER, "%" + name + "%");
    }

    public Page<Stock> findByNameContainingIgnoreCase(String name, Pageable pageable) {
        String sql = StockQueries.addPagination(StockQueries.FIND_BY_NAME_CONTAINING);
        String namePattern = "%" + name + "%";
        List<Stock> results = jdbcTemplate.query(sql, STOCK_ROW_MAPPER, 
                namePattern, pageable.getPageSize(), pageable.getOffset());
        
        Long total = jdbcTemplate.queryForObject(StockQueries.COUNT_BY_NAME_CONTAINING, Long.class, namePattern);
        return new PageImpl<>(results, pageable, total != null ? total : 0);
    }

    public List<Stock> findBySymbolContainingIgnoreCase(String symbol) {
        return jdbcTemplate.query(StockQueries.FIND_BY_SYMBOL_CONTAINING, STOCK_ROW_MAPPER, "%" + symbol + "%");
    }

    // Financial metrics queries
    public List<Stock> findByMinimumDividendYield(BigDecimal minDividendYield) {
        return jdbcTemplate.query(StockQueries.FIND_BY_DIVIDEND_YIELD, STOCK_ROW_MAPPER, minDividendYield);
    }

    public List<Stock> findByPeRatioRange(BigDecimal minPE, BigDecimal maxPE) {
        return jdbcTemplate.query(StockQueries.FIND_BY_PE_RATIO_RANGE, STOCK_ROW_MAPPER, minPE, maxPE);
    }

    public List<Stock> findByBetaRange(BigDecimal minBeta, BigDecimal maxBeta) {
        return jdbcTemplate.query(StockQueries.FIND_BY_BETA_RANGE, STOCK_ROW_MAPPER, minBeta, maxBeta);
    }

    // Sorting and ranking queries
    public Page<Stock> findAllOrderByMarketCapDesc(Pageable pageable) {
        String sql = StockQueries.addPagination(StockQueries.FIND_ALL_ORDER_BY_MARKET_CAP_DESC);
        List<Stock> results = jdbcTemplate.query(sql, STOCK_ROW_MAPPER, 
                pageable.getPageSize(), pageable.getOffset());
        
        Long total = jdbcTemplate.queryForObject(StockQueries.COUNT_ALL, Long.class);
        return new PageImpl<>(results, pageable, total != null ? total : 0);
    }

    public Page<Stock> findAllOrderByCurrentPriceDesc(Pageable pageable) {
        String sql = StockQueries.addPagination(StockQueries.FIND_ALL_ORDER_BY_PRICE_DESC);
        List<Stock> results = jdbcTemplate.query(sql, STOCK_ROW_MAPPER, 
                pageable.getPageSize(), pageable.getOffset());
        
        Long total = jdbcTemplate.queryForObject(StockQueries.COUNT_ALL, Long.class);
        return new PageImpl<>(results, pageable, total != null ? total : 0);
    }

    public Page<Stock> findAllOrderByVolumeDesc(Pageable pageable) {
        String sql = StockQueries.addPagination(StockQueries.FIND_ALL_ORDER_BY_VOLUME_DESC);
        List<Stock> results = jdbcTemplate.query(sql, STOCK_ROW_MAPPER, 
                pageable.getPageSize(), pageable.getOffset());
        
        Long total = jdbcTemplate.queryForObject(StockQueries.COUNT_ALL, Long.class);
        return new PageImpl<>(results, pageable, total != null ? total : 0);
    }

    // 52-week high/low queries
    public List<Stock> findNearFiftyTwoWeekHigh() {
        return jdbcTemplate.query(StockQueries.FIND_NEAR_52_WEEK_HIGH, STOCK_ROW_MAPPER);
    }

    public List<Stock> findNearFiftyTwoWeekLow() {
        return jdbcTemplate.query(StockQueries.FIND_NEAR_52_WEEK_LOW, STOCK_ROW_MAPPER);
    }

    // Custom paginated query support
    public Page<Stock> findBySectorWithCustomCount(String sector, Pageable pageable) {
        return findBySector(sector, pageable); // Reuse existing method
    }

    // Inner class for row mapping
    private static class StockRowMapper implements RowMapper<Stock> {
        @Override
        public Stock mapRow(ResultSet rs, int rowNum) throws SQLException {
            Stock stock = new Stock();
            stock.setId(rs.getLong("id"));
            stock.setSymbol(rs.getString("symbol"));
            stock.setName(rs.getString("name"));
            stock.setCurrentPrice(rs.getBigDecimal("current_price"));
            stock.setMarketCap(rs.getLong("market_cap"));
            stock.setSector(rs.getString("sector"));
            stock.setIndustry(rs.getString("industry"));
            stock.setVolume(rs.getLong("volume"));
            stock.setPeRatio(rs.getBigDecimal("pe_ratio"));
            stock.setDividendYield(rs.getBigDecimal("dividend_yield"));
            stock.setFiftyTwoWeekHigh(rs.getBigDecimal("fifty_two_week_high"));
            stock.setFiftyTwoWeekLow(rs.getBigDecimal("fifty_two_week_low"));
            stock.setBeta(rs.getBigDecimal("beta"));
            return stock;
        }
    }
}