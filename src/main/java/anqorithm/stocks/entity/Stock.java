package anqorithm.stocks.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Objects;

@Entity
@Table(name = "stocks", indexes = {
        @Index(name = "idx_stocks_symbol", columnList = "symbol"),
        @Index(name = "idx_stocks_sector", columnList = "sector"),
        @Index(name = "idx_stocks_market_cap", columnList = "market_cap"),
        @Index(name = "idx_stocks_current_price", columnList = "current_price")
})
@NamedQueries({
        @NamedQuery(name = "Stock.findBySymbol", query = "SELECT s FROM Stock s WHERE s.symbol = :symbol"),
        @NamedQuery(name = "Stock.findBySector", query = "SELECT s FROM Stock s WHERE s.sector = :sector"),
        @NamedQuery(name = "Stock.findByPriceRange", query = "SELECT s FROM Stock s WHERE s.currentPrice BETWEEN :minPrice AND :maxPrice")
})
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "symbol", nullable = false, unique = true, length = 10)
    @NotBlank(message = "Stock symbol is required")
    @Size(min = 1, max = 10, message = "Stock symbol must be between 1 and 10 characters")
    @Pattern(regexp = "^[A-Z]+$", message = "Stock symbol must contain only uppercase letters")
    private String symbol;

    @Column(name = "name", nullable = false, length = 255)
    @NotBlank(message = "Company name is required")
    @Size(min = 1, max = 255, message = "Company name must be between 1 and 255 characters")
    private String name;

    @Column(name = "current_price", nullable = false, precision = 12, scale = 4)
    @NotNull(message = "Current price is required")
    @DecimalMin(value = "0.0001", message = "Current price must be greater than 0")
    @Digits(integer = 8, fraction = 4, message = "Current price must have at most 8 integer digits and 4 decimal places")
    private BigDecimal currentPrice;

    @Column(name = "market_cap")
    @Min(value = 0, message = "Market cap must be non-negative")
    private Long marketCap;

    @Column(name = "sector", length = 100)
    @Size(max = 100, message = "Sector must be at most 100 characters")
    private String sector;

    @Column(name = "industry", length = 100)
    @Size(max = 100, message = "Industry must be at most 100 characters")
    private String industry;

    @Column(name = "dividend_yield", precision = 5, scale = 4)
    @DecimalMin(value = "0.0", message = "Dividend yield must be non-negative")
    @DecimalMax(value = "1.0", message = "Dividend yield must not exceed 100%")
    @Digits(integer = 1, fraction = 4, message = "Dividend yield must have at most 1 integer digit and 4 decimal places")
    private BigDecimal dividendYield;

    @Column(name = "pe_ratio", precision = 8, scale = 2)
    @DecimalMin(value = "0.01", message = "PE ratio must be greater than 0")
    @Digits(integer = 6, fraction = 2, message = "PE ratio must have at most 6 integer digits and 2 decimal places")
    private BigDecimal peRatio;

    @Column(name = "eps", precision = 8, scale = 2)
    @Digits(integer = 6, fraction = 2, message = "EPS must have at most 6 integer digits and 2 decimal places")
    private BigDecimal eps;

    @Column(name = "fifty_two_week_high", precision = 12, scale = 4)
    @DecimalMin(value = "0.0001", message = "52-week high must be greater than 0")
    @Digits(integer = 8, fraction = 4, message = "52-week high must have at most 8 integer digits and 4 decimal places")
    private BigDecimal fiftyTwoWeekHigh;

    @Column(name = "fifty_two_week_low", precision = 12, scale = 4)
    @DecimalMin(value = "0.0001", message = "52-week low must be greater than 0")
    @Digits(integer = 8, fraction = 4, message = "52-week low must have at most 8 integer digits and 4 decimal places")
    private BigDecimal fiftyTwoWeekLow;

    @Column(name = "volume", columnDefinition = "BIGINT DEFAULT 0")
    @Min(value = 0, message = "Volume must be non-negative")
    private Long volume = 0L;

    @Column(name = "average_volume", columnDefinition = "BIGINT DEFAULT 0")
    @Min(value = 0, message = "Average volume must be non-negative")
    private Long averageVolume = 0L;

    @Column(name = "beta", precision = 6, scale = 4)
    @DecimalMin(value = "0.0", message = "Beta must be non-negative")
    @Digits(integer = 2, fraction = 4, message = "Beta must have at most 2 integer digits and 4 decimal places")
    private BigDecimal beta;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private ZonedDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private ZonedDateTime updatedAt;

    @Version
    @Column(name = "version")
    private Long version;

    public Stock() {
    }

    public Stock(String symbol, String name, BigDecimal currentPrice) {
        this.symbol = symbol;
        this.name = name;
        this.currentPrice = currentPrice;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(BigDecimal currentPrice) {
        this.currentPrice = currentPrice;
    }

    public Long getMarketCap() {
        return marketCap;
    }

    public void setMarketCap(Long marketCap) {
        this.marketCap = marketCap;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public BigDecimal getDividendYield() {
        return dividendYield;
    }

    public void setDividendYield(BigDecimal dividendYield) {
        this.dividendYield = dividendYield;
    }

    public BigDecimal getPeRatio() {
        return peRatio;
    }

    public void setPeRatio(BigDecimal peRatio) {
        this.peRatio = peRatio;
    }

    public BigDecimal getEps() {
        return eps;
    }

    public void setEps(BigDecimal eps) {
        this.eps = eps;
    }

    public BigDecimal getFiftyTwoWeekHigh() {
        return fiftyTwoWeekHigh;
    }

    public void setFiftyTwoWeekHigh(BigDecimal fiftyTwoWeekHigh) {
        this.fiftyTwoWeekHigh = fiftyTwoWeekHigh;
    }

    public BigDecimal getFiftyTwoWeekLow() {
        return fiftyTwoWeekLow;
    }

    public void setFiftyTwoWeekLow(BigDecimal fiftyTwoWeekLow) {
        this.fiftyTwoWeekLow = fiftyTwoWeekLow;
    }

    public Long getVolume() {
        return volume;
    }

    public void setVolume(Long volume) {
        this.volume = volume;
    }

    public Long getAverageVolume() {
        return averageVolume;
    }

    public void setAverageVolume(Long averageVolume) {
        this.averageVolume = averageVolume;
    }

    public BigDecimal getBeta() {
        return beta;
    }

    public void setBeta(BigDecimal beta) {
        this.beta = beta;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public ZonedDateTime getUpdatedAt() {
        return updatedAt;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    @PrePersist
    protected void onCreate() {
        if (volume == null) {
            volume = 0L;
        }
        if (averageVolume == null) {
            averageVolume = 0L;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        if (volume == null) {
            volume = 0L;
        }
        if (averageVolume == null) {
            averageVolume = 0L;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Stock stock = (Stock) o;
        return Objects.equals(id, stock.id) && Objects.equals(symbol, stock.symbol);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, symbol);
    }

    @Override
    public String toString() {
        return "Stock{" +
                "id=" + id +
                ", symbol='" + symbol + '\'' +
                ", name='" + name + '\'' +
                ", currentPrice=" + currentPrice +
                ", marketCap=" + marketCap +
                ", sector='" + sector + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}