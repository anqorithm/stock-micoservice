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
        microsoftStock.setDividendYield(new BigDecimal("0.0075"));
        microsoftStock.setPeRatio(new BigDecimal("28.75"));
        microsoftStock.setVolume(1200000L);
        microsoftStock.setBeta(new BigDecimal("0.95"));

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
        return stock;
    }

    @Test
    void testSaveAndFindById() {
        Stock saved = stockRepository.save(appleStock);
        assertNotNull(saved.getId());
        
        Optional<Stock> found = stockRepository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("AAPL", found.get().getSymbol());
        assertEquals("Apple Inc.", found.get().getName());
    }

    @Test
    void testFindBySymbol() {
        stockRepository.save(appleStock);
        
        Optional<Stock> found = stockRepository.findBySymbol("AAPL");
        
        assertTrue(found.isPresent());
        assertEquals("AAPL", found.get().getSymbol());
        assertEquals("Apple Inc.", found.get().getName());
        assertEquals(new BigDecimal("150.00"), found.get().getCurrentPrice());
    }

    @Test
    void testFindBySymbolNotFound() {
        Optional<Stock> found = stockRepository.findBySymbol("NONEXISTENT");
        
        assertFalse(found.isPresent());
    }

    @Test
    void testFindBySector() {
        stockRepository.save(appleStock);
        stockRepository.save(googleStock);
        stockRepository.save(microsoftStock);
        
        List<Stock> technologyStocks = stockRepository.findBySector("Technology");
        
        assertEquals(3, technologyStocks.size());
        assertTrue(technologyStocks.stream().allMatch(stock -> "Technology".equals(stock.getSector())));
    }

    @Test
    void testFindBySectorWithPageable() {
        stockRepository.save(appleStock);
        stockRepository.save(googleStock);
        stockRepository.save(microsoftStock);
        
        Pageable pageable = PageRequest.of(0, 2);
        Page<Stock> technologyStocks = stockRepository.findBySector("Technology", pageable);
        
        assertEquals(2, technologyStocks.getContent().size());
        assertEquals(3, technologyStocks.getTotalElements());
        assertEquals(2, technologyStocks.getTotalPages());
    }

    @Test
    void testFindByIndustry() {
        stockRepository.save(appleStock);
        stockRepository.save(googleStock);
        stockRepository.save(microsoftStock);
        
        List<Stock> softwareStocks = stockRepository.findByIndustry("Software");
        
        assertEquals(1, softwareStocks.size());
        assertEquals("MSFT", softwareStocks.get(0).getSymbol());
    }

    @Test
    void testFindByIndustryWithPageable() {
        stockRepository.save(appleStock);
        stockRepository.save(googleStock);
        stockRepository.save(microsoftStock);
        
        Pageable pageable = PageRequest.of(0, 10);
        Page<Stock> softwareStocks = stockRepository.findByIndustry("Software", pageable);
        
        assertEquals(1, softwareStocks.getContent().size());
        assertEquals(1, softwareStocks.getTotalElements());
        assertEquals(1, softwareStocks.getTotalPages());
    }

    @Test
    void testFindAll() {
        stockRepository.save(appleStock);
        stockRepository.save(googleStock);
        stockRepository.save(microsoftStock);
        
        List<Stock> allStocks = stockRepository.findAll();
        
        assertEquals(3, allStocks.size());
    }

    @Test
    void testFindAllWithPageable() {
        stockRepository.save(appleStock);
        stockRepository.save(googleStock);
        stockRepository.save(microsoftStock);
        
        Pageable pageable = PageRequest.of(0, 2);
        Page<Stock> allStocks = stockRepository.findAll(pageable);
        
        assertEquals(2, allStocks.getContent().size());
        assertEquals(3, allStocks.getTotalElements());
        assertEquals(2, allStocks.getTotalPages());
    }

    @Test
    void testCount() {
        stockRepository.save(appleStock);
        stockRepository.save(googleStock);
        stockRepository.save(microsoftStock);
        
        long count = stockRepository.count();
        
        assertEquals(3, count);
    }

    @Test
    void testExistsById() {
        Stock saved = stockRepository.save(appleStock);
        
        assertTrue(stockRepository.existsById(saved.getId()));
        assertFalse(stockRepository.existsById(999L));
    }

    @Test
    void testDeleteById() {
        Stock saved = stockRepository.save(appleStock);
        Long id = saved.getId();
        
        assertTrue(stockRepository.existsById(id));
        stockRepository.deleteById(id);
        assertFalse(stockRepository.existsById(id));
    }

    @Test
    void testDelete() {
        Stock saved = stockRepository.save(appleStock);
        
        assertTrue(stockRepository.existsById(saved.getId()));
        stockRepository.delete(saved);
        assertFalse(stockRepository.existsById(saved.getId()));
    }

    @Test
    void testDeleteAll() {
        stockRepository.save(appleStock);
        stockRepository.save(googleStock);
        stockRepository.save(microsoftStock);
        
        assertEquals(3, stockRepository.count());
        stockRepository.deleteAll();
        assertEquals(0, stockRepository.count());
    }

    @Test
    void testUpdate() {
        Stock saved = stockRepository.save(appleStock);
        
        saved.setCurrentPrice(new BigDecimal("160.00"));
        saved.setName("Apple Inc. Updated");
        
        Stock updated = stockRepository.save(saved);
        
        assertEquals(saved.getId(), updated.getId());
        assertEquals(new BigDecimal("160.00"), updated.getCurrentPrice());
        assertEquals("Apple Inc. Updated", updated.getName());
    }
}