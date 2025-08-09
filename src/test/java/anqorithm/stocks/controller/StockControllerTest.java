package anqorithm.stocks.controller;

import anqorithm.stocks.entity.Stock;
import anqorithm.stocks.exception.StockAlreadyExistsException;
import anqorithm.stocks.exception.StockNotFoundException;
import anqorithm.stocks.service.StockService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StockController.class)
class StockControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
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
    void testGetAllStocks() throws Exception {
        List<Stock> stocks = Arrays.asList(sampleStock);
        Pageable pageable = PageRequest.of(0, 20);
        Page<Stock> page = new PageImpl<>(stocks, pageable, 1);
        
        when(stockService.findAllPaged(0, 20, "id", "asc")).thenReturn(page);

        mockMvc.perform(get("/stocks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stocks").isArray())
                .andExpect(jsonPath("$.stocks[0].symbol").value("AAPL"))
                .andExpect(jsonPath("$.currentPage").value(0))
                .andExpect(jsonPath("$.totalItems").value(1))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.hasNext").value(false))
                .andExpect(jsonPath("$.hasPrevious").value(false));

        verify(stockService).findAllPaged(0, 20, "id", "asc");
    }

    @Test
    void testGetAllStocksWithParameters() throws Exception {
        List<Stock> stocks = Arrays.asList(sampleStock);
        Pageable pageable = PageRequest.of(1, 5);
        Page<Stock> page = new PageImpl<>(stocks, pageable, 1);
        
        when(stockService.findAllPaged(1, 5, "name", "desc")).thenReturn(page);

        mockMvc.perform(get("/stocks")
                .param("page", "1")
                .param("size", "5")
                .param("sortBy", "name")
                .param("sortDirection", "desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stocks").isArray());

        verify(stockService).findAllPaged(1, 5, "name", "desc");
    }

    @Test
    void testGetStockById() throws Exception {
        when(stockService.findById(1L)).thenReturn(Optional.of(sampleStock));

        mockMvc.perform(get("/stocks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.symbol").value("AAPL"))
                .andExpect(jsonPath("$.name").value("Apple Inc."))
                .andExpect(jsonPath("$.currentPrice").value(150.00));

        verify(stockService).findById(1L);
    }

    @Test
    void testGetStockByIdNotFound() throws Exception {
        when(stockService.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/stocks/999"))
                .andExpect(status().isNotFound());

        verify(stockService).findById(999L);
    }

    @Test
    void testGetStockBySymbol() throws Exception {
        when(stockService.findBySymbol("AAPL")).thenReturn(Optional.of(sampleStock));

        mockMvc.perform(get("/stocks/symbol/AAPL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.symbol").value("AAPL"))
                .andExpect(jsonPath("$.name").value("Apple Inc."));

        verify(stockService).findBySymbol("AAPL");
    }

    @Test
    void testGetStockBySymbolNotFound() throws Exception {
        when(stockService.findBySymbol("NONEXISTENT")).thenReturn(Optional.empty());

        mockMvc.perform(get("/stocks/symbol/NONEXISTENT"))
                .andExpect(status().isNotFound());

        verify(stockService).findBySymbol("NONEXISTENT");
    }

    @Test
    void testCreateStock() throws Exception {
        Stock newStock = new Stock();
        newStock.setSymbol("GOOGL");
        newStock.setName("Alphabet Inc.");
        newStock.setCurrentPrice(new BigDecimal("130.00"));

        Stock createdStock = new Stock();
        createdStock.setId(2L);
        createdStock.setSymbol("GOOGL");
        createdStock.setName("Alphabet Inc.");
        createdStock.setCurrentPrice(new BigDecimal("130.00"));

        when(stockService.existsBySymbol("GOOGL")).thenReturn(false);
        when(stockService.create(any(Stock.class))).thenReturn(createdStock);

        mockMvc.perform(post("/stocks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newStock)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/stocks/2"))
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.symbol").value("GOOGL"));

        verify(stockService).existsBySymbol("GOOGL");
        verify(stockService).create(any(Stock.class));
    }

    @Test
    void testCreateStockAlreadyExists() throws Exception {
        Stock newStock = new Stock();
        newStock.setSymbol("AAPL");
        newStock.setName("Apple Inc.");
        newStock.setCurrentPrice(new BigDecimal("150.00"));

        when(stockService.existsBySymbol("AAPL")).thenReturn(true);

        mockMvc.perform(post("/stocks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newStock)))
                .andExpect(status().isConflict());

        verify(stockService).existsBySymbol("AAPL");
        verify(stockService, never()).create(any(Stock.class));
    }

    @Test
    void testCreateStockValidationError() throws Exception {
        Stock invalidStock = new Stock();
        // Missing required fields

        mockMvc.perform(post("/stocks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidStock)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateStock() throws Exception {
        Stock updateData = new Stock();
        updateData.setSymbol("AAPL");
        updateData.setName("Apple Inc. Updated");
        updateData.setCurrentPrice(new BigDecimal("155.00"));

        when(stockService.existsById(1L)).thenReturn(true);
        when(stockService.update(eq(1L), any(Stock.class))).thenReturn(sampleStock);

        mockMvc.perform(put("/stocks/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.symbol").value("AAPL"));

        verify(stockService).existsById(1L);
        verify(stockService).update(eq(1L), any(Stock.class));
    }

    @Test
    void testUpdateStockNotFound() throws Exception {
        Stock updateData = new Stock();
        updateData.setSymbol("TEST");
        updateData.setName("Updated Name");
        updateData.setCurrentPrice(new BigDecimal("100.00"));

        when(stockService.existsById(999L)).thenReturn(false);

        mockMvc.perform(put("/stocks/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isNotFound());

        verify(stockService).existsById(999L);
        verify(stockService, never()).update(anyLong(), any(Stock.class));
    }

    @Test
    void testDeleteStock() throws Exception {
        when(stockService.deleteById(1L)).thenReturn(true);

        mockMvc.perform(delete("/stocks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Stock successfully deleted"))
                .andExpect(jsonPath("$.id").value(1));

        verify(stockService).deleteById(1L);
    }

    @Test
    void testDeleteStockNotFound() throws Exception {
        when(stockService.deleteById(999L)).thenReturn(false);

        mockMvc.perform(delete("/stocks/999"))
                .andExpect(status().isNotFound());

        verify(stockService).deleteById(999L);
    }

    @Test
    void testDeleteStockBySymbol() throws Exception {
        when(stockService.deleteBySymbol("AAPL")).thenReturn(true);

        mockMvc.perform(delete("/stocks/symbol/AAPL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Stock successfully deleted"))
                .andExpect(jsonPath("$.symbol").value("AAPL"));

        verify(stockService).deleteBySymbol("AAPL");
    }

    @Test
    void testUpdateStockPrice() throws Exception {
        when(stockService.updatePrice("AAPL", new BigDecimal("160.00"))).thenReturn(sampleStock);

        mockMvc.perform(patch("/stocks/AAPL/price")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"price\": 160.00}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.symbol").value("AAPL"));

        verify(stockService).updatePrice("AAPL", new BigDecimal("160.00"));
    }

    @Test
    void testUpdateStockPriceInvalidPrice() throws Exception {
        mockMvc.perform(patch("/stocks/AAPL/price")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"price\": 0}"))
                .andExpect(status().isBadRequest());

        verify(stockService, never()).updatePrice(anyString(), any(BigDecimal.class));
    }

    @Test
    void testUpdateStockPriceMissingPrice() throws Exception {
        mockMvc.perform(patch("/stocks/AAPL/price")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());

        verify(stockService, never()).updatePrice(anyString(), any(BigDecimal.class));
    }

    @Test
    void testUpdateStockVolume() throws Exception {
        when(stockService.updateVolume("AAPL", 1500000L)).thenReturn(sampleStock);

        mockMvc.perform(patch("/stocks/AAPL/volume")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"volume\": 1500000}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.symbol").value("AAPL"));

        verify(stockService).updateVolume("AAPL", 1500000L);
    }

    @Test
    void testUpdateStockVolumeInvalidVolume() throws Exception {
        mockMvc.perform(patch("/stocks/AAPL/volume")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"volume\": -1000}"))
                .andExpect(status().isBadRequest());

        verify(stockService, never()).updateVolume(anyString(), anyLong());
    }

    @Test
    void testSearchStocksByName() throws Exception {
        List<Stock> stocks = Arrays.asList(sampleStock);
        when(stockService.searchByName("Apple", 0, 20)).thenReturn(stocks);

        mockMvc.perform(get("/stocks/search")
                .param("name", "Apple"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].symbol").value("AAPL"));

        verify(stockService).searchByName("Apple", 0, 20);
    }

    @Test
    void testSearchStocksBySymbol() throws Exception {
        List<Stock> stocks = Arrays.asList(sampleStock);
        when(stockService.searchBySymbol("AA", 1, 15)).thenReturn(stocks);

        mockMvc.perform(get("/stocks/search")
                .param("symbol", "AA")
                .param("page", "1")
                .param("size", "15"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].symbol").value("AAPL"));

        verify(stockService).searchBySymbol("AA", 1, 15);
    }

    @Test
    void testSearchStocksNoParameters() throws Exception {
        mockMvc.perform(get("/stocks/search"))
                .andExpect(status().isBadRequest());

        verify(stockService, never()).searchByName(anyString(), anyInt(), anyInt());
        verify(stockService, never()).searchBySymbol(anyString(), anyInt(), anyInt());
    }

    @Test
    void testGetStocksBySector() throws Exception {
        List<Stock> stocks = Arrays.asList(sampleStock);
        when(stockService.findBySector("Technology", 0, 20)).thenReturn(stocks);

        mockMvc.perform(get("/stocks/sector/Technology"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].sector").value("Technology"));

        verify(stockService).findBySector("Technology", 0, 20);
    }

    @Test
    void testGetStocksByIndustry() throws Exception {
        List<Stock> stocks = Arrays.asList(sampleStock);
        when(stockService.findByIndustry("Consumer Electronics", 1, 10)).thenReturn(stocks);

        mockMvc.perform(get("/stocks/industry/Consumer Electronics")
                .param("page", "1")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].industry").value("Consumer Electronics"));

        verify(stockService).findByIndustry("Consumer Electronics", 1, 10);
    }

    @Test
    void testGetStocksByPriceRange() throws Exception {
        List<Stock> stocks = Arrays.asList(sampleStock);
        when(stockService.findByPriceRange(new BigDecimal("100.00"), new BigDecimal("200.00"), 0, 20))
            .thenReturn(stocks);

        mockMvc.perform(get("/stocks/price-range")
                .param("minPrice", "100.00")
                .param("maxPrice", "200.00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].symbol").value("AAPL"));

        verify(stockService).findByPriceRange(new BigDecimal("100.00"), new BigDecimal("200.00"), 0, 20);
    }

    @Test
    void testGetStocksByPriceRangeInvalidRange() throws Exception {
        mockMvc.perform(get("/stocks/price-range")
                .param("minPrice", "200.00")
                .param("maxPrice", "100.00"))
                .andExpect(status().isBadRequest());

        verify(stockService, never()).findByPriceRange(any(BigDecimal.class), any(BigDecimal.class), anyInt(), anyInt());
    }

    @Test
    void testGetTopStocksByMarketCap() throws Exception {
        List<Stock> stocks = Arrays.asList(sampleStock);
        when(stockService.findTopByMarketCap(5)).thenReturn(stocks);

        mockMvc.perform(get("/stocks/top/market-cap")
                .param("limit", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].symbol").value("AAPL"));

        verify(stockService).findTopByMarketCap(5);
    }

    @Test
    void testGetTopStocksByVolume() throws Exception {
        List<Stock> stocks = Arrays.asList(sampleStock);
        when(stockService.findTopByVolume(10)).thenReturn(stocks);

        mockMvc.perform(get("/stocks/top/volume"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].symbol").value("AAPL"));

        verify(stockService).findTopByVolume(10);
    }

    @Test
    void testGetHighPerformers() throws Exception {
        List<Stock> stocks = Arrays.asList(sampleStock);
        when(stockService.getHighPerformers(15)).thenReturn(stocks);

        mockMvc.perform(get("/stocks/performers/high")
                .param("limit", "15"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].symbol").value("AAPL"));

        verify(stockService).getHighPerformers(15);
    }

    @Test
    void testGetLowPerformers() throws Exception {
        List<Stock> stocks = Arrays.asList(sampleStock);
        when(stockService.getLowPerformers(8)).thenReturn(stocks);

        mockMvc.perform(get("/stocks/performers/low")
                .param("limit", "8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].symbol").value("AAPL"));

        verify(stockService).getLowPerformers(8);
    }

    @Test
    void testGetValueStocks() throws Exception {
        List<Stock> stocks = Arrays.asList(sampleStock);
        when(stockService.getValueStocks(new BigDecimal("20.0"), 12)).thenReturn(stocks);

        mockMvc.perform(get("/stocks/value-stocks")
                .param("maxPeRatio", "20.0")
                .param("limit", "12"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].symbol").value("AAPL"));

        verify(stockService).getValueStocks(new BigDecimal("20.0"), 12);
    }

    @Test
    void testGetDividendStocks() throws Exception {
        List<Stock> stocks = Arrays.asList(sampleStock);
        when(stockService.getDividendStocks(new BigDecimal("0.03"), 7)).thenReturn(stocks);

        mockMvc.perform(get("/stocks/dividend-stocks")
                .param("minDividendYield", "0.03")
                .param("limit", "7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].symbol").value("AAPL"));

        verify(stockService).getDividendStocks(new BigDecimal("0.03"), 7);
    }

    @Test
    void testGetDistinctSectors() throws Exception {
        List<String> sectors = Arrays.asList("Technology", "Finance");
        when(stockService.getDistinctSectors()).thenReturn(sectors);

        mockMvc.perform(get("/stocks/sectors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0]").value("Technology"))
                .andExpect(jsonPath("$[1]").value("Finance"));

        verify(stockService).getDistinctSectors();
    }

    @Test
    void testGetDistinctIndustries() throws Exception {
        List<String> industries = Arrays.asList("Consumer Electronics", "Banking");
        when(stockService.getDistinctIndustries()).thenReturn(industries);

        mockMvc.perform(get("/stocks/industries"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0]").value("Consumer Electronics"))
                .andExpect(jsonPath("$[1]").value("Banking"));

        verify(stockService).getDistinctIndustries();
    }

    @Test
    void testGetStockStats() throws Exception {
        when(stockService.getTotalCount()).thenReturn(100L);
        when(stockService.getDistinctSectors()).thenReturn(Arrays.asList("Technology", "Finance"));
        when(stockService.getDistinctIndustries()).thenReturn(Arrays.asList("Software", "Banking"));

        mockMvc.perform(get("/stocks/stats/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalStocks").value(100))
                .andExpect(jsonPath("$.totalSectors").value(2))
                .andExpect(jsonPath("$.totalIndustries").value(2))
                .andExpect(jsonPath("$.sectors").isArray())
                .andExpect(jsonPath("$.industries").isArray());

        verify(stockService).getTotalCount();
        verify(stockService).getDistinctSectors();
        verify(stockService).getDistinctIndustries();
    }

    @Test
    void testCheckStockExists() throws Exception {
        when(stockService.existsBySymbol("AAPL")).thenReturn(true);

        mockMvc.perform(get("/stocks/exists/symbol/AAPL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exists").value(true));

        verify(stockService).existsBySymbol("AAPL");
    }

    @Test
    void testCreateStocksBatch() throws Exception {
        Stock stock1 = new Stock("GOOGL", "Alphabet", new BigDecimal("130.00"));
        Stock stock2 = new Stock("MSFT", "Microsoft", new BigDecimal("300.00"));
        List<Stock> inputStocks = Arrays.asList(stock1, stock2);

        Stock created1 = new Stock("GOOGL", "Alphabet", new BigDecimal("130.00"));
        created1.setId(1L);
        Stock created2 = new Stock("MSFT", "Microsoft", new BigDecimal("300.00"));
        created2.setId(2L);
        List<Stock> createdStocks = Arrays.asList(created1, created2);

        when(stockService.existsBySymbol("GOOGL")).thenReturn(false);
        when(stockService.existsBySymbol("MSFT")).thenReturn(false);
        when(stockService.create(any(Stock.class)))
            .thenReturn(created1)
            .thenReturn(created2);

        mockMvc.perform(post("/stocks/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputStocks)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.created").value(2))
                .andExpect(jsonPath("$.skipped").value(0))
                .andExpect(jsonPath("$.stocks").isArray())
                .andExpect(jsonPath("$.stocks[0].symbol").value("GOOGL"))
                .andExpect(jsonPath("$.stocks[1].symbol").value("MSFT"));

        verify(stockService).existsBySymbol("GOOGL");
        verify(stockService).existsBySymbol("MSFT");
        verify(stockService, times(2)).create(any(Stock.class));
    }

    @Test
    void testCreateStocksBatchWithDuplicates() throws Exception {
        Stock stock1 = new Stock("AAPL", "Apple", new BigDecimal("150.00"));
        Stock stock2 = new Stock("GOOGL", "Alphabet", new BigDecimal("130.00"));
        List<Stock> inputStocks = Arrays.asList(stock1, stock2);

        Stock created2 = new Stock("GOOGL", "Alphabet", new BigDecimal("130.00"));
        created2.setId(2L);

        when(stockService.existsBySymbol("AAPL")).thenReturn(true); // Already exists
        when(stockService.existsBySymbol("GOOGL")).thenReturn(false);
        when(stockService.create(any(Stock.class))).thenReturn(created2);

        mockMvc.perform(post("/stocks/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputStocks)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.created").value(1))
                .andExpect(jsonPath("$.skipped").value(1))
                .andExpect(jsonPath("$.stocks").isArray())
                .andExpect(jsonPath("$.stocks[0].symbol").value("GOOGL"));

        verify(stockService).existsBySymbol("AAPL");
        verify(stockService).existsBySymbol("GOOGL");
        verify(stockService, times(1)).create(any(Stock.class));
    }

    @Test
    void testHealthCheck() throws Exception {
        mockMvc.perform(get("/stocks/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.service").value("Stock API"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void testInvalidPageParameter() throws Exception {
        mockMvc.perform(get("/stocks")
                .param("page", "-1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testInvalidSizeParameter() throws Exception {
        mockMvc.perform(get("/stocks")
                .param("size", "0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testInvalidLimitParameter() throws Exception {
        mockMvc.perform(get("/stocks/top/market-cap")
                .param("limit", "0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testInvalidIdParameter() throws Exception {
        mockMvc.perform(get("/stocks/0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testValidationConstraintsOnCreate() throws Exception {
        Stock invalidStock = new Stock();
        invalidStock.setSymbol(""); // Blank symbol
        invalidStock.setName(""); // Blank name
        // Missing currentPrice

        mockMvc.perform(post("/stocks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidStock)))
                .andExpect(status().isBadRequest());

        verify(stockService, never()).create(any(Stock.class));
    }

    @Test
    void testValidationConstraintsOnUpdate() throws Exception {
        Stock invalidStock = new Stock();
        invalidStock.setSymbol(""); // Blank symbol
        invalidStock.setCurrentPrice(new BigDecimal("-10.00")); // Negative price

        mockMvc.perform(put("/stocks/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidStock)))
                .andExpect(status().isBadRequest());

        verify(stockService, never()).update(anyLong(), any(Stock.class));
    }

    @Test
    void testSearchWithEmptyParameters() throws Exception {
        mockMvc.perform(get("/stocks/search")
                .param("name", "")
                .param("symbol", ""))
                .andExpect(status().isBadRequest());

        verify(stockService, never()).searchByName(anyString(), anyInt(), anyInt());
        verify(stockService, never()).searchBySymbol(anyString(), anyInt(), anyInt());
    }

    @Test
    void testSearchWithWhitespaceParameters() throws Exception {
        mockMvc.perform(get("/stocks/search")
                .param("name", "   "))
                .andExpect(status().isBadRequest());

        verify(stockService, never()).searchByName(anyString(), anyInt(), anyInt());
    }
}