package anqorithm.stocks.repository;

import anqorithm.stocks.entity.Stock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestPropertySource(properties = {
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class StockRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private StockRepository stockRepository;

    private Stock appleStock;
    private Stock googleStock;
    private Stock microsoftStock;

    @BeforeEach
    void setUp() {
        appleStock = createStock("AAPL", "Apple Inc.", "150.00", 2500000000000L, "Technology", "Consumer Electronics");
        appleStock.setDividendYield(new BigDecimal("0.0050"));
        appleStock.setPeRatio(new BigDecimal("25.50"));
        appleStock.setVolume(1000000L);
        appleStock.setBeta(new BigDecimal("1.20"));

        googleStock = createStock("GOOGL", "Alphabet Inc.", "130.00", 1650000000000L, "Technology", "Internet Services");
        googleStock.setDividendYield(new BigDecimal("0.0000"));
        googleStock.setPeRatio(new BigDecimal("22.30"));
        googleStock.setVolume(800000L);
        googleStock.setBeta(new BigDecimal("1.05"));

        microsoftStock = createStock("MSFT", "Microsoft Corp.", "300.00", 2800000000000L, "Technology", "Software");
        microsoftStock.setDividendYield(new BigDecimal("0.0070"));
        microsoftStock.setPeRatio(new BigDecimal("30.10"));
        microsoftStock.setVolume(600000L);
        microsoftStock.setBeta(new BigDecimal("0.90"));

        entityManager.persistAndFlush(appleStock);
        entityManager.persistAndFlush(googleStock);
        entityManager.persistAndFlush(microsoftStock);
    }

    private Stock createStock(String symbol, String name, String price, Long marketCap, String sector, String industry) {
        Stock stock = new Stock();
        stock.setSymbol(symbol);
        stock.setName(name);
        stock.setCurrentPrice(new BigDecimal(price));
        stock.setMarketCap(marketCap);
        stock.setSector(sector);
        stock.setIndustry(industry);
        stock.setVolume(0L);
        stock.setAverageVolume(0L);
        return stock;
    }

    @Test
    void testFindBySymbol() {
        Optional<Stock> found = stockRepository.findBySymbol("AAPL");
        
        assertTrue(found.isPresent());
        assertEquals("Apple Inc.", found.get().getName());
        assertEquals(new BigDecimal("150.00"), found.get().getCurrentPrice());
    }

    @Test
    void testFindBySymbolNotFound() {
        Optional<Stock> found = stockRepository.findBySymbol("NONEXISTENT");
        
        assertFalse(found.isPresent());
    }

    @Test
    void testExistsBySymbol() {
        assertTrue(stockRepository.existsBySymbol("AAPL"));
        assertFalse(stockRepository.existsBySymbol("NONEXISTENT"));
    }

    @Test
    void testFindBySector() {
        List<Stock> technologyStocks = stockRepository.findBySector("Technology");
        
        assertEquals(3, technologyStocks.size());
        assertTrue(technologyStocks.stream().allMatch(stock -> "Technology".equals(stock.getSector())));
    }

    @Test
    void testFindBySectorWithPageable() {
        Pageable pageable = PageRequest.of(0, 2);
        Page<Stock> technologyStocks = stockRepository.findBySector("Technology", pageable);
        
        assertEquals(2, technologyStocks.getContent().size());
        assertEquals(3, technologyStocks.getTotalElements());
        assertEquals(2, technologyStocks.getTotalPages());
    }

    @Test
    void testFindByIndustry() {
        List<Stock> softwareStocks = stockRepository.findByIndustry("Software");
        
        assertEquals(1, softwareStocks.size());
        assertEquals("MSFT", softwareStocks.get(0).getSymbol());
    }

    @Test
    void testFindByIndustryWithPageable() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Stock> softwareStocks = stockRepository.findByIndustry("Software", pageable);
        
        assertEquals(1, softwareStocks.getContent().size());
        assertEquals(1, softwareStocks.getTotalElements());
        assertEquals(1, softwareStocks.getTotalPages());
    }

    @Test
    void testFindByPriceRange() {
        List<Stock> stocksInRange = stockRepository.findByPriceRange(new BigDecimal("120.00"), new BigDecimal("200.00"));
        
        assertEquals(2, stocksInRange.size());
        assertTrue(stocksInRange.stream().allMatch(stock -> 
            stock.getCurrentPrice().compareTo(new BigDecimal("120.00")) >= 0 &&
            stock.getCurrentPrice().compareTo(new BigDecimal("200.00")) <= 0));
    }

    @Test
    void testFindByPriceRangeWithPageable() {
        Pageable pageable = PageRequest.of(0, 1);
        Page<Stock> stocksInRange = stockRepository.findByPriceRange(
            new BigDecimal("120.00"), new BigDecimal("200.00"), pageable);
        
        assertEquals(1, stocksInRange.getContent().size());
        assertEquals(2, stocksInRange.getTotalElements());
    }

    @Test
    void testFindByMinimumMarketCap() {
        List<Stock> largeCapStocks = stockRepository.findByMinimumMarketCap(2000000000000L);
        
        assertEquals(2, largeCapStocks.size()); // Only AAPL and MSFT meet the criteria
        // Should be ordered by market cap descending
        assertEquals("MSFT", largeCapStocks.get(0).getSymbol()); // Highest market cap
    }

    @Test
    void testFindByMinimumMarketCapWithPageable() {
        Pageable pageable = PageRequest.of(0, 2);
        Page<Stock> largeCapStocks = stockRepository.findByMinimumMarketCap(2000000000000L, pageable);
        
        assertEquals(2, largeCapStocks.getContent().size());
        assertEquals(2, largeCapStocks.getTotalElements()); // Only AAPL and MSFT meet the criteria
    }

    @Test
    void testFindByMinimumDividendYield() {
        List<Stock> dividendStocks = stockRepository.findByMinimumDividendYield(new BigDecimal("0.0050"));
        
        assertEquals(2, dividendStocks.size());
        assertTrue(dividendStocks.stream().allMatch(stock -> 
            stock.getDividendYield().compareTo(new BigDecimal("0.0050")) >= 0));
        // Should be ordered by dividend yield descending
        assertEquals("MSFT", dividendStocks.get(0).getSymbol()); // Higher dividend yield
    }

    @Test
    void testFindByPeRatioRange() {
        List<Stock> valueStocks = stockRepository.findByPeRatioRange(new BigDecimal("20.00"), new BigDecimal("30.00"));
        
        assertEquals(2, valueStocks.size());
        assertTrue(valueStocks.stream().allMatch(stock -> 
            stock.getPeRatio().compareTo(new BigDecimal("20.00")) >= 0 &&
            stock.getPeRatio().compareTo(new BigDecimal("30.00")) <= 0));
        // Should be ordered by PE ratio ascending
        assertEquals("GOOGL", valueStocks.get(0).getSymbol()); // Lower PE ratio
    }

    @Test
    void testFindByNameContainingIgnoreCase() {
        List<Stock> stocks = stockRepository.findByNameContainingIgnoreCase("apple");
        
        assertEquals(1, stocks.size());
        assertEquals("AAPL", stocks.get(0).getSymbol());
    }

    @Test
    void testFindByNameContainingIgnoreCaseWithPageable() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Stock> stocks = stockRepository.findByNameContainingIgnoreCase("inc", pageable);
        
        assertEquals(2, stocks.getContent().size()); // Apple Inc. and Alphabet Inc.
        assertEquals(2, stocks.getTotalElements());
    }

    @Test
    void testFindBySymbolContainingIgnoreCase() {
        List<Stock> stocks = stockRepository.findBySymbolContainingIgnoreCase("aa");
        
        assertEquals(1, stocks.size());
        assertEquals("AAPL", stocks.get(0).getSymbol());
    }

    @Test
    void testFindDistinctSectors() {
        List<String> sectors = stockRepository.findDistinctSectors();
        
        assertEquals(1, sectors.size());
        assertEquals("Technology", sectors.get(0));
    }

    @Test
    void testFindDistinctIndustries() {
        List<String> industries = stockRepository.findDistinctIndustries();
        
        assertEquals(3, industries.size());
        assertTrue(industries.contains("Consumer Electronics"));
        assertTrue(industries.contains("Internet Services"));
        assertTrue(industries.contains("Software"));
    }

    @Test
    void testCountBySector() {
        Long count = stockRepository.countBySector("Technology");
        
        assertEquals(3L, count);
    }

    @Test
    void testCountBySectorNotFound() {
        Long count = stockRepository.countBySector("NonExistent");
        
        assertEquals(0L, count);
    }

    @Test
    void testFindAllOrderByMarketCapDesc() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Stock> stocks = stockRepository.findAllOrderByMarketCapDesc(pageable);
        
        assertEquals(3, stocks.getContent().size());
        // Should be ordered by market cap descending
        assertEquals("MSFT", stocks.getContent().get(0).getSymbol());
        assertEquals("AAPL", stocks.getContent().get(1).getSymbol());
        assertEquals("GOOGL", stocks.getContent().get(2).getSymbol());
    }

    @Test
    void testFindAllOrderByCurrentPriceDesc() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Stock> stocks = stockRepository.findAllOrderByCurrentPriceDesc(pageable);
        
        assertEquals(3, stocks.getContent().size());
        // Should be ordered by price descending
        assertEquals("MSFT", stocks.getContent().get(0).getSymbol());
        assertEquals("AAPL", stocks.getContent().get(1).getSymbol());
        assertEquals("GOOGL", stocks.getContent().get(2).getSymbol());
    }

    @Test
    void testFindAllOrderByVolumeDesc() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Stock> stocks = stockRepository.findAllOrderByVolumeDesc(pageable);
        
        assertEquals(3, stocks.getContent().size());
        // Should be ordered by volume descending
        assertEquals("AAPL", stocks.getContent().get(0).getSymbol());
        assertEquals("GOOGL", stocks.getContent().get(1).getSymbol());
        assertEquals("MSFT", stocks.getContent().get(2).getSymbol());
    }

    @Test
    void testUpdateCurrentPrice() {
        int updatedRows = stockRepository.updateCurrentPrice(appleStock.getId(), new BigDecimal("160.00"));
        
        assertEquals(1, updatedRows);
        
        // Verify the update
        entityManager.clear();
        Stock updated = entityManager.find(Stock.class, appleStock.getId());
        assertEquals(0, new BigDecimal("160.00").compareTo(updated.getCurrentPrice()));
    }

    @Test
    void testUpdateCurrentPriceNonExistentId() {
        int updatedRows = stockRepository.updateCurrentPrice(999L, new BigDecimal("160.00"));
        
        assertEquals(0, updatedRows);
    }

    @Test
    void testUpdateVolume() {
        int updatedRows = stockRepository.updateVolume(appleStock.getId(), 2000000L);
        
        assertEquals(1, updatedRows);
        
        // Verify the update
        entityManager.clear();
        Stock updated = entityManager.find(Stock.class, appleStock.getId());
        assertEquals(2000000L, updated.getVolume());
    }

    @Test
    void testUpdatePriceAndVolumeBySymbol() {
        int updatedRows = stockRepository.updatePriceAndVolumeBySymbol(
            "AAPL", new BigDecimal("165.00"), 1500000L);
        
        assertEquals(1, updatedRows);
        
        // Verify the update
        entityManager.clear();
        Stock updated = stockRepository.findBySymbol("AAPL").orElseThrow();
        assertEquals(0, new BigDecimal("165.00").compareTo(updated.getCurrentPrice()));
        assertEquals(1500000L, updated.getVolume());
    }

    @Test
    void testUpdatePriceAndVolumeBySymbolNotFound() {
        int updatedRows = stockRepository.updatePriceAndVolumeBySymbol(
            "NONEXISTENT", new BigDecimal("165.00"), 1500000L);
        
        assertEquals(0, updatedRows);
    }

    @Test
    void testDeleteBySymbol() {
        int deletedRows = stockRepository.deleteBySymbol("AAPL");
        
        assertEquals(1, deletedRows);
        assertFalse(stockRepository.existsBySymbol("AAPL"));
    }

    @Test
    void testDeleteBySymbolNotFound() {
        int deletedRows = stockRepository.deleteBySymbol("NONEXISTENT");
        
        assertEquals(0, deletedRows);
    }

    @Test
    void testFindBySectorWithCustomCount() {
        Pageable pageable = PageRequest.of(0, 2);
        Page<Stock> stocks = stockRepository.findBySectorWithCustomCount("Technology", pageable);
        
        assertEquals(2, stocks.getContent().size());
        assertEquals(3, stocks.getTotalElements());
    }

    @Test
    void testFindByBetaRange() {
        List<Stock> stocks = stockRepository.findByBetaRange(new BigDecimal("1.00"), new BigDecimal("1.30"));
        
        assertEquals(2, stocks.size());
        // Should be ordered by beta ascending
        assertEquals("GOOGL", stocks.get(0).getSymbol()); // Beta 1.05
        assertEquals("AAPL", stocks.get(1).getSymbol()); // Beta 1.20
    }

    @Test
    void testFindNearFiftyTwoWeekHigh() {
        // Set up some stocks with 52-week data
        appleStock.setFiftyTwoWeekHigh(new BigDecimal("160.00"));
        appleStock.setCurrentPrice(new BigDecimal("155.00")); // 155 >= 160 * 0.9 (144)
        entityManager.merge(appleStock);
        entityManager.flush();
        
        List<Stock> stocks = stockRepository.findNearFiftyTwoWeekHigh();
        
        assertEquals(1, stocks.size());
        assertEquals("AAPL", stocks.get(0).getSymbol());
    }

    @Test
    void testFindNearFiftyTwoWeekLow() {
        // Set up some stocks with 52-week data
        appleStock.setFiftyTwoWeekLow(new BigDecimal("140.00"));
        appleStock.setCurrentPrice(new BigDecimal("150.00")); // 150 <= 140 * 1.1 (154)
        entityManager.merge(appleStock);
        entityManager.flush();
        
        List<Stock> stocks = stockRepository.findNearFiftyTwoWeekLow();
        
        assertEquals(1, stocks.size());
        assertEquals("AAPL", stocks.get(0).getSymbol());
    }

    @Test
    void testRepositoryBehaviorWithNullValues() {
        // Create a stock with minimal required fields
        Stock minimalStock = new Stock();
        minimalStock.setSymbol("MIN");
        minimalStock.setName("Minimal Stock");
        minimalStock.setCurrentPrice(new BigDecimal("10.00"));
        
        Stock saved = stockRepository.save(minimalStock);
        
        assertNotNull(saved.getId());
        assertEquals("MIN", saved.getSymbol());
        assertNull(saved.getSector());
        assertNull(saved.getIndustry());
        assertNull(saved.getMarketCap());
    }

    @Test
    void testSaveAndFindById() {
        Stock newStock = createStock("NEW", "New Company", "50.00", 1000000000L, "Finance", "Banking");
        
        Stock saved = stockRepository.save(newStock);
        
        assertNotNull(saved.getId());
        
        Optional<Stock> found = stockRepository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("NEW", found.get().getSymbol());
        assertEquals("New Company", found.get().getName());
    }

    @Test
    void testDeleteById() {
        Long stockId = appleStock.getId();
        
        stockRepository.deleteById(stockId);
        
        Optional<Stock> found = stockRepository.findById(stockId);
        assertFalse(found.isPresent());
    }

    @Test
    void testFindAll() {
        List<Stock> allStocks = stockRepository.findAll();
        
        assertEquals(3, allStocks.size());
    }

    @Test
    void testCount() {
        long count = stockRepository.count();
        
        assertEquals(3, count);
    }

    @Test
    void testExistsById() {
        assertTrue(stockRepository.existsById(appleStock.getId()));
        assertFalse(stockRepository.existsById(999L));
    }
}