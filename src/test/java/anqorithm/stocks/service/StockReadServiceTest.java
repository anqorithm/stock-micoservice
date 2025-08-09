package anqorithm.stocks.service;

import anqorithm.stocks.entity.Stock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockReadServiceTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private StockReadService stockReadService;

    private Stock sampleStock;
    private RowMapper<Stock> stockRowMapper;

    @BeforeEach
    void setUp() {
        sampleStock = new Stock();
        sampleStock.setId(1L);
        sampleStock.setSymbol("AAPL");
        sampleStock.setName("Apple Inc.");
        sampleStock.setCurrentPrice(new BigDecimal("150.00"));
        sampleStock.setMarketCap(2500000000000L);
        sampleStock.setSector("Technology");
        sampleStock.setIndustry("Consumer Electronics");
        sampleStock.setDividendYield(new BigDecimal("0.0050"));
        sampleStock.setPeRatio(new BigDecimal("25.50"));
        sampleStock.setEps(new BigDecimal("6.15"));
        sampleStock.setFiftyTwoWeekHigh(new BigDecimal("180.00"));
        sampleStock.setFiftyTwoWeekLow(new BigDecimal("120.00"));
        sampleStock.setVolume(1000000L);
        sampleStock.setAverageVolume(850000L);
        sampleStock.setBeta(new BigDecimal("1.20"));
        sampleStock.setVersion(0L);
    }

    @Test
    void testFindByIdSuccess() {
        when(jdbcTemplate.queryForObject(anyString(), any(RowMapper.class), eq(1L)))
            .thenReturn(sampleStock);

        Optional<Stock> result = stockReadService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals("AAPL", result.get().getSymbol());
        assertEquals("Apple Inc.", result.get().getName());
        verify(jdbcTemplate).queryForObject(anyString(), any(RowMapper.class), eq(1L));
    }

    @Test
    void testFindByIdNotFound() {
        when(jdbcTemplate.queryForObject(anyString(), any(RowMapper.class), eq(999L)))
            .thenThrow(new EmptyResultDataAccessException(1));

        Optional<Stock> result = stockReadService.findById(999L);

        assertFalse(result.isPresent());
        verify(jdbcTemplate).queryForObject(anyString(), any(RowMapper.class), eq(999L));
    }

    @Test
    void testFindBySymbolSuccess() {
        when(jdbcTemplate.queryForObject(anyString(), any(RowMapper.class), eq("AAPL")))
            .thenReturn(sampleStock);

        Optional<Stock> result = stockReadService.findBySymbol("AAPL");

        assertTrue(result.isPresent());
        assertEquals("AAPL", result.get().getSymbol());
        verify(jdbcTemplate).queryForObject(anyString(), any(RowMapper.class), eq("AAPL"));
    }

    @Test
    void testFindBySymbolNotFound() {
        when(jdbcTemplate.queryForObject(anyString(), any(RowMapper.class), eq("NONEXISTENT")))
            .thenThrow(new EmptyResultDataAccessException(1));

        Optional<Stock> result = stockReadService.findBySymbol("NONEXISTENT");

        assertFalse(result.isPresent());
        verify(jdbcTemplate).queryForObject(anyString(), any(RowMapper.class), eq("NONEXISTENT"));
    }

    @Test
    void testFindBySymbolConvertsToUpperCase() {
        when(jdbcTemplate.queryForObject(anyString(), any(RowMapper.class), eq("AAPL")))
            .thenReturn(sampleStock);

        Optional<Stock> result = stockReadService.findBySymbol("aapl");

        assertTrue(result.isPresent());
        verify(jdbcTemplate).queryForObject(anyString(), any(RowMapper.class), eq("AAPL"));
    }

    @Test
    void testFindAll() {
        List<Stock> stocks = Arrays.asList(sampleStock);
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), eq(20), eq(0)))
            .thenReturn(stocks);

        List<Stock> result = stockReadService.findAll(20, 0);

        assertEquals(1, result.size());
        assertEquals("AAPL", result.get(0).getSymbol());
        verify(jdbcTemplate).query(anyString(), any(RowMapper.class), eq(20), eq(0));
    }

    @Test
    void testFindBySector() {
        List<Stock> stocks = Arrays.asList(sampleStock);
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), eq("Technology"), eq(10), eq(0)))
            .thenReturn(stocks);

        List<Stock> result = stockReadService.findBySector("Technology", 10, 0);

        assertEquals(1, result.size());
        assertEquals("Technology", result.get(0).getSector());
        verify(jdbcTemplate).query(anyString(), any(RowMapper.class), eq("Technology"), eq(10), eq(0));
    }

    @Test
    void testFindByIndustry() {
        List<Stock> stocks = Arrays.asList(sampleStock);
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), eq("Consumer Electronics"), eq(15), eq(5)))
            .thenReturn(stocks);

        List<Stock> result = stockReadService.findByIndustry("Consumer Electronics", 15, 5);

        assertEquals(1, result.size());
        assertEquals("Consumer Electronics", result.get(0).getIndustry());
        verify(jdbcTemplate).query(anyString(), any(RowMapper.class), eq("Consumer Electronics"), eq(15), eq(5));
    }

    @Test
    void testFindByPriceRange() {
        List<Stock> stocks = Arrays.asList(sampleStock);
        BigDecimal minPrice = new BigDecimal("100.00");
        BigDecimal maxPrice = new BigDecimal("200.00");
        
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), eq(minPrice), eq(maxPrice), eq(25), eq(10)))
            .thenReturn(stocks);

        List<Stock> result = stockReadService.findByPriceRange(minPrice, maxPrice, 25, 10);

        assertEquals(1, result.size());
        verify(jdbcTemplate).query(anyString(), any(RowMapper.class), eq(minPrice), eq(maxPrice), eq(25), eq(10));
    }

    @Test
    void testFindByMarketCapRange() {
        List<Stock> stocks = Arrays.asList(sampleStock);
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), eq(1000000000000L), eq(3000000000000L), eq(10), eq(0)))
            .thenReturn(stocks);

        List<Stock> result = stockReadService.findByMarketCapRange(1000000000000L, 3000000000000L, 10, 0);

        assertEquals(1, result.size());
        verify(jdbcTemplate).query(anyString(), any(RowMapper.class), eq(1000000000000L), eq(3000000000000L), eq(10), eq(0));
    }

    @Test
    void testFindTopByMarketCap() {
        List<Stock> stocks = Arrays.asList(sampleStock);
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), eq(5)))
            .thenReturn(stocks);

        List<Stock> result = stockReadService.findTopByMarketCap(5);

        assertEquals(1, result.size());
        verify(jdbcTemplate).query(anyString(), any(RowMapper.class), eq(5));
    }

    @Test
    void testFindTopByVolume() {
        List<Stock> stocks = Arrays.asList(sampleStock);
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), eq(10)))
            .thenReturn(stocks);

        List<Stock> result = stockReadService.findTopByVolume(10);

        assertEquals(1, result.size());
        verify(jdbcTemplate).query(anyString(), any(RowMapper.class), eq(10));
    }

    @Test
    void testFindTopByDividendYield() {
        List<Stock> stocks = Arrays.asList(sampleStock);
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), eq(8)))
            .thenReturn(stocks);

        List<Stock> result = stockReadService.findTopByDividendYield(8);

        assertEquals(1, result.size());
        verify(jdbcTemplate).query(anyString(), any(RowMapper.class), eq(8));
    }

    @Test
    void testFindByNameSearch() {
        List<Stock> stocks = Arrays.asList(sampleStock);
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), eq("%Apple%"), eq(20), eq(0)))
            .thenReturn(stocks);

        List<Stock> result = stockReadService.findByNameSearch("Apple", 20, 0);

        assertEquals(1, result.size());
        verify(jdbcTemplate).query(anyString(), any(RowMapper.class), eq("%Apple%"), eq(20), eq(0));
    }

    @Test
    void testFindBySymbolSearch() {
        List<Stock> stocks = Arrays.asList(sampleStock);
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), eq("%AA%"), eq(15), eq(5)))
            .thenReturn(stocks);

        List<Stock> result = stockReadService.findBySymbolSearch("AA", 15, 5);

        assertEquals(1, result.size());
        verify(jdbcTemplate).query(anyString(), any(RowMapper.class), eq("%AA%"), eq(15), eq(5));
    }

    @Test
    void testFindDistinctSectors() {
        List<String> sectors = Arrays.asList("Technology", "Finance");
        when(jdbcTemplate.queryForList(anyString(), eq(String.class)))
            .thenReturn(sectors);

        List<String> result = stockReadService.findDistinctSectors();

        assertEquals(2, result.size());
        assertTrue(result.contains("Technology"));
        assertTrue(result.contains("Finance"));
        verify(jdbcTemplate).queryForList(anyString(), eq(String.class));
    }

    @Test
    void testFindDistinctIndustries() {
        List<String> industries = Arrays.asList("Consumer Electronics", "Banking");
        when(jdbcTemplate.queryForList(anyString(), eq(String.class)))
            .thenReturn(industries);

        List<String> result = stockReadService.findDistinctIndustries();

        assertEquals(2, result.size());
        assertTrue(result.contains("Consumer Electronics"));
        assertTrue(result.contains("Banking"));
        verify(jdbcTemplate).queryForList(anyString(), eq(String.class));
    }

    @Test
    void testCountTotal() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Long.class)))
            .thenReturn(100L);

        Long result = stockReadService.countTotal();

        assertEquals(100L, result);
        verify(jdbcTemplate).queryForObject(anyString(), eq(Long.class));
    }

    @Test
    void testCountBySector() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Long.class), eq("Technology")))
            .thenReturn(25L);

        Long result = stockReadService.countBySector("Technology");

        assertEquals(25L, result);
        verify(jdbcTemplate).queryForObject(anyString(), eq(Long.class), eq("Technology"));
    }

    @Test
    void testCountByIndustry() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Long.class), eq("Banking")))
            .thenReturn(15L);

        Long result = stockReadService.countByIndustry("Banking");

        assertEquals(15L, result);
        verify(jdbcTemplate).queryForObject(anyString(), eq(Long.class), eq("Banking"));
    }

    @Test
    void testFindHighPerformers() {
        List<Stock> stocks = Arrays.asList(sampleStock);
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), eq(10)))
            .thenReturn(stocks);

        List<Stock> result = stockReadService.findHighPerformers(10);

        assertEquals(1, result.size());
        verify(jdbcTemplate).query(anyString(), any(RowMapper.class), eq(10));
    }

    @Test
    void testFindLowPerformers() {
        List<Stock> stocks = Arrays.asList(sampleStock);
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), eq(5)))
            .thenReturn(stocks);

        List<Stock> result = stockReadService.findLowPerformers(5);

        assertEquals(1, result.size());
        verify(jdbcTemplate).query(anyString(), any(RowMapper.class), eq(5));
    }

    @Test
    void testFindValueStocks() {
        List<Stock> stocks = Arrays.asList(sampleStock);
        BigDecimal maxPeRatio = new BigDecimal("15.0");
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), eq(maxPeRatio), eq(10)))
            .thenReturn(stocks);

        List<Stock> result = stockReadService.findValueStocks(maxPeRatio, 10);

        assertEquals(1, result.size());
        verify(jdbcTemplate).query(anyString(), any(RowMapper.class), eq(maxPeRatio), eq(10));
    }

    @Test
    void testFindDividendStocks() {
        List<Stock> stocks = Arrays.asList(sampleStock);
        BigDecimal minDividendYield = new BigDecimal("0.02");
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), eq(minDividendYield), eq(15)))
            .thenReturn(stocks);

        List<Stock> result = stockReadService.findDividendStocks(minDividendYield, 15);

        assertEquals(1, result.size());
        verify(jdbcTemplate).query(anyString(), any(RowMapper.class), eq(minDividendYield), eq(15));
    }

    @Test
    void testFindHighBetaStocks() {
        List<Stock> stocks = Arrays.asList(sampleStock);
        BigDecimal minBeta = new BigDecimal("1.5");
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), eq(minBeta), eq(8)))
            .thenReturn(stocks);

        List<Stock> result = stockReadService.findHighBetaStocks(minBeta, 8);

        assertEquals(1, result.size());
        verify(jdbcTemplate).query(anyString(), any(RowMapper.class), eq(minBeta), eq(8));
    }

    @Test
    void testFindLowBetaStocks() {
        List<Stock> stocks = Arrays.asList(sampleStock);
        BigDecimal maxBeta = new BigDecimal("0.8");
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), eq(maxBeta), eq(12)))
            .thenReturn(stocks);

        List<Stock> result = stockReadService.findLowBetaStocks(maxBeta, 12);

        assertEquals(1, result.size());
        verify(jdbcTemplate).query(anyString(), any(RowMapper.class), eq(maxBeta), eq(12));
    }

    @Test
    void testGetAverageMarketCap() {
        BigDecimal avgMarketCap = new BigDecimal("2000000000000");
        when(jdbcTemplate.queryForObject(anyString(), eq(BigDecimal.class)))
            .thenReturn(avgMarketCap);

        BigDecimal result = stockReadService.getAverageMarketCap();

        assertEquals(avgMarketCap, result);
        verify(jdbcTemplate).queryForObject(anyString(), eq(BigDecimal.class));
    }

    @Test
    void testGetAveragePrice() {
        BigDecimal avgPrice = new BigDecimal("125.50");
        when(jdbcTemplate.queryForObject(anyString(), eq(BigDecimal.class)))
            .thenReturn(avgPrice);

        BigDecimal result = stockReadService.getAveragePrice();

        assertEquals(avgPrice, result);
        verify(jdbcTemplate).queryForObject(anyString(), eq(BigDecimal.class));
    }

    @Test
    void testGetAveragePeRatio() {
        BigDecimal avgPeRatio = new BigDecimal("22.75");
        when(jdbcTemplate.queryForObject(anyString(), eq(BigDecimal.class)))
            .thenReturn(avgPeRatio);

        BigDecimal result = stockReadService.getAveragePeRatio();

        assertEquals(avgPeRatio, result);
        verify(jdbcTemplate).queryForObject(anyString(), eq(BigDecimal.class));
    }

    @Test
    void testStockRowMapperMapRow() throws SQLException {
        ResultSet rs = mock(ResultSet.class);
        
        // Mock all the ResultSet methods
        when(rs.getLong("id")).thenReturn(1L);
        when(rs.getString("symbol")).thenReturn("AAPL");
        when(rs.getString("name")).thenReturn("Apple Inc.");
        when(rs.getBigDecimal("current_price")).thenReturn(new BigDecimal("150.00"));
        when(rs.getLong("market_cap")).thenReturn(2500000000000L);
        when(rs.wasNull()).thenReturn(false);
        when(rs.getString("sector")).thenReturn("Technology");
        when(rs.getString("industry")).thenReturn("Consumer Electronics");
        when(rs.getBigDecimal("dividend_yield")).thenReturn(new BigDecimal("0.0050"));
        when(rs.getBigDecimal("pe_ratio")).thenReturn(new BigDecimal("25.50"));
        when(rs.getBigDecimal("eps")).thenReturn(new BigDecimal("6.15"));
        when(rs.getBigDecimal("fifty_two_week_high")).thenReturn(new BigDecimal("180.00"));
        when(rs.getBigDecimal("fifty_two_week_low")).thenReturn(new BigDecimal("120.00"));
        when(rs.getLong("volume")).thenReturn(1000000L);
        when(rs.getLong("average_volume")).thenReturn(850000L);
        when(rs.getBigDecimal("beta")).thenReturn(new BigDecimal("1.20"));
        when(rs.getTimestamp("created_at")).thenReturn(new Timestamp(System.currentTimeMillis()));
        when(rs.getTimestamp("updated_at")).thenReturn(new Timestamp(System.currentTimeMillis()));
        when(rs.getLong("version")).thenReturn(0L);

        // Use reflection to access the private StockRowMapper
        StockReadService.StockRowMapper mapper = new StockReadService.StockRowMapper();
        Stock stock = mapper.mapRow(rs, 1);

        assertNotNull(stock);
        assertEquals(1L, stock.getId());
        assertEquals("AAPL", stock.getSymbol());
        assertEquals("Apple Inc.", stock.getName());
        assertEquals(new BigDecimal("150.00"), stock.getCurrentPrice());
        assertEquals(2500000000000L, stock.getMarketCap());
        assertEquals("Technology", stock.getSector());
        assertEquals("Consumer Electronics", stock.getIndustry());
        assertEquals(new BigDecimal("0.0050"), stock.getDividendYield());
        assertEquals(new BigDecimal("25.50"), stock.getPeRatio());
        assertEquals(new BigDecimal("6.15"), stock.getEps());
        assertEquals(new BigDecimal("180.00"), stock.getFiftyTwoWeekHigh());
        assertEquals(new BigDecimal("120.00"), stock.getFiftyTwoWeekLow());
        assertEquals(1000000L, stock.getVolume());
        assertEquals(850000L, stock.getAverageVolume());
        assertEquals(new BigDecimal("1.20"), stock.getBeta());
        assertEquals(0L, stock.getVersion());
    }

    @Test
    void testStockRowMapperWithNullValues() throws SQLException {
        ResultSet rs = mock(ResultSet.class);
        
        // Mock required fields
        when(rs.getLong("id")).thenReturn(1L);
        when(rs.getString("symbol")).thenReturn("TEST");
        when(rs.getString("name")).thenReturn("Test Company");
        when(rs.getBigDecimal("current_price")).thenReturn(new BigDecimal("50.00"));
        
        // Mock nullable fields as null
        when(rs.getLong("market_cap")).thenReturn(0L);
        when(rs.getString("sector")).thenReturn(null);
        when(rs.getString("industry")).thenReturn(null);
        when(rs.getBigDecimal("dividend_yield")).thenReturn(null);
        when(rs.getBigDecimal("pe_ratio")).thenReturn(null);
        when(rs.getBigDecimal("eps")).thenReturn(null);
        when(rs.getBigDecimal("fifty_two_week_high")).thenReturn(null);
        when(rs.getBigDecimal("fifty_two_week_low")).thenReturn(null);
        when(rs.getLong("volume")).thenReturn(0L);
        when(rs.getLong("average_volume")).thenReturn(0L);
        when(rs.getBigDecimal("beta")).thenReturn(null);
        when(rs.getTimestamp("created_at")).thenReturn(null);
        when(rs.getTimestamp("updated_at")).thenReturn(null);
        when(rs.getLong("version")).thenReturn(0L);
        
        // Set wasNull() to return true for the fields that should be null
        when(rs.wasNull()).thenReturn(true);

        StockReadService.StockRowMapper mapper = new StockReadService.StockRowMapper();
        Stock stock = mapper.mapRow(rs, 1);

        assertNotNull(stock);
        assertEquals(1L, stock.getId());
        assertEquals("TEST", stock.getSymbol());
        assertEquals("Test Company", stock.getName());
        assertEquals(new BigDecimal("50.00"), stock.getCurrentPrice());
        assertNull(stock.getMarketCap());
        assertNull(stock.getSector());
        assertNull(stock.getIndustry());
        assertNull(stock.getDividendYield());
        assertNull(stock.getPeRatio());
        assertNull(stock.getEps());
        assertNull(stock.getFiftyTwoWeekHigh());
        assertNull(stock.getFiftyTwoWeekLow());
        assertEquals(0L, stock.getVolume());
        assertEquals(0L, stock.getAverageVolume());
        assertNull(stock.getBeta());
        assertNull(stock.getVersion());
    }

    @Test
    void testConstructor() {
        // Test that the service is properly instantiated with JdbcTemplate
        StockReadService service = new StockReadService(jdbcTemplate);
        assertNotNull(service);
    }
}