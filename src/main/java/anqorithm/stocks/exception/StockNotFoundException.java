package anqorithm.stocks.exception;

public class StockNotFoundException extends RuntimeException {
    
    public StockNotFoundException(String message) {
        super(message);
    }
    
    public StockNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public static StockNotFoundException byId(Long id) {
        return new StockNotFoundException("Stock not found with id: " + id);
    }
    
    public static StockNotFoundException bySymbol(String symbol) {
        return new StockNotFoundException("Stock not found with symbol: " + symbol);
    }
}