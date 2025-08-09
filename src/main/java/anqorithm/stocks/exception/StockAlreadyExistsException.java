package anqorithm.stocks.exception;

public class StockAlreadyExistsException extends RuntimeException {
    
    public StockAlreadyExistsException(String message) {
        super(message);
    }
    
    public StockAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public static StockAlreadyExistsException bySymbol(String symbol) {
        return new StockAlreadyExistsException("Stock already exists with symbol: " + symbol);
    }
}