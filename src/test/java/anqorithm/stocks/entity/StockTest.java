package anqorithm.stocks.entity;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class StockTest {

    private Validator validator;
    private Stock validStock;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        
        validStock = new Stock();
        validStock.setSymbol("AAPL");
        validStock.setName("Apple Inc.");
        validStock.setCurrentPrice(new BigDecimal("150.00"));
        validStock.setMarketCap(2500000000000L);
        validStock.setSector("Technology");
        validStock.setIndustry("Consumer Electronics");
        validStock.setDividendYield(new BigDecimal("0.0050"));
        validStock.setPeRatio(new BigDecimal("25.50"));
        validStock.setEps(new BigDecimal("6.15"));
        validStock.setFiftyTwoWeekHigh(new BigDecimal("180.00"));
        validStock.setFiftyTwoWeekLow(new BigDecimal("120.00"));
        validStock.setVolume(1000000L);
        validStock.setAverageVolume(850000L);
        validStock.setBeta(new BigDecimal("1.20"));
    }

    @Test
    void testValidStock() {
        Set<ConstraintViolation<Stock>> violations = validator.validate(validStock);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testConstructorWithParameters() {
        Stock stock = new Stock("MSFT", "Microsoft Corp", new BigDecimal("300.00"));
        
        assertEquals("MSFT", stock.getSymbol());
        assertEquals("Microsoft Corp", stock.getName());
        assertEquals(new BigDecimal("300.00"), stock.getCurrentPrice());
    }

    @Test
    void testDefaultConstructor() {
        Stock stock = new Stock();
        
        assertNull(stock.getId());
        assertNull(stock.getSymbol());
        assertNull(stock.getName());
        assertNull(stock.getCurrentPrice());
    }

    @Test
    void testSymbolValidation() {
        // Test blank symbol
        validStock.setSymbol("");
        Set<ConstraintViolation<Stock>> violations = validator.validate(validStock);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Stock symbol is required")));

        // Test null symbol
        validStock.setSymbol(null);
        violations = validator.validate(validStock);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Stock symbol is required")));

        // Test symbol too long
        validStock.setSymbol("VERYLONGSYMBOL");
        violations = validator.validate(validStock);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Stock symbol must be between 1 and 10 characters")));

        // Test symbol with lowercase (should fail pattern)
        validStock.setSymbol("aapl");
        violations = validator.validate(validStock);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Stock symbol must contain only uppercase letters")));
    }

    @Test
    void testNameValidation() {
        // Test blank name
        validStock.setName("");
        Set<ConstraintViolation<Stock>> violations = validator.validate(validStock);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Company name is required")));

        // Test null name
        validStock.setName(null);
        violations = validator.validate(validStock);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Company name is required")));

        // Test name too long
        String longName = "A".repeat(256);
        validStock.setName(longName);
        violations = validator.validate(validStock);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Company name must be between 1 and 255 characters")));
    }

    @Test
    void testCurrentPriceValidation() {
        // Test null price
        validStock.setCurrentPrice(null);
        Set<ConstraintViolation<Stock>> violations = validator.validate(validStock);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Current price is required")));

        // Test zero price
        validStock.setCurrentPrice(BigDecimal.ZERO);
        violations = validator.validate(validStock);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Current price must be greater than 0")));

        // Test negative price
        validStock.setCurrentPrice(new BigDecimal("-10.00"));
        violations = validator.validate(validStock);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Current price must be greater than 0")));
    }

    @Test
    void testMarketCapValidation() {
        // Test negative market cap
        validStock.setMarketCap(-1000L);
        Set<ConstraintViolation<Stock>> violations = validator.validate(validStock);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Market cap must be non-negative")));

        // Test zero market cap (should be valid)
        validStock.setMarketCap(0L);
        violations = validator.validate(validStock);
        assertTrue(violations.isEmpty());

        // Test null market cap (should be valid)
        validStock.setMarketCap(null);
        violations = validator.validate(validStock);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testDividendYieldValidation() {
        // Test negative dividend yield
        validStock.setDividendYield(new BigDecimal("-0.01"));
        Set<ConstraintViolation<Stock>> violations = validator.validate(validStock);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Dividend yield must be non-negative")));

        // Test dividend yield greater than 100%
        validStock.setDividendYield(new BigDecimal("1.01"));
        violations = validator.validate(validStock);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Dividend yield must not exceed 100%")));
    }

    @Test
    void testPeRatioValidation() {
        // Test zero PE ratio
        validStock.setPeRatio(BigDecimal.ZERO);
        Set<ConstraintViolation<Stock>> violations = validator.validate(validStock);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("PE ratio must be greater than 0")));

        // Test negative PE ratio
        validStock.setPeRatio(new BigDecimal("-5.0"));
        violations = validator.validate(validStock);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("PE ratio must be greater than 0")));
    }

    @Test
    void testVolumeValidation() {
        // Test negative volume
        validStock.setVolume(-1000L);
        Set<ConstraintViolation<Stock>> violations = validator.validate(validStock);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Volume must be non-negative")));

        // Test negative average volume
        validStock.setAverageVolume(-1000L);
        violations = validator.validate(validStock);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Average volume must be non-negative")));
    }

    @Test
    void testBetaValidation() {
        // Test negative beta
        validStock.setBeta(new BigDecimal("-0.5"));
        Set<ConstraintViolation<Stock>> violations = validator.validate(validStock);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Beta must be non-negative")));
    }

    @Test
    void testStringFieldsMaxLength() {
        // Test sector max length
        validStock.setSector("A".repeat(101));
        Set<ConstraintViolation<Stock>> violations = validator.validate(validStock);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Sector must be at most 100 characters")));

        // Test industry max length
        validStock.setSector("Technology"); // Reset to valid
        validStock.setIndustry("A".repeat(101));
        violations = validator.validate(validStock);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Industry must be at most 100 characters")));
    }

    @Test
    void testEqualsAndHashCode() {
        Stock stock1 = new Stock();
        stock1.setId(1L);
        stock1.setSymbol("AAPL");

        Stock stock2 = new Stock();
        stock2.setId(1L);
        stock2.setSymbol("AAPL");

        Stock stock3 = new Stock();
        stock3.setId(2L);
        stock3.setSymbol("GOOGL");

        // Test equals
        assertEquals(stock1, stock2);
        assertNotEquals(stock1, stock3);
        assertNotEquals(stock1, null);
        assertNotEquals(stock1, "not a stock");

        // Test hashCode consistency
        assertEquals(stock1.hashCode(), stock2.hashCode());
        
        // Test reflexivity
        assertEquals(stock1, stock1);
    }

    @Test
    void testEqualsWithNullFields() {
        Stock stock1 = new Stock();
        Stock stock2 = new Stock();

        assertEquals(stock1, stock2);
        assertEquals(stock1.hashCode(), stock2.hashCode());

        // Test with null id but same symbol
        stock1.setSymbol("AAPL");
        stock2.setSymbol("AAPL");
        assertEquals(stock1, stock2);
    }

    @Test
    void testToString() {
        validStock.setId(1L);
        
        String toString = validStock.toString();
        
        assertTrue(toString.contains("Stock{"));
        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("symbol='AAPL'"));
        assertTrue(toString.contains("name='Apple Inc.'"));
        assertTrue(toString.contains("currentPrice=150.00"));
        assertTrue(toString.contains("sector='Technology'"));
    }

    @Test
    void testGettersAndSetters() {
        Stock stock = new Stock();
        
        // Test all setters and getters
        stock.setId(1L);
        assertEquals(1L, stock.getId());

        stock.setSymbol("TEST");
        assertEquals("TEST", stock.getSymbol());

        stock.setName("Test Company");
        assertEquals("Test Company", stock.getName());

        BigDecimal price = new BigDecimal("100.00");
        stock.setCurrentPrice(price);
        assertEquals(price, stock.getCurrentPrice());

        stock.setMarketCap(1000000000L);
        assertEquals(1000000000L, stock.getMarketCap());

        stock.setSector("Technology");
        assertEquals("Technology", stock.getSector());

        stock.setIndustry("Software");
        assertEquals("Software", stock.getIndustry());

        BigDecimal dividend = new BigDecimal("0.05");
        stock.setDividendYield(dividend);
        assertEquals(dividend, stock.getDividendYield());

        BigDecimal pe = new BigDecimal("20.0");
        stock.setPeRatio(pe);
        assertEquals(pe, stock.getPeRatio());

        BigDecimal eps = new BigDecimal("5.0");
        stock.setEps(eps);
        assertEquals(eps, stock.getEps());

        BigDecimal high = new BigDecimal("120.0");
        stock.setFiftyTwoWeekHigh(high);
        assertEquals(high, stock.getFiftyTwoWeekHigh());

        BigDecimal low = new BigDecimal("80.0");
        stock.setFiftyTwoWeekLow(low);
        assertEquals(low, stock.getFiftyTwoWeekLow());

        stock.setVolume(1000000L);
        assertEquals(1000000L, stock.getVolume());

        stock.setAverageVolume(900000L);
        assertEquals(900000L, stock.getAverageVolume());

        BigDecimal beta = new BigDecimal("1.5");
        stock.setBeta(beta);
        assertEquals(beta, stock.getBeta());

        ZonedDateTime now = ZonedDateTime.now();
        stock.setVersion(1L);
        assertEquals(1L, stock.getVersion());

        // Test created/updated timestamps (getters only since they're managed by JPA)
        assertNull(stock.getCreatedAt());
        assertNull(stock.getUpdatedAt());
    }

    @Test
    void testPrePersistCallback() {
        Stock stock = new Stock();
        stock.onCreate();
        
        assertEquals(0L, stock.getVolume());
        assertEquals(0L, stock.getAverageVolume());
        
        // Test when volumes are already set
        stock.setVolume(1000L);
        stock.setAverageVolume(900L);
        stock.onCreate();
        
        assertEquals(1000L, stock.getVolume());
        assertEquals(900L, stock.getAverageVolume());
    }

    @Test
    void testPreUpdateCallback() {
        Stock stock = new Stock();
        stock.onUpdate();
        
        assertEquals(0L, stock.getVolume());
        assertEquals(0L, stock.getAverageVolume());
        
        // Test when volumes are already set
        stock.setVolume(1000L);
        stock.setAverageVolume(900L);
        stock.onUpdate();
        
        assertEquals(1000L, stock.getVolume());
        assertEquals(900L, stock.getAverageVolume());
    }

    @Test
    void testFiftyTwoWeekRangeValidation() {
        // Test valid 52-week high
        validStock.setFiftyTwoWeekHigh(new BigDecimal("200.00"));
        Set<ConstraintViolation<Stock>> violations = validator.validate(validStock);
        assertTrue(violations.isEmpty());

        // Test zero 52-week high
        validStock.setFiftyTwoWeekHigh(BigDecimal.ZERO);
        violations = validator.validate(validStock);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("52-week high must be greater than 0")));

        // Test valid 52-week low
        validStock.setFiftyTwoWeekHigh(new BigDecimal("200.00")); // Reset to valid
        validStock.setFiftyTwoWeekLow(new BigDecimal("100.00"));
        violations = validator.validate(validStock);
        assertTrue(violations.isEmpty());

        // Test zero 52-week low
        validStock.setFiftyTwoWeekLow(BigDecimal.ZERO);
        violations = validator.validate(validStock);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("52-week low must be greater than 0")));
    }

    @Test
    void testNullableFields() {
        Stock stock = new Stock();
        stock.setSymbol("TEST");
        stock.setName("Test Company");
        stock.setCurrentPrice(new BigDecimal("100.00"));
        
        // All other fields should be nullable and not cause validation errors
        Set<ConstraintViolation<Stock>> violations = validator.validate(stock);
        assertTrue(violations.isEmpty());
    }
}