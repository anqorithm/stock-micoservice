package anqorithm.stocks.repository;

import anqorithm.stocks.entity.Stock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface StockRepository extends JpaRepository<Stock, Long> {

    Optional<Stock> findBySymbol(String symbol);

    boolean existsBySymbol(String symbol);

    List<Stock> findBySector(String sector);

    Page<Stock> findBySector(String sector, Pageable pageable);

    List<Stock> findByIndustry(String industry);

    Page<Stock> findByIndustry(String industry, Pageable pageable);

    @Query("SELECT s FROM Stock s WHERE s.currentPrice BETWEEN :minPrice AND :maxPrice")
    List<Stock> findByPriceRange(@Param("minPrice") BigDecimal minPrice, 
                                 @Param("maxPrice") BigDecimal maxPrice);

    @Query("SELECT s FROM Stock s WHERE s.currentPrice BETWEEN :minPrice AND :maxPrice")
    Page<Stock> findByPriceRange(@Param("minPrice") BigDecimal minPrice, 
                                 @Param("maxPrice") BigDecimal maxPrice, 
                                 Pageable pageable);

    @Query("SELECT s FROM Stock s WHERE s.marketCap >= :minMarketCap ORDER BY s.marketCap DESC")
    List<Stock> findByMinimumMarketCap(@Param("minMarketCap") Long minMarketCap);

    @Query("SELECT s FROM Stock s WHERE s.marketCap >= :minMarketCap ORDER BY s.marketCap DESC")
    Page<Stock> findByMinimumMarketCap(@Param("minMarketCap") Long minMarketCap, Pageable pageable);

    @Query("SELECT s FROM Stock s WHERE s.dividendYield >= :minDividendYield ORDER BY s.dividendYield DESC")
    List<Stock> findByMinimumDividendYield(@Param("minDividendYield") BigDecimal minDividendYield);

    @Query("SELECT s FROM Stock s WHERE s.peRatio BETWEEN :minPE AND :maxPE ORDER BY s.peRatio ASC")
    List<Stock> findByPeRatioRange(@Param("minPE") BigDecimal minPE, 
                                   @Param("maxPE") BigDecimal maxPE);

    @Query("SELECT s FROM Stock s WHERE UPPER(s.name) LIKE UPPER(CONCAT('%', :name, '%'))")
    List<Stock> findByNameContainingIgnoreCase(@Param("name") String name);

    @Query("SELECT s FROM Stock s WHERE UPPER(s.name) LIKE UPPER(CONCAT('%', :name, '%'))")
    Page<Stock> findByNameContainingIgnoreCase(@Param("name") String name, Pageable pageable);

    @Query("SELECT s FROM Stock s WHERE UPPER(s.symbol) LIKE UPPER(CONCAT('%', :symbol, '%'))")
    List<Stock> findBySymbolContainingIgnoreCase(@Param("symbol") String symbol);

    @Query("SELECT DISTINCT s.sector FROM Stock s WHERE s.sector IS NOT NULL ORDER BY s.sector")
    List<String> findDistinctSectors();

    @Query("SELECT DISTINCT s.industry FROM Stock s WHERE s.industry IS NOT NULL ORDER BY s.industry")
    List<String> findDistinctIndustries();

    @Query("SELECT COUNT(s) FROM Stock s WHERE s.sector = :sector")
    Long countBySector(@Param("sector") String sector);

    @Query("SELECT s FROM Stock s ORDER BY s.marketCap DESC")
    Page<Stock> findAllOrderByMarketCapDesc(Pageable pageable);

    @Query("SELECT s FROM Stock s ORDER BY s.currentPrice DESC")
    Page<Stock> findAllOrderByCurrentPriceDesc(Pageable pageable);

    @Query("SELECT s FROM Stock s ORDER BY s.volume DESC")
    Page<Stock> findAllOrderByVolumeDesc(Pageable pageable);

    @Modifying
    @Query("UPDATE Stock s SET s.currentPrice = :newPrice WHERE s.id = :id")
    int updateCurrentPrice(@Param("id") Long id, @Param("newPrice") BigDecimal newPrice);

    @Modifying
    @Query("UPDATE Stock s SET s.volume = :volume WHERE s.id = :id")
    int updateVolume(@Param("id") Long id, @Param("volume") Long volume);

    @Modifying
    @Query("UPDATE Stock s SET s.currentPrice = :price, s.volume = :volume WHERE s.symbol = :symbol")
    int updatePriceAndVolumeBySymbol(@Param("symbol") String symbol, 
                                     @Param("price") BigDecimal price, 
                                     @Param("volume") Long volume);

    @Modifying
    @Query("DELETE FROM Stock s WHERE s.symbol = :symbol")
    int deleteBySymbol(@Param("symbol") String symbol);

    @Query(value = "SELECT s FROM Stock s WHERE s.sector = :sector", 
           countQuery = "SELECT COUNT(s) FROM Stock s WHERE s.sector = :sector")
    Page<Stock> findBySectorWithCustomCount(@Param("sector") String sector, Pageable pageable);

    @Query("SELECT s FROM Stock s WHERE s.beta BETWEEN :minBeta AND :maxBeta ORDER BY s.beta ASC")
    List<Stock> findByBetaRange(@Param("minBeta") BigDecimal minBeta, 
                                @Param("maxBeta") BigDecimal maxBeta);

    @Query("SELECT s FROM Stock s WHERE s.currentPrice >= s.fiftyTwoWeekHigh * 0.9")
    List<Stock> findNearFiftyTwoWeekHigh();

    @Query("SELECT s FROM Stock s WHERE s.currentPrice <= s.fiftyTwoWeekLow * 1.1")
    List<Stock> findNearFiftyTwoWeekLow();
}