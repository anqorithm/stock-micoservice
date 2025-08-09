package anqorithm.stocks.repository;

import anqorithm.stocks.repository.jdbc.StockJdbcRepository;

import anqorithm.stocks.entity.Stock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockJdbcRepositoryTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private StockJdbcRepository stockJdbcRepository;

    private Stock sampleStock;

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
        sampleStock.setVolume(1000000L);
        sampleStock.setBeta(new BigDecimal("1.20"));
        sampleStock.setDividendYield(new BigDecimal("0.005"));
        sampleStock.setPeRatio(new BigDecimal("25.50"));
    }

    @Test
    void testExistsBySymbol_ReturnsTrue() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Long.class), eq("AAPL")))
                .thenReturn(1L);

        boolean result = stockJdbcRepository.existsBySymbol("AAPL");

        assertTrue(result);
        verify(jdbcTemplate).queryForObject(anyString(), eq(Long.class), eq("AAPL"));
    }

    @Test
    void testExistsBySymbol_ReturnsFalse() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Long.class), eq("NONEXISTENT")))
                .thenReturn(0L);

        boolean result = stockJdbcRepository.existsBySymbol("NONEXISTENT");

        assertFalse(result);
        verify(jdbcTemplate).queryForObject(anyString(), eq(Long.class), eq("NONEXISTENT"));
    }

    @Test
    void testFindByPriceRange() {
        List<Stock> expectedStocks = Arrays.asList(sampleStock);
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), any(BigDecimal.class), any(BigDecimal.class)))
                .thenReturn(expectedStocks);

        List<Stock> result = stockJdbcRepository.findByPriceRange(new BigDecimal("100"), new BigDecimal("200"));

        assertEquals(1, result.size());
        assertEquals("AAPL", result.get(0).getSymbol());
        verify(jdbcTemplate).query(anyString(), any(RowMapper.class), any(BigDecimal.class), any(BigDecimal.class));
    }

    @Test
    void testFindByNameContainingIgnoreCase() {
        List<Stock> expectedStocks = Arrays.asList(sampleStock);
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), eq("%apple%")))
                .thenReturn(expectedStocks);

        List<Stock> result = stockJdbcRepository.findByNameContainingIgnoreCase("apple");

        assertEquals(1, result.size());
        assertEquals("AAPL", result.get(0).getSymbol());
        verify(jdbcTemplate).query(anyString(), any(RowMapper.class), eq("%apple%"));
    }

    @Test
    void testFindDistinctSectors() {
        List<String> expectedSectors = Arrays.asList("Technology", "Healthcare");
        when(jdbcTemplate.queryForList(anyString(), eq(String.class)))
                .thenReturn(expectedSectors);

        List<String> result = stockJdbcRepository.findDistinctSectors();

        assertEquals(2, result.size());
        assertTrue(result.contains("Technology"));
        assertTrue(result.contains("Healthcare"));
        verify(jdbcTemplate).queryForList(anyString(), eq(String.class));
    }

    @Test
    void testCountBySector() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Long.class), eq("Technology")))
                .thenReturn(5L);

        Long result = stockJdbcRepository.countBySector("Technology");

        assertEquals(5L, result);
        verify(jdbcTemplate).queryForObject(anyString(), eq(Long.class), eq("Technology"));
    }

    // Note: Update and delete operations are handled by JPA repository
    // JDBC repository is only for SELECT operations

    @Test
    void testFindById_Found() {
        List<Stock> expectedStocks = Arrays.asList(sampleStock);
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), eq(1L)))
                .thenReturn(expectedStocks);

        var result = stockJdbcRepository.findById(1L);

        assertTrue(result.isPresent());
        assertEquals("AAPL", result.get().getSymbol());
        verify(jdbcTemplate).query(anyString(), any(RowMapper.class), eq(1L));
    }

    @Test
    void testFindById_NotFound() {
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), eq(999L)))
                .thenReturn(Collections.emptyList());

        var result = stockJdbcRepository.findById(999L);

        assertFalse(result.isPresent());
        verify(jdbcTemplate).query(anyString(), any(RowMapper.class), eq(999L));
    }

    @Test
    void testFindBySymbol_Found() {
        List<Stock> expectedStocks = Arrays.asList(sampleStock);
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), eq("AAPL")))
                .thenReturn(expectedStocks);

        var result = stockJdbcRepository.findBySymbol("AAPL");

        assertTrue(result.isPresent());
        assertEquals("AAPL", result.get().getSymbol());
        verify(jdbcTemplate).query(anyString(), any(RowMapper.class), eq("AAPL"));
    }

    @Test
    void testFindBySymbol_NotFound() {
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), eq("NONEXISTENT")))
                .thenReturn(Collections.emptyList());

        var result = stockJdbcRepository.findBySymbol("NONEXISTENT");

        assertFalse(result.isPresent());
        verify(jdbcTemplate).query(anyString(), any(RowMapper.class), eq("NONEXISTENT"));
    }

    @Test
    void testFindBySector() {
        List<Stock> expectedStocks = Arrays.asList(sampleStock);
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), eq("Technology")))
                .thenReturn(expectedStocks);

        List<Stock> result = stockJdbcRepository.findBySector("Technology");

        assertEquals(1, result.size());
        assertEquals("AAPL", result.get(0).getSymbol());
        verify(jdbcTemplate).query(anyString(), any(RowMapper.class), eq("Technology"));
    }
}