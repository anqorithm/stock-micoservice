package anqorithm.stocks.service;

import anqorithm.stocks.entity.Stock;
import anqorithm.stocks.repository.StockRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockServiceTest {

    @Mock
    private StockRepository stockRepository;

    @Mock
    private StockReadService stockReadService;

    @InjectMocks
    private StockService stockService;

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
        when(stockReadService.findById(1L)).thenReturn(Optional.of(sampleStock));

        Optional<Stock> result = stockService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals("AAPL", result.get().getSymbol());
        verify(stockReadService).findById(1L);
    }

    @Test
    void testFindByIdNotFound() {
        when(stockReadService.findById(999L)).thenReturn(Optional.empty());

        Optional<Stock> result = stockService.findById(999L);

        assertFalse(result.isPresent());
        verify(stockReadService).findById(999L);
    }

    @Test
    void testFindBySymbolSuccess() {
        when(stockReadService.findBySymbol("AAPL")).thenReturn(Optional.of(sampleStock));

        Optional<Stock> result = stockService.findBySymbol("AAPL");

        assertTrue(result.isPresent());
        assertEquals("AAPL", result.get().getSymbol());
        verify(stockReadService).findBySymbol("AAPL");
    }

    @Test
    void testFindBySymbolNotFound() {
        when(stockReadService.findBySymbol("NONEXISTENT")).thenReturn(Optional.empty());

        Optional<Stock> result = stockService.findBySymbol("NONEXISTENT");

        assertFalse(result.isPresent());
        verify(stockReadService).findBySymbol("NONEXISTENT");
    }

    @Test
    void testFindAll() {
        List<Stock> stocks = Arrays.asList(sampleStock);
        when(stockReadService.findAll(20, 0)).thenReturn(stocks);

        List<Stock> result = stockService.findAll(0, 20);

        assertEquals(1, result.size());
        assertEquals("AAPL", result.get(0).getSymbol());
        verify(stockReadService).findAll(20, 0);
    }

    @Test
    void testFindAllPaged() {
        List<Stock> stocks = Arrays.asList(sampleStock);
        Page<Stock> page = new PageImpl<>(stocks);
        
        when(stockRepository.findAll(any(PageRequest.class))).thenReturn(page);

        Page<Stock> result = stockService.findAllPaged(0, 20, "id", "asc");

        assertEquals(1, result.getContent().size());
        assertEquals("AAPL", result.getContent().get(0).getSymbol());
        verify(stockRepository).findAll(any(PageRequest.class));
    }

    @Test
    void testFindAllPagedDescending() {
        List<Stock> stocks = Arrays.asList(sampleStock);
        Page<Stock> page = new PageImpl<>(stocks);
        
        when(stockRepository.findAll(any(PageRequest.class))).thenReturn(page);

        Page<Stock> result = stockService.findAllPaged(0, 20, "name", "desc");

        assertEquals(1, result.getContent().size());
        verify(stockRepository).findAll(
            PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "name")));
    }

    @Test
    void testCreateSuccess() {
        Stock newStock = new Stock();
        newStock.setSymbol("googl");
        newStock.setName("Alphabet Inc.");
        newStock.setCurrentPrice(new BigDecimal("130.00"));

        Stock expectedStock = new Stock();
        expectedStock.setId(2L);
        expectedStock.setSymbol("GOOGL");
        expectedStock.setName("Alphabet Inc.");
        expectedStock.setCurrentPrice(new BigDecimal("130.00"));

        when(stockRepository.existsBySymbol("GOOGL")).thenReturn(false);
        when(stockRepository.save(any(Stock.class))).thenReturn(expectedStock);

        Stock result = stockService.create(newStock);

        assertEquals(2L, result.getId());
        assertEquals("GOOGL", result.getSymbol());
        verify(stockRepository).existsBySymbol("GOOGL");
        verify(stockRepository).save(argThat(stock -> "GOOGL".equals(stock.getSymbol())));
    }

    @Test
    void testCreateWithExistingSymbol() {
        Stock newStock = new Stock();
        newStock.setSymbol("AAPL");
        newStock.setName("Apple Inc.");
        newStock.setCurrentPrice(new BigDecimal("150.00"));

        when(stockRepository.existsBySymbol("AAPL")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class, 
            () -> stockService.create(newStock)
        );

        assertEquals("Stock with symbol AAPL already exists", exception.getMessage());
        verify(stockRepository).existsBySymbol("AAPL");
        verify(stockRepository, never()).save(any(Stock.class));
    }

    @Test
    void testCreateWithNullSymbol() {
        Stock newStock = new Stock();
        newStock.setSymbol(null);
        newStock.setName("Test Company");
        newStock.setCurrentPrice(new BigDecimal("50.00"));

        when(stockRepository.existsBySymbol(null)).thenReturn(false);
        when(stockRepository.save(any(Stock.class))).thenReturn(newStock);

        Stock result = stockService.create(newStock);

        assertNull(result.getSymbol());
        verify(stockRepository).save(newStock);
    }

    @Test
    void testUpdateSuccess() {
        Stock existingStock = new Stock();
        existingStock.setId(1L);
        existingStock.setSymbol("AAPL");
        existingStock.setName("Apple Inc.");
        existingStock.setCurrentPrice(new BigDecimal("150.00"));

        Stock updateData = new Stock();
        updateData.setName("Apple Inc. Updated");
        updateData.setCurrentPrice(new BigDecimal("155.00"));
        updateData.setMarketCap(2600000000000L);
        updateData.setSector("Technology Updated");
        updateData.setIndustry("Consumer Electronics Updated");
        updateData.setDividendYield(new BigDecimal("0.0055"));
        updateData.setPeRatio(new BigDecimal("26.00"));
        updateData.setEps(new BigDecimal("6.20"));
        updateData.setFiftyTwoWeekHigh(new BigDecimal("185.00"));
        updateData.setFiftyTwoWeekLow(new BigDecimal("125.00"));
        updateData.setVolume(1100000L);
        updateData.setAverageVolume(900000L);
        updateData.setBeta(new BigDecimal("1.25"));

        when(stockRepository.findById(1L)).thenReturn(Optional.of(existingStock));
        when(stockRepository.save(any(Stock.class))).thenReturn(existingStock);

        Stock result = stockService.update(1L, updateData);

        assertEquals("Apple Inc. Updated", existingStock.getName());
        assertEquals(new BigDecimal("155.00"), existingStock.getCurrentPrice());
        assertEquals(2600000000000L, existingStock.getMarketCap());
        assertEquals("Technology Updated", existingStock.getSector());
        verify(stockRepository).findById(1L);
        verify(stockRepository).save(existingStock);
    }

    @Test
    void testUpdateWithSymbolChange() {
        Stock existingStock = new Stock();
        existingStock.setId(1L);
        existingStock.setSymbol("AAPL");
        existingStock.setName("Apple Inc.");

        Stock updateData = new Stock();
        updateData.setSymbol("AAPL2");

        when(stockRepository.findById(1L)).thenReturn(Optional.of(existingStock));
        when(stockRepository.existsBySymbol("AAPL2")).thenReturn(false);
        when(stockRepository.save(any(Stock.class))).thenReturn(existingStock);

        Stock result = stockService.update(1L, updateData);

        assertEquals("AAPL2", existingStock.getSymbol());
        verify(stockRepository).existsBySymbol("AAPL2");
        verify(stockRepository).save(existingStock);
    }

    @Test
    void testUpdateWithConflictingSymbol() {
        Stock existingStock = new Stock();
        existingStock.setId(1L);
        existingStock.setSymbol("AAPL");

        Stock updateData = new Stock();
        updateData.setSymbol("GOOGL");

        when(stockRepository.findById(1L)).thenReturn(Optional.of(existingStock));
        when(stockRepository.existsBySymbol("GOOGL")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> stockService.update(1L, updateData)
        );

        assertEquals("Stock with symbol GOOGL already exists", exception.getMessage());
        verify(stockRepository).findById(1L);
        verify(stockRepository).existsBySymbol("GOOGL");
        verify(stockRepository, never()).save(any(Stock.class));
    }

    @Test
    void testUpdateNotFound() {
        Stock updateData = new Stock();
        updateData.setName("Updated Name");

        when(stockRepository.findById(999L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> stockService.update(999L, updateData)
        );

        assertEquals("Stock not found with id: 999", exception.getMessage());
        verify(stockRepository).findById(999L);
        verify(stockRepository, never()).save(any(Stock.class));
    }

    @Test
    void testUpdateWithNullFields() {
        Stock existingStock = new Stock();
        existingStock.setId(1L);
        existingStock.setSymbol("AAPL");
        existingStock.setName("Original Name");
        existingStock.setCurrentPrice(new BigDecimal("150.00"));

        Stock updateData = new Stock();
        // All fields are null

        when(stockRepository.findById(1L)).thenReturn(Optional.of(existingStock));
        when(stockRepository.save(any(Stock.class))).thenReturn(existingStock);

        Stock result = stockService.update(1L, updateData);

        // Original values should remain unchanged
        assertEquals("Original Name", existingStock.getName());
        assertEquals(new BigDecimal("150.00"), existingStock.getCurrentPrice());
        verify(stockRepository).save(existingStock);
    }

    @Test
    void testDeleteByIdSuccess() {
        when(stockRepository.existsById(1L)).thenReturn(true);

        boolean result = stockService.deleteById(1L);

        assertTrue(result);
        verify(stockRepository).existsById(1L);
        verify(stockRepository).deleteById(1L);
    }

    @Test
    void testDeleteByIdNotFound() {
        when(stockRepository.existsById(999L)).thenReturn(false);

        boolean result = stockService.deleteById(999L);

        assertFalse(result);
        verify(stockRepository).existsById(999L);
        verify(stockRepository, never()).deleteById(999L);
    }

    @Test
    void testDeleteBySymbolSuccess() {
        when(stockRepository.deleteBySymbol("AAPL")).thenReturn(1);

        boolean result = stockService.deleteBySymbol("aapl");

        assertTrue(result);
        verify(stockRepository).deleteBySymbol("AAPL");
    }

    @Test
    void testDeleteBySymbolNotFound() {
        when(stockRepository.deleteBySymbol("NONEXISTENT")).thenReturn(0);

        boolean result = stockService.deleteBySymbol("NONEXISTENT");

        assertFalse(result);
        verify(stockRepository).deleteBySymbol("NONEXISTENT");
    }

    @Test
    void testUpdatePriceSuccess() {
        when(stockRepository.findBySymbol("AAPL")).thenReturn(Optional.of(sampleStock));
        when(stockRepository.save(any(Stock.class))).thenReturn(sampleStock);

        Stock result = stockService.updatePrice("aapl", new BigDecimal("155.00"));

        assertEquals(new BigDecimal("155.00"), sampleStock.getCurrentPrice());
        verify(stockRepository).findBySymbol("AAPL");
        verify(stockRepository).save(sampleStock);
    }

    @Test
    void testUpdatePriceNotFound() {
        when(stockRepository.findBySymbol("NONEXISTENT")).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> stockService.updatePrice("NONEXISTENT", new BigDecimal("100.00"))
        );

        assertEquals("Stock not found with symbol: NONEXISTENT", exception.getMessage());
        verify(stockRepository).findBySymbol("NONEXISTENT");
        verify(stockRepository, never()).save(any(Stock.class));
    }

    @Test
    void testUpdateVolumeSuccess() {
        when(stockRepository.findBySymbol("AAPL")).thenReturn(Optional.of(sampleStock));
        when(stockRepository.save(any(Stock.class))).thenReturn(sampleStock);

        Stock result = stockService.updateVolume("aapl", 1500000L);

        assertEquals(1500000L, sampleStock.getVolume());
        verify(stockRepository).findBySymbol("AAPL");
        verify(stockRepository).save(sampleStock);
    }

    @Test
    void testUpdateVolumeNotFound() {
        when(stockRepository.findBySymbol("NONEXISTENT")).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> stockService.updateVolume("NONEXISTENT", 1000000L)
        );

        assertEquals("Stock not found with symbol: NONEXISTENT", exception.getMessage());
        verify(stockRepository).findBySymbol("NONEXISTENT");
        verify(stockRepository, never()).save(any(Stock.class));
    }

    @Test
    void testBulkUpdatePriceAndVolume() {
        when(stockRepository.updatePriceAndVolumeBySymbol("AAPL", new BigDecimal("155.00"), 1500000L))
            .thenReturn(1);

        int result = stockService.bulkUpdatePriceAndVolume("aapl", new BigDecimal("155.00"), 1500000L);

        assertEquals(1, result);
        verify(stockRepository).updatePriceAndVolumeBySymbol("AAPL", new BigDecimal("155.00"), 1500000L);
    }

    @Test
    void testFindBySector() {
        List<Stock> stocks = Arrays.asList(sampleStock);
        when(stockReadService.findBySector("Technology", 20, 0)).thenReturn(stocks);

        List<Stock> result = stockService.findBySector("Technology", 0, 20);

        assertEquals(1, result.size());
        assertEquals("Technology", result.get(0).getSector());
        verify(stockReadService).findBySector("Technology", 20, 0);
    }

    @Test
    void testFindByIndustry() {
        List<Stock> stocks = Arrays.asList(sampleStock);
        when(stockReadService.findByIndustry("Consumer Electronics", 15, 15)).thenReturn(stocks);

        List<Stock> result = stockService.findByIndustry("Consumer Electronics", 1, 15);

        assertEquals(1, result.size());
        assertEquals("Consumer Electronics", result.get(0).getIndustry());
        verify(stockReadService).findByIndustry("Consumer Electronics", 15, 15);
    }

    @Test
    void testFindByPriceRange() {
        List<Stock> stocks = Arrays.asList(sampleStock);
        BigDecimal minPrice = new BigDecimal("100.00");
        BigDecimal maxPrice = new BigDecimal("200.00");
        
        when(stockReadService.findByPriceRange(minPrice, maxPrice, 10, 20)).thenReturn(stocks);

        List<Stock> result = stockService.findByPriceRange(minPrice, maxPrice, 2, 10);

        assertEquals(1, result.size());
        verify(stockReadService).findByPriceRange(minPrice, maxPrice, 10, 20);
    }

    @Test
    void testFindTopByMarketCap() {
        List<Stock> stocks = Arrays.asList(sampleStock);
        when(stockReadService.findTopByMarketCap(5)).thenReturn(stocks);

        List<Stock> result = stockService.findTopByMarketCap(5);

        assertEquals(1, result.size());
        verify(stockReadService).findTopByMarketCap(5);
    }

    @Test
    void testFindTopByVolume() {
        List<Stock> stocks = Arrays.asList(sampleStock);
        when(stockReadService.findTopByVolume(10)).thenReturn(stocks);

        List<Stock> result = stockService.findTopByVolume(10);

        assertEquals(1, result.size());
        verify(stockReadService).findTopByVolume(10);
    }

    @Test
    void testSearchByName() {
        List<Stock> stocks = Arrays.asList(sampleStock);
        when(stockReadService.findByNameSearch("Apple", 20, 0)).thenReturn(stocks);

        List<Stock> result = stockService.searchByName("Apple", 0, 20);

        assertEquals(1, result.size());
        verify(stockReadService).findByNameSearch("Apple", 20, 0);
    }

    @Test
    void testSearchBySymbol() {
        List<Stock> stocks = Arrays.asList(sampleStock);
        when(stockReadService.findBySymbolSearch("AA", 15, 15)).thenReturn(stocks);

        List<Stock> result = stockService.searchBySymbol("AA", 1, 15);

        assertEquals(1, result.size());
        verify(stockReadService).findBySymbolSearch("AA", 15, 15);
    }

    @Test
    void testGetDistinctSectors() {
        List<String> sectors = Arrays.asList("Technology", "Finance");
        when(stockReadService.findDistinctSectors()).thenReturn(sectors);

        List<String> result = stockService.getDistinctSectors();

        assertEquals(2, result.size());
        assertTrue(result.contains("Technology"));
        assertTrue(result.contains("Finance"));
        verify(stockReadService).findDistinctSectors();
    }

    @Test
    void testGetDistinctIndustries() {
        List<String> industries = Arrays.asList("Consumer Electronics", "Banking");
        when(stockReadService.findDistinctIndustries()).thenReturn(industries);

        List<String> result = stockService.getDistinctIndustries();

        assertEquals(2, result.size());
        assertTrue(result.contains("Consumer Electronics"));
        assertTrue(result.contains("Banking"));
        verify(stockReadService).findDistinctIndustries();
    }

    @Test
    void testGetTotalCount() {
        when(stockReadService.countTotal()).thenReturn(100L);

        Long result = stockService.getTotalCount();

        assertEquals(100L, result);
        verify(stockReadService).countTotal();
    }

    @Test
    void testGetCountBySector() {
        when(stockReadService.countBySector("Technology")).thenReturn(25L);

        Long result = stockService.getCountBySector("Technology");

        assertEquals(25L, result);
        verify(stockReadService).countBySector("Technology");
    }

    @Test
    void testGetHighPerformers() {
        List<Stock> stocks = Arrays.asList(sampleStock);
        when(stockReadService.findHighPerformers(10)).thenReturn(stocks);

        List<Stock> result = stockService.getHighPerformers(10);

        assertEquals(1, result.size());
        verify(stockReadService).findHighPerformers(10);
    }

    @Test
    void testGetLowPerformers() {
        List<Stock> stocks = Arrays.asList(sampleStock);
        when(stockReadService.findLowPerformers(5)).thenReturn(stocks);

        List<Stock> result = stockService.getLowPerformers(5);

        assertEquals(1, result.size());
        verify(stockReadService).findLowPerformers(5);
    }

    @Test
    void testGetValueStocks() {
        List<Stock> stocks = Arrays.asList(sampleStock);
        BigDecimal maxPeRatio = new BigDecimal("15.0");
        when(stockReadService.findValueStocks(maxPeRatio, 10)).thenReturn(stocks);

        List<Stock> result = stockService.getValueStocks(maxPeRatio, 10);

        assertEquals(1, result.size());
        verify(stockReadService).findValueStocks(maxPeRatio, 10);
    }

    @Test
    void testGetDividendStocks() {
        List<Stock> stocks = Arrays.asList(sampleStock);
        BigDecimal minDividendYield = new BigDecimal("0.02");
        when(stockReadService.findDividendStocks(minDividendYield, 15)).thenReturn(stocks);

        List<Stock> result = stockService.getDividendStocks(minDividendYield, 15);

        assertEquals(1, result.size());
        verify(stockReadService).findDividendStocks(minDividendYield, 15);
    }

    @Test
    void testExistsById() {
        when(stockRepository.existsById(1L)).thenReturn(true);
        when(stockRepository.existsById(999L)).thenReturn(false);

        assertTrue(stockService.existsById(1L));
        assertFalse(stockService.existsById(999L));

        verify(stockRepository).existsById(1L);
        verify(stockRepository).existsById(999L);
    }

    @Test
    void testExistsBySymbol() {
        when(stockRepository.existsBySymbol("AAPL")).thenReturn(true);
        when(stockRepository.existsBySymbol("NONEXISTENT")).thenReturn(false);

        assertTrue(stockService.existsBySymbol("aapl"));
        assertFalse(stockService.existsBySymbol("NONEXISTENT"));

        verify(stockRepository).existsBySymbol("AAPL");
        verify(stockRepository).existsBySymbol("NONEXISTENT");
    }

    @Test
    void testUpdateWithSameSymbol() {
        Stock existingStock = new Stock();
        existingStock.setId(1L);
        existingStock.setSymbol("AAPL");
        existingStock.setName("Apple Inc.");

        Stock updateData = new Stock();
        updateData.setSymbol("AAPL"); // Same symbol
        updateData.setName("Apple Inc. Updated");

        when(stockRepository.findById(1L)).thenReturn(Optional.of(existingStock));
        when(stockRepository.save(any(Stock.class))).thenReturn(existingStock);

        Stock result = stockService.update(1L, updateData);

        assertEquals("Apple Inc. Updated", existingStock.getName());
        assertEquals("AAPL", existingStock.getSymbol());
        verify(stockRepository).findById(1L);
        verify(stockRepository, never()).existsBySymbol(anyString());
        verify(stockRepository).save(existingStock);
    }
}