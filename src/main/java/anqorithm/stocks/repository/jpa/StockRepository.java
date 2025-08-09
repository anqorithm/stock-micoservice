package anqorithm.stocks.repository.jpa;

import anqorithm.stocks.entity.Stock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface StockRepository extends JpaRepository<Stock, Long> {

    Optional<Stock> findBySymbol(String symbol);

    List<Stock> findBySector(String sector);

    Page<Stock> findBySector(String sector, Pageable pageable);

    List<Stock> findByIndustry(String industry);

    Page<Stock> findByIndustry(String industry, Pageable pageable);
}