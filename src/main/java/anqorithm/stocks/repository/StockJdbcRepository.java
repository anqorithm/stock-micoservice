package anqorithm.stocks.repository;

import anqorithm.stocks.entity.Stock;
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

@Repository
public class StockJdbcRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final RowMapper<Stock> STOCK_ROW_MAPPER = new RowMapper<Stock>() {
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
    };

    public List<Stock> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        String sql = "SELECT * FROM stocks WHERE current_price BETWEEN ? AND ?";
        return jdbcTemplate.query(sql, STOCK_ROW_MAPPER, minPrice, maxPrice);
    }

    public Page<Stock> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        String sql = "SELECT * FROM stocks WHERE current_price BETWEEN ? AND ? LIMIT ? OFFSET ?";
        String countSql = "SELECT COUNT(*) FROM stocks WHERE current_price BETWEEN ? AND ?";
        
        List<Stock> stocks = jdbcTemplate.query(sql, STOCK_ROW_MAPPER, 
                minPrice, maxPrice, pageable.getPageSize(), pageable.getOffset());
        
        Long total = jdbcTemplate.queryForObject(countSql, Long.class, minPrice, maxPrice);
        
        return new PageImpl<>(stocks, pageable, total != null ? total : 0);
    }

    public List<Stock> findByMinimumMarketCap(Long minMarketCap) {
        String sql = "SELECT * FROM stocks WHERE market_cap >= ? ORDER BY market_cap DESC";
        return jdbcTemplate.query(sql, STOCK_ROW_MAPPER, minMarketCap);
    }

    public Page<Stock> findByMinimumMarketCap(Long minMarketCap, Pageable pageable) {
        String sql = "SELECT * FROM stocks WHERE market_cap >= ? ORDER BY market_cap DESC LIMIT ? OFFSET ?";
        String countSql = "SELECT COUNT(*) FROM stocks WHERE market_cap >= ?";
        
        List<Stock> stocks = jdbcTemplate.query(sql, STOCK_ROW_MAPPER, 
                minMarketCap, pageable.getPageSize(), pageable.getOffset());
        
        Long total = jdbcTemplate.queryForObject(countSql, Long.class, minMarketCap);
        
        return new PageImpl<>(stocks, pageable, total != null ? total : 0);
    }

    public List<Stock> findByMinimumDividendYield(BigDecimal minDividendYield) {
        String sql = "SELECT * FROM stocks WHERE dividend_yield >= ? ORDER BY dividend_yield DESC";
        return jdbcTemplate.query(sql, STOCK_ROW_MAPPER, minDividendYield);
    }

    public List<Stock> findByPeRatioRange(BigDecimal minPE, BigDecimal maxPE) {
        String sql = "SELECT * FROM stocks WHERE pe_ratio BETWEEN ? AND ? ORDER BY pe_ratio ASC";
        return jdbcTemplate.query(sql, STOCK_ROW_MAPPER, minPE, maxPE);
    }

    public List<Stock> findByNameContainingIgnoreCase(String name) {
        String sql = "SELECT * FROM stocks WHERE UPPER(name) LIKE UPPER(?)";
        return jdbcTemplate.query(sql, STOCK_ROW_MAPPER, "%" + name + "%");
    }

    public Page<Stock> findByNameContainingIgnoreCase(String name, Pageable pageable) {
        String sql = "SELECT * FROM stocks WHERE UPPER(name) LIKE UPPER(?) LIMIT ? OFFSET ?";
        String countSql = "SELECT COUNT(*) FROM stocks WHERE UPPER(name) LIKE UPPER(?)";
        
        String namePattern = "%" + name + "%";
        List<Stock> stocks = jdbcTemplate.query(sql, STOCK_ROW_MAPPER, 
                namePattern, pageable.getPageSize(), pageable.getOffset());
        
        Long total = jdbcTemplate.queryForObject(countSql, Long.class, namePattern);
        
        return new PageImpl<>(stocks, pageable, total != null ? total : 0);
    }

    public List<Stock> findBySymbolContainingIgnoreCase(String symbol) {
        String sql = "SELECT * FROM stocks WHERE UPPER(symbol) LIKE UPPER(?)";
        return jdbcTemplate.query(sql, STOCK_ROW_MAPPER, "%" + symbol + "%");
    }

    public List<String> findDistinctSectors() {
        String sql = "SELECT DISTINCT sector FROM stocks WHERE sector IS NOT NULL ORDER BY sector";
        return jdbcTemplate.queryForList(sql, String.class);
    }

    public List<String> findDistinctIndustries() {
        String sql = "SELECT DISTINCT industry FROM stocks WHERE industry IS NOT NULL ORDER BY industry";
        return jdbcTemplate.queryForList(sql, String.class);
    }

    public Long countBySector(String sector) {
        String sql = "SELECT COUNT(*) FROM stocks WHERE sector = ?";
        return jdbcTemplate.queryForObject(sql, Long.class, sector);
    }

    public Page<Stock> findAllOrderByMarketCapDesc(Pageable pageable) {
        String sql = "SELECT * FROM stocks ORDER BY market_cap DESC LIMIT ? OFFSET ?";
        String countSql = "SELECT COUNT(*) FROM stocks";
        
        List<Stock> stocks = jdbcTemplate.query(sql, STOCK_ROW_MAPPER, 
                pageable.getPageSize(), pageable.getOffset());
        
        Long total = jdbcTemplate.queryForObject(countSql, Long.class);
        
        return new PageImpl<>(stocks, pageable, total != null ? total : 0);
    }

    public Page<Stock> findAllOrderByCurrentPriceDesc(Pageable pageable) {
        String sql = "SELECT * FROM stocks ORDER BY current_price DESC LIMIT ? OFFSET ?";
        String countSql = "SELECT COUNT(*) FROM stocks";
        
        List<Stock> stocks = jdbcTemplate.query(sql, STOCK_ROW_MAPPER, 
                pageable.getPageSize(), pageable.getOffset());
        
        Long total = jdbcTemplate.queryForObject(countSql, Long.class);
        
        return new PageImpl<>(stocks, pageable, total != null ? total : 0);
    }

    public Page<Stock> findAllOrderByVolumeDesc(Pageable pageable) {
        String sql = "SELECT * FROM stocks ORDER BY volume DESC LIMIT ? OFFSET ?";
        String countSql = "SELECT COUNT(*) FROM stocks";
        
        List<Stock> stocks = jdbcTemplate.query(sql, STOCK_ROW_MAPPER, 
                pageable.getPageSize(), pageable.getOffset());
        
        Long total = jdbcTemplate.queryForObject(countSql, Long.class);
        
        return new PageImpl<>(stocks, pageable, total != null ? total : 0);
    }

    public int updateCurrentPrice(Long id, BigDecimal newPrice) {
        String sql = "UPDATE stocks SET current_price = ? WHERE id = ?";
        return jdbcTemplate.update(sql, newPrice, id);
    }

    public int updateVolume(Long id, Long volume) {
        String sql = "UPDATE stocks SET volume = ? WHERE id = ?";
        return jdbcTemplate.update(sql, volume, id);
    }

    public int updatePriceAndVolumeBySymbol(String symbol, BigDecimal price, Long volume) {
        String sql = "UPDATE stocks SET current_price = ?, volume = ? WHERE symbol = ?";
        return jdbcTemplate.update(sql, price, volume, symbol);
    }

    public int deleteBySymbol(String symbol) {
        String sql = "DELETE FROM stocks WHERE symbol = ?";
        return jdbcTemplate.update(sql, symbol);
    }

    public Page<Stock> findBySectorWithCustomCount(String sector, Pageable pageable) {
        String sql = "SELECT * FROM stocks WHERE sector = ? LIMIT ? OFFSET ?";
        String countSql = "SELECT COUNT(*) FROM stocks WHERE sector = ?";
        
        List<Stock> stocks = jdbcTemplate.query(sql, STOCK_ROW_MAPPER, 
                sector, pageable.getPageSize(), pageable.getOffset());
        
        Long total = jdbcTemplate.queryForObject(countSql, Long.class, sector);
        
        return new PageImpl<>(stocks, pageable, total != null ? total : 0);
    }

    public List<Stock> findByBetaRange(BigDecimal minBeta, BigDecimal maxBeta) {
        String sql = "SELECT * FROM stocks WHERE beta BETWEEN ? AND ? ORDER BY beta ASC";
        return jdbcTemplate.query(sql, STOCK_ROW_MAPPER, minBeta, maxBeta);
    }

    public List<Stock> findNearFiftyTwoWeekHigh() {
        String sql = "SELECT * FROM stocks WHERE current_price >= fifty_two_week_high * 0.9";
        return jdbcTemplate.query(sql, STOCK_ROW_MAPPER);
    }

    public List<Stock> findNearFiftyTwoWeekLow() {
        String sql = "SELECT * FROM stocks WHERE current_price <= fifty_two_week_low * 1.1";
        return jdbcTemplate.query(sql, STOCK_ROW_MAPPER);
    }

    public Optional<Stock> findById(Long id) {
        String sql = "SELECT * FROM stocks WHERE id = ?";
        List<Stock> stocks = jdbcTemplate.query(sql, STOCK_ROW_MAPPER, id);
        return stocks.isEmpty() ? Optional.empty() : Optional.of(stocks.get(0));
    }

    public Optional<Stock> findBySymbol(String symbol) {
        String sql = "SELECT * FROM stocks WHERE symbol = ?";
        List<Stock> stocks = jdbcTemplate.query(sql, STOCK_ROW_MAPPER, symbol);
        return stocks.isEmpty() ? Optional.empty() : Optional.of(stocks.get(0));
    }

    public List<Stock> findAll() {
        String sql = "SELECT * FROM stocks";
        return jdbcTemplate.query(sql, STOCK_ROW_MAPPER);
    }

    public Page<Stock> findAll(Pageable pageable) {
        String sql = "SELECT * FROM stocks LIMIT ? OFFSET ?";
        String countSql = "SELECT COUNT(*) FROM stocks";
        
        List<Stock> stocks = jdbcTemplate.query(sql, STOCK_ROW_MAPPER, 
                pageable.getPageSize(), pageable.getOffset());
        
        Long total = jdbcTemplate.queryForObject(countSql, Long.class);
        
        return new PageImpl<>(stocks, pageable, total != null ? total : 0);
    }

    public List<Stock> findBySector(String sector) {
        String sql = "SELECT * FROM stocks WHERE sector = ?";
        return jdbcTemplate.query(sql, STOCK_ROW_MAPPER, sector);
    }

    public Page<Stock> findBySector(String sector, Pageable pageable) {
        String sql = "SELECT * FROM stocks WHERE sector = ? LIMIT ? OFFSET ?";
        String countSql = "SELECT COUNT(*) FROM stocks WHERE sector = ?";
        
        List<Stock> stocks = jdbcTemplate.query(sql, STOCK_ROW_MAPPER, 
                sector, pageable.getPageSize(), pageable.getOffset());
        
        Long total = jdbcTemplate.queryForObject(countSql, Long.class, sector);
        
        return new PageImpl<>(stocks, pageable, total != null ? total : 0);
    }

    public List<Stock> findByIndustry(String industry) {
        String sql = "SELECT * FROM stocks WHERE industry = ?";
        return jdbcTemplate.query(sql, STOCK_ROW_MAPPER, industry);
    }

    public Page<Stock> findByIndustry(String industry, Pageable pageable) {
        String sql = "SELECT * FROM stocks WHERE industry = ? LIMIT ? OFFSET ?";
        String countSql = "SELECT COUNT(*) FROM stocks WHERE industry = ?";
        
        List<Stock> stocks = jdbcTemplate.query(sql, STOCK_ROW_MAPPER, 
                industry, pageable.getPageSize(), pageable.getOffset());
        
        Long total = jdbcTemplate.queryForObject(countSql, Long.class, industry);
        
        return new PageImpl<>(stocks, pageable, total != null ? total : 0);
    }

    public boolean existsBySymbol(String symbol) {
        String sql = "SELECT COUNT(*) FROM stocks WHERE symbol = ?";
        Long count = jdbcTemplate.queryForObject(sql, Long.class, symbol);
        return count != null && count > 0;
    }
}