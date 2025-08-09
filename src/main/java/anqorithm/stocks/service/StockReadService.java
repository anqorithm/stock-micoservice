package anqorithm.stocks.service;

import anqorithm.stocks.entity.Stock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class StockReadService {

    private final JdbcTemplate jdbcTemplate;
    private final StockRowMapper stockRowMapper;

    @Autowired
    public StockReadService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.stockRowMapper = new StockRowMapper();
    }

    public Optional<Stock> findById(Long id) {
        String sql = "SELECT * FROM stocks WHERE id = ?";
        try {
            Stock stock = jdbcTemplate.queryForObject(sql, stockRowMapper, id);
            return Optional.ofNullable(stock);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<Stock> findBySymbol(String symbol) {
        String sql = "SELECT * FROM stocks WHERE symbol = ?";
        try {
            Stock stock = jdbcTemplate.queryForObject(sql, stockRowMapper, symbol.toUpperCase());
            return Optional.ofNullable(stock);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<Stock> findAll(int limit, int offset) {
        String sql = "SELECT * FROM stocks ORDER BY id LIMIT ? OFFSET ?";
        return jdbcTemplate.query(sql, stockRowMapper, limit, offset);
    }

    public List<Stock> findBySector(String sector, int limit, int offset) {
        String sql = "SELECT * FROM stocks WHERE sector = ? ORDER BY market_cap DESC LIMIT ? OFFSET ?";
        return jdbcTemplate.query(sql, stockRowMapper, sector, limit, offset);
    }

    public List<Stock> findByIndustry(String industry, int limit, int offset) {
        String sql = "SELECT * FROM stocks WHERE industry = ? ORDER BY market_cap DESC LIMIT ? OFFSET ?";
        return jdbcTemplate.query(sql, stockRowMapper, industry, limit, offset);
    }

    public List<Stock> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, int limit, int offset) {
        String sql = "SELECT * FROM stocks WHERE current_price BETWEEN ? AND ? ORDER BY current_price ASC LIMIT ? OFFSET ?";
        return jdbcTemplate.query(sql, stockRowMapper, minPrice, maxPrice, limit, offset);
    }

    public List<Stock> findByMarketCapRange(Long minMarketCap, Long maxMarketCap, int limit, int offset) {
        String sql = "SELECT * FROM stocks WHERE market_cap BETWEEN ? AND ? ORDER BY market_cap DESC LIMIT ? OFFSET ?";
        return jdbcTemplate.query(sql, stockRowMapper, minMarketCap, maxMarketCap, limit, offset);
    }

    public List<Stock> findTopByMarketCap(int limit) {
        String sql = "SELECT * FROM stocks WHERE market_cap IS NOT NULL ORDER BY market_cap DESC LIMIT ?";
        return jdbcTemplate.query(sql, stockRowMapper, limit);
    }

    public List<Stock> findTopByVolume(int limit) {
        String sql = "SELECT * FROM stocks WHERE volume IS NOT NULL ORDER BY volume DESC LIMIT ?";
        return jdbcTemplate.query(sql, stockRowMapper, limit);
    }

    public List<Stock> findTopByDividendYield(int limit) {
        String sql = "SELECT * FROM stocks WHERE dividend_yield IS NOT NULL ORDER BY dividend_yield DESC LIMIT ?";
        return jdbcTemplate.query(sql, stockRowMapper, limit);
    }

    public List<Stock> findByNameSearch(String searchTerm, int limit, int offset) {
        String sql = "SELECT * FROM stocks WHERE UPPER(name) LIKE UPPER(?) ORDER BY name LIMIT ? OFFSET ?";
        String searchPattern = "%" + searchTerm + "%";
        return jdbcTemplate.query(sql, stockRowMapper, searchPattern, limit, offset);
    }

    public List<Stock> findBySymbolSearch(String searchTerm, int limit, int offset) {
        String sql = "SELECT * FROM stocks WHERE UPPER(symbol) LIKE UPPER(?) ORDER BY symbol LIMIT ? OFFSET ?";
        String searchPattern = "%" + searchTerm + "%";
        return jdbcTemplate.query(sql, stockRowMapper, searchPattern, limit, offset);
    }

    public List<String> findDistinctSectors() {
        String sql = "SELECT DISTINCT sector FROM stocks WHERE sector IS NOT NULL ORDER BY sector";
        return jdbcTemplate.queryForList(sql, String.class);
    }

    public List<String> findDistinctIndustries() {
        String sql = "SELECT DISTINCT industry FROM stocks WHERE industry IS NOT NULL ORDER BY industry";
        return jdbcTemplate.queryForList(sql, String.class);
    }

    public Long countTotal() {
        String sql = "SELECT COUNT(*) FROM stocks";
        return jdbcTemplate.queryForObject(sql, Long.class);
    }

    public Long countBySector(String sector) {
        String sql = "SELECT COUNT(*) FROM stocks WHERE sector = ?";
        return jdbcTemplate.queryForObject(sql, Long.class, sector);
    }

    public Long countByIndustry(String industry) {
        String sql = "SELECT COUNT(*) FROM stocks WHERE industry = ?";
        return jdbcTemplate.queryForObject(sql, Long.class, industry);
    }

    public List<Stock> findHighPerformers(int limit) {
        String sql = """
            SELECT * FROM stocks 
            WHERE current_price IS NOT NULL 
            AND fifty_two_week_low IS NOT NULL 
            AND current_price > fifty_two_week_low * 1.5 
            ORDER BY (current_price / fifty_two_week_low) DESC 
            LIMIT ?
        """;
        return jdbcTemplate.query(sql, stockRowMapper, limit);
    }

    public List<Stock> findLowPerformers(int limit) {
        String sql = """
            SELECT * FROM stocks 
            WHERE current_price IS NOT NULL 
            AND fifty_two_week_high IS NOT NULL 
            AND current_price < fifty_two_week_high * 0.7 
            ORDER BY (current_price / fifty_two_week_high) ASC 
            LIMIT ?
        """;
        return jdbcTemplate.query(sql, stockRowMapper, limit);
    }

    public List<Stock> findValueStocks(BigDecimal maxPeRatio, int limit) {
        String sql = """
            SELECT * FROM stocks 
            WHERE pe_ratio IS NOT NULL 
            AND pe_ratio <= ? 
            AND pe_ratio > 0 
            ORDER BY pe_ratio ASC 
            LIMIT ?
        """;
        return jdbcTemplate.query(sql, stockRowMapper, maxPeRatio, limit);
    }

    public List<Stock> findDividendStocks(BigDecimal minDividendYield, int limit) {
        String sql = """
            SELECT * FROM stocks 
            WHERE dividend_yield IS NOT NULL 
            AND dividend_yield >= ? 
            ORDER BY dividend_yield DESC 
            LIMIT ?
        """;
        return jdbcTemplate.query(sql, stockRowMapper, minDividendYield, limit);
    }

    public List<Stock> findHighBetaStocks(BigDecimal minBeta, int limit) {
        String sql = """
            SELECT * FROM stocks 
            WHERE beta IS NOT NULL 
            AND beta >= ? 
            ORDER BY beta DESC 
            LIMIT ?
        """;
        return jdbcTemplate.query(sql, stockRowMapper, minBeta, limit);
    }

    public List<Stock> findLowBetaStocks(BigDecimal maxBeta, int limit) {
        String sql = """
            SELECT * FROM stocks 
            WHERE beta IS NOT NULL 
            AND beta <= ? 
            AND beta > 0 
            ORDER BY beta ASC 
            LIMIT ?
        """;
        return jdbcTemplate.query(sql, stockRowMapper, maxBeta, limit);
    }

    public BigDecimal getAverageMarketCap() {
        String sql = "SELECT AVG(market_cap) FROM stocks WHERE market_cap IS NOT NULL";
        return jdbcTemplate.queryForObject(sql, BigDecimal.class);
    }

    public BigDecimal getAveragePrice() {
        String sql = "SELECT AVG(current_price) FROM stocks WHERE current_price IS NOT NULL";
        return jdbcTemplate.queryForObject(sql, BigDecimal.class);
    }

    public BigDecimal getAveragePeRatio() {
        String sql = "SELECT AVG(pe_ratio) FROM stocks WHERE pe_ratio IS NOT NULL";
        return jdbcTemplate.queryForObject(sql, BigDecimal.class);
    }

    static class StockRowMapper implements RowMapper<Stock> {
        @Override
        public Stock mapRow(ResultSet rs, int rowNum) throws SQLException {
            Stock stock = new Stock();
            stock.setId(rs.getLong("id"));
            stock.setSymbol(rs.getString("symbol"));
            stock.setName(rs.getString("name"));
            stock.setCurrentPrice(rs.getBigDecimal("current_price"));
            
            Long marketCap = rs.getLong("market_cap");
            if (!rs.wasNull()) {
                stock.setMarketCap(marketCap);
            }
            
            stock.setSector(rs.getString("sector"));
            stock.setIndustry(rs.getString("industry"));
            
            BigDecimal dividendYield = rs.getBigDecimal("dividend_yield");
            if (!rs.wasNull()) {
                stock.setDividendYield(dividendYield);
            }
            
            BigDecimal peRatio = rs.getBigDecimal("pe_ratio");
            if (!rs.wasNull()) {
                stock.setPeRatio(peRatio);
            }
            
            BigDecimal eps = rs.getBigDecimal("eps");
            if (!rs.wasNull()) {
                stock.setEps(eps);
            }
            
            BigDecimal fiftyTwoWeekHigh = rs.getBigDecimal("fifty_two_week_high");
            if (!rs.wasNull()) {
                stock.setFiftyTwoWeekHigh(fiftyTwoWeekHigh);
            }
            
            BigDecimal fiftyTwoWeekLow = rs.getBigDecimal("fifty_two_week_low");
            if (!rs.wasNull()) {
                stock.setFiftyTwoWeekLow(fiftyTwoWeekLow);
            }
            
            Long volume = rs.getLong("volume");
            if (!rs.wasNull()) {
                stock.setVolume(volume);
            }
            
            Long averageVolume = rs.getLong("average_volume");
            if (!rs.wasNull()) {
                stock.setAverageVolume(averageVolume);
            }
            
            BigDecimal beta = rs.getBigDecimal("beta");
            if (!rs.wasNull()) {
                stock.setBeta(beta);
            }
            
            // Handle timestamps
            java.sql.Timestamp createdAtTs = rs.getTimestamp("created_at");
            if (createdAtTs != null) {
                stock.setVersion(0L); // Set via reflection or use a constructor that allows it
            }
            
            java.sql.Timestamp updatedAtTs = rs.getTimestamp("updated_at");
            if (updatedAtTs != null) {
                stock.setVersion(0L); // Set via reflection or use a constructor that allows it
            }
            
            Long version = rs.getLong("version");
            if (!rs.wasNull()) {
                stock.setVersion(version);
            }
            
            return stock;
        }
    }
}