package anqorithm.stocks.controller;

import anqorithm.stocks.entity.Stock;
import anqorithm.stocks.exception.StockAlreadyExistsException;
import anqorithm.stocks.exception.StockNotFoundException;
import anqorithm.stocks.service.StockService;
import io.micrometer.core.annotation.Timed;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/stocks")
@Validated
@Timed
public class StockController {

    private static final Logger logger = LoggerFactory.getLogger(StockController.class);
    private final StockService stockService;

    @Autowired
    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllStocks(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {
        
        logger.info("Getting all stocks - page: {}, size: {}, sortBy: {}, sortDirection: {}", 
                   page, size, sortBy, sortDirection);
        
        Page<Stock> stockPage = stockService.findAllPaged(page, size, sortBy, sortDirection);
        
        Map<String, Object> response = Map.of(
            "stocks", stockPage.getContent(),
            "currentPage", stockPage.getNumber(),
            "totalItems", stockPage.getTotalElements(),
            "totalPages", stockPage.getTotalPages(),
            "hasNext", stockPage.hasNext(),
            "hasPrevious", stockPage.hasPrevious()
        );
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Stock> getStockById(@PathVariable @Positive Long id) {
        logger.info("Getting stock by id: {}", id);
        
        Optional<Stock> stock = stockService.findById(id);
        return stock.map(ResponseEntity::ok)
                   .orElseThrow(() -> StockNotFoundException.byId(id));
    }

    @GetMapping("/symbol/{symbol}")
    public ResponseEntity<Stock> getStockBySymbol(@PathVariable String symbol) {
        logger.info("Getting stock by symbol: {}", symbol);
        
        Optional<Stock> stock = stockService.findBySymbol(symbol);
        return stock.map(ResponseEntity::ok)
                   .orElseThrow(() -> StockNotFoundException.bySymbol(symbol));
    }

    @PostMapping
    public ResponseEntity<Stock> createStock(@Valid @RequestBody Stock stock) {
        logger.info("Creating new stock: {}", stock.getSymbol());
        
        if (stockService.existsBySymbol(stock.getSymbol())) {
            throw StockAlreadyExistsException.bySymbol(stock.getSymbol());
        }
        
        Stock createdStock = stockService.create(stock);
        
        URI location = URI.create("/api/v1/stocks/" + createdStock.getId());
        return ResponseEntity.created(location).body(createdStock);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Stock> updateStock(@PathVariable @Positive Long id, 
                                           @Valid @RequestBody Stock stock) {
        logger.info("Updating stock with id: {}", id);
        
        if (!stockService.existsById(id)) {
            throw StockNotFoundException.byId(id);
        }
        
        Stock updatedStock = stockService.update(id, stock);
        return ResponseEntity.ok(updatedStock);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteStock(@PathVariable @Positive Long id) {
        logger.info("Deleting stock with id: {}", id);
        
        boolean deleted = stockService.deleteById(id);
        if (!deleted) {
            throw StockNotFoundException.byId(id);
        }
        
        Map<String, Object> response = Map.of(
            "message", "Stock successfully deleted",
            "id", id
        );
        
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/symbol/{symbol}")
    public ResponseEntity<Map<String, Object>> deleteStockBySymbol(@PathVariable String symbol) {
        logger.info("Deleting stock with symbol: {}", symbol);
        
        boolean deleted = stockService.deleteBySymbol(symbol);
        if (!deleted) {
            throw StockNotFoundException.bySymbol(symbol);
        }
        
        Map<String, Object> response = Map.of(
            "message", "Stock successfully deleted",
            "symbol", symbol.toUpperCase()
        );
        
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{symbol}/price")
    public ResponseEntity<Stock> updateStockPrice(@PathVariable String symbol,
                                                 @RequestBody Map<String, BigDecimal> priceUpdate) {
        logger.info("Updating price for stock: {}", symbol);
        
        BigDecimal newPrice = priceUpdate.get("price");
        if (newPrice == null || newPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price must be provided and greater than 0");
        }
        
        Stock updatedStock = stockService.updatePrice(symbol, newPrice);
        return ResponseEntity.ok(updatedStock);
    }

    @PatchMapping("/{symbol}/volume")
    public ResponseEntity<Stock> updateStockVolume(@PathVariable String symbol,
                                                  @RequestBody Map<String, Long> volumeUpdate) {
        logger.info("Updating volume for stock: {}", symbol);
        
        Long newVolume = volumeUpdate.get("volume");
        if (newVolume == null || newVolume < 0) {
            throw new IllegalArgumentException("Volume must be provided and non-negative");
        }
        
        Stock updatedStock = stockService.updateVolume(symbol, newVolume);
        return ResponseEntity.ok(updatedStock);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Stock>> searchStocks(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String symbol,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) int size) {
        
        logger.info("Searching stocks - name: {}, symbol: {}", name, symbol);
        
        List<Stock> stocks;
        if (name != null && !name.trim().isEmpty()) {
            stocks = stockService.searchByName(name.trim(), page, size);
        } else if (symbol != null && !symbol.trim().isEmpty()) {
            stocks = stockService.searchBySymbol(symbol.trim(), page, size);
        } else {
            throw new IllegalArgumentException("Either name or symbol parameter must be provided");
        }
        
        return ResponseEntity.ok(stocks);
    }

    @GetMapping("/sector/{sector}")
    public ResponseEntity<List<Stock>> getStocksBySector(
            @PathVariable String sector,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) int size) {
        
        logger.info("Getting stocks by sector: {}", sector);
        List<Stock> stocks = stockService.findBySector(sector, page, size);
        return ResponseEntity.ok(stocks);
    }

    @GetMapping("/industry/{industry}")
    public ResponseEntity<List<Stock>> getStocksByIndustry(
            @PathVariable String industry,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) int size) {
        
        logger.info("Getting stocks by industry: {}", industry);
        List<Stock> stocks = stockService.findByIndustry(industry, page, size);
        return ResponseEntity.ok(stocks);
    }

    @GetMapping("/price-range")
    public ResponseEntity<List<Stock>> getStocksByPriceRange(
            @RequestParam BigDecimal minPrice,
            @RequestParam BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) int size) {
        
        if (minPrice.compareTo(maxPrice) > 0) {
            throw new IllegalArgumentException("minPrice cannot be greater than maxPrice");
        }
        
        logger.info("Getting stocks by price range: {} - {}", minPrice, maxPrice);
        List<Stock> stocks = stockService.findByPriceRange(minPrice, maxPrice, page, size);
        return ResponseEntity.ok(stocks);
    }

    @GetMapping("/top/market-cap")
    public ResponseEntity<List<Stock>> getTopStocksByMarketCap(
            @RequestParam(defaultValue = "10") @Min(1) int limit) {
        
        logger.info("Getting top {} stocks by market cap", limit);
        List<Stock> stocks = stockService.findTopByMarketCap(limit);
        return ResponseEntity.ok(stocks);
    }

    @GetMapping("/top/volume")
    public ResponseEntity<List<Stock>> getTopStocksByVolume(
            @RequestParam(defaultValue = "10") @Min(1) int limit) {
        
        logger.info("Getting top {} stocks by volume", limit);
        List<Stock> stocks = stockService.findTopByVolume(limit);
        return ResponseEntity.ok(stocks);
    }

    @GetMapping("/performers/high")
    public ResponseEntity<List<Stock>> getHighPerformers(
            @RequestParam(defaultValue = "10") @Min(1) int limit) {
        
        logger.info("Getting top {} high performers", limit);
        List<Stock> stocks = stockService.getHighPerformers(limit);
        return ResponseEntity.ok(stocks);
    }

    @GetMapping("/performers/low")
    public ResponseEntity<List<Stock>> getLowPerformers(
            @RequestParam(defaultValue = "10") @Min(1) int limit) {
        
        logger.info("Getting top {} low performers", limit);
        List<Stock> stocks = stockService.getLowPerformers(limit);
        return ResponseEntity.ok(stocks);
    }

    @GetMapping("/value-stocks")
    public ResponseEntity<List<Stock>> getValueStocks(
            @RequestParam(defaultValue = "15.0") BigDecimal maxPeRatio,
            @RequestParam(defaultValue = "10") @Min(1) int limit) {
        
        logger.info("Getting value stocks with PE ratio <= {}", maxPeRatio);
        List<Stock> stocks = stockService.getValueStocks(maxPeRatio, limit);
        return ResponseEntity.ok(stocks);
    }

    @GetMapping("/dividend-stocks")
    public ResponseEntity<List<Stock>> getDividendStocks(
            @RequestParam(defaultValue = "0.02") BigDecimal minDividendYield,
            @RequestParam(defaultValue = "10") @Min(1) int limit) {
        
        logger.info("Getting dividend stocks with yield >= {}", minDividendYield);
        List<Stock> stocks = stockService.getDividendStocks(minDividendYield, limit);
        return ResponseEntity.ok(stocks);
    }

    @GetMapping("/sectors")
    public ResponseEntity<List<String>> getDistinctSectors() {
        logger.info("Getting distinct sectors");
        List<String> sectors = stockService.getDistinctSectors();
        return ResponseEntity.ok(sectors);
    }

    @GetMapping("/industries")
    public ResponseEntity<List<String>> getDistinctIndustries() {
        logger.info("Getting distinct industries");
        List<String> industries = stockService.getDistinctIndustries();
        return ResponseEntity.ok(industries);
    }

    @GetMapping("/stats/count")
    public ResponseEntity<Map<String, Object>> getStockStats() {
        logger.info("Getting stock statistics");
        
        Long totalCount = stockService.getTotalCount();
        List<String> sectors = stockService.getDistinctSectors();
        List<String> industries = stockService.getDistinctIndustries();
        
        Map<String, Object> stats = Map.of(
            "totalStocks", totalCount,
            "totalSectors", sectors.size(),
            "totalIndustries", industries.size(),
            "sectors", sectors,
            "industries", industries
        );
        
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/exists/symbol/{symbol}")
    public ResponseEntity<Map<String, Boolean>> checkStockExists(@PathVariable String symbol) {
        logger.info("Checking if stock exists: {}", symbol);
        
        boolean exists = stockService.existsBySymbol(symbol);
        Map<String, Boolean> response = Map.of("exists", exists);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/batch")
    public ResponseEntity<Map<String, Object>> createStocksBatch(@Valid @RequestBody List<Stock> stocks) {
        logger.info("Creating batch of {} stocks", stocks.size());
        
        List<Stock> createdStocks = stocks.stream()
            .filter(stock -> !stockService.existsBySymbol(stock.getSymbol()))
            .map(stockService::create)
            .toList();
        
        Map<String, Object> response = Map.of(
            "created", createdStocks.size(),
            "skipped", stocks.size() - createdStocks.size(),
            "stocks", createdStocks
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> health = Map.of(
            "status", "UP",
            "service", "Stock API",
            "timestamp", java.time.Instant.now().toString()
        );
        return ResponseEntity.ok(health);
    }
}