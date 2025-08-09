package anqorithm.stocks.service;

import anqorithm.stocks.entity.Stock;
import anqorithm.stocks.repository.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class StockService {

    private final StockRepository stockRepository;
    private final StockReadService stockReadService;

    @Autowired
    public StockService(StockRepository stockRepository, StockReadService stockReadService) {
        this.stockRepository = stockRepository;
        this.stockReadService = stockReadService;
    }

    @Cacheable(value = "stocks", key = "#id")
    @Transactional(readOnly = true)
    public Optional<Stock> findById(Long id) {
        return stockReadService.findById(id);
    }

    @Cacheable(value = "stocks", key = "#symbol")
    @Transactional(readOnly = true)
    public Optional<Stock> findBySymbol(String symbol) {
        return stockReadService.findBySymbol(symbol);
    }

    @Transactional(readOnly = true)
    public List<Stock> findAll(int page, int size) {
        int offset = page * size;
        return stockReadService.findAll(size, offset);
    }

    @Transactional(readOnly = true)
    public Page<Stock> findAllPaged(int page, int size, String sortBy, String sortDirection) {
        Sort.Direction direction = "desc".equalsIgnoreCase(sortDirection) ? 
            Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        return stockRepository.findAll(pageable);
    }

    @CacheEvict(value = "stocks", allEntries = true)
    @Transactional
    public Stock create(Stock stock) {
        if (stock.getSymbol() != null) {
            stock.setSymbol(stock.getSymbol().toUpperCase());
        }
        
        if (stockRepository.existsBySymbol(stock.getSymbol())) {
            throw new IllegalArgumentException("Stock with symbol " + stock.getSymbol() + " already exists");
        }
        
        return stockRepository.save(stock);
    }

    @CacheEvict(value = "stocks", key = "#id")
    @Transactional
    public Stock update(Long id, Stock updatedStock) {
        Stock existingStock = stockRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Stock not found with id: " + id));

        if (updatedStock.getSymbol() != null && !updatedStock.getSymbol().equals(existingStock.getSymbol())) {
            String upperSymbol = updatedStock.getSymbol().toUpperCase();
            if (stockRepository.existsBySymbol(upperSymbol)) {
                throw new IllegalArgumentException("Stock with symbol " + upperSymbol + " already exists");
            }
            existingStock.setSymbol(upperSymbol);
        }

        if (updatedStock.getName() != null) {
            existingStock.setName(updatedStock.getName());
        }
        if (updatedStock.getCurrentPrice() != null) {
            existingStock.setCurrentPrice(updatedStock.getCurrentPrice());
        }
        if (updatedStock.getMarketCap() != null) {
            existingStock.setMarketCap(updatedStock.getMarketCap());
        }
        if (updatedStock.getSector() != null) {
            existingStock.setSector(updatedStock.getSector());
        }
        if (updatedStock.getIndustry() != null) {
            existingStock.setIndustry(updatedStock.getIndustry());
        }
        if (updatedStock.getDividendYield() != null) {
            existingStock.setDividendYield(updatedStock.getDividendYield());
        }
        if (updatedStock.getPeRatio() != null) {
            existingStock.setPeRatio(updatedStock.getPeRatio());
        }
        if (updatedStock.getEps() != null) {
            existingStock.setEps(updatedStock.getEps());
        }
        if (updatedStock.getFiftyTwoWeekHigh() != null) {
            existingStock.setFiftyTwoWeekHigh(updatedStock.getFiftyTwoWeekHigh());
        }
        if (updatedStock.getFiftyTwoWeekLow() != null) {
            existingStock.setFiftyTwoWeekLow(updatedStock.getFiftyTwoWeekLow());
        }
        if (updatedStock.getVolume() != null) {
            existingStock.setVolume(updatedStock.getVolume());
        }
        if (updatedStock.getAverageVolume() != null) {
            existingStock.setAverageVolume(updatedStock.getAverageVolume());
        }
        if (updatedStock.getBeta() != null) {
            existingStock.setBeta(updatedStock.getBeta());
        }

        return stockRepository.save(existingStock);
    }

    @CacheEvict(value = "stocks", key = "#id")
    @Transactional
    public boolean deleteById(Long id) {
        if (stockRepository.existsById(id)) {
            stockRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @CacheEvict(value = "stocks", key = "#symbol")
    @Transactional
    public boolean deleteBySymbol(String symbol) {
        int deletedRows = stockRepository.deleteBySymbol(symbol.toUpperCase());
        return deletedRows > 0;
    }

    @Transactional
    public Stock updatePrice(String symbol, BigDecimal newPrice) {
        Stock stock = stockRepository.findBySymbol(symbol.toUpperCase())
            .orElseThrow(() -> new IllegalArgumentException("Stock not found with symbol: " + symbol));
        
        stock.setCurrentPrice(newPrice);
        return stockRepository.save(stock);
    }

    @Transactional
    public Stock updateVolume(String symbol, Long volume) {
        Stock stock = stockRepository.findBySymbol(symbol.toUpperCase())
            .orElseThrow(() -> new IllegalArgumentException("Stock not found with symbol: " + symbol));
        
        stock.setVolume(volume);
        return stockRepository.save(stock);
    }

    @Transactional
    public int bulkUpdatePriceAndVolume(String symbol, BigDecimal price, Long volume) {
        return stockRepository.updatePriceAndVolumeBySymbol(symbol.toUpperCase(), price, volume);
    }

    @Transactional(readOnly = true)
    public List<Stock> findBySector(String sector, int page, int size) {
        int offset = page * size;
        return stockReadService.findBySector(sector, size, offset);
    }

    @Transactional(readOnly = true)
    public List<Stock> findByIndustry(String industry, int page, int size) {
        int offset = page * size;
        return stockReadService.findByIndustry(industry, size, offset);
    }

    @Transactional(readOnly = true)
    public List<Stock> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, int page, int size) {
        int offset = page * size;
        return stockReadService.findByPriceRange(minPrice, maxPrice, size, offset);
    }

    @Transactional(readOnly = true)
    public List<Stock> findTopByMarketCap(int limit) {
        return stockReadService.findTopByMarketCap(limit);
    }

    @Transactional(readOnly = true)
    public List<Stock> findTopByVolume(int limit) {
        return stockReadService.findTopByVolume(limit);
    }

    @Transactional(readOnly = true)
    public List<Stock> searchByName(String searchTerm, int page, int size) {
        int offset = page * size;
        return stockReadService.findByNameSearch(searchTerm, size, offset);
    }

    @Transactional(readOnly = true)
    public List<Stock> searchBySymbol(String searchTerm, int page, int size) {
        int offset = page * size;
        return stockReadService.findBySymbolSearch(searchTerm, size, offset);
    }

    @Cacheable(value = "sectors")
    @Transactional(readOnly = true)
    public List<String> getDistinctSectors() {
        return stockReadService.findDistinctSectors();
    }

    @Cacheable(value = "industries")
    @Transactional(readOnly = true)
    public List<String> getDistinctIndustries() {
        return stockReadService.findDistinctIndustries();
    }

    @Transactional(readOnly = true)
    public Long getTotalCount() {
        return stockReadService.countTotal();
    }

    @Transactional(readOnly = true)
    public Long getCountBySector(String sector) {
        return stockReadService.countBySector(sector);
    }

    @Transactional(readOnly = true)
    public List<Stock> getHighPerformers(int limit) {
        return stockReadService.findHighPerformers(limit);
    }

    @Transactional(readOnly = true)
    public List<Stock> getLowPerformers(int limit) {
        return stockReadService.findLowPerformers(limit);
    }

    @Transactional(readOnly = true)
    public List<Stock> getValueStocks(BigDecimal maxPeRatio, int limit) {
        return stockReadService.findValueStocks(maxPeRatio, limit);
    }

    @Transactional(readOnly = true)
    public List<Stock> getDividendStocks(BigDecimal minDividendYield, int limit) {
        return stockReadService.findDividendStocks(minDividendYield, limit);
    }

    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return stockRepository.existsById(id);
    }

    @Transactional(readOnly = true)
    public boolean existsBySymbol(String symbol) {
        return stockRepository.existsBySymbol(symbol.toUpperCase());
    }
}