package anqorithm.stocks.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StockAlreadyExistsExceptionTest {

    @Test
    void testConstructorWithMessage() {
        String message = "Stock already exists";
        StockAlreadyExistsException exception = new StockAlreadyExistsException(message);
        
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testConstructorWithMessageAndCause() {
        String message = "Stock already exists";
        RuntimeException cause = new RuntimeException("Database constraint violation");
        StockAlreadyExistsException exception = new StockAlreadyExistsException(message, cause);
        
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testBySymbolStaticMethod() {
        String symbol = "AAPL";
        StockAlreadyExistsException exception = StockAlreadyExistsException.bySymbol(symbol);
        
        assertEquals("Stock already exists with symbol: AAPL", exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testBySymbolWithNullSymbol() {
        StockAlreadyExistsException exception = StockAlreadyExistsException.bySymbol(null);
        
        assertEquals("Stock already exists with symbol: null", exception.getMessage());
    }

    @Test
    void testExceptionIsRuntimeException() {
        StockAlreadyExistsException exception = new StockAlreadyExistsException("test");
        
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    void testExceptionCanBeThrown() {
        assertThrows(StockAlreadyExistsException.class, () -> {
            throw new StockAlreadyExistsException("Test exception");
        });
    }

    @Test
    void testExceptionWithEmptyMessage() {
        StockAlreadyExistsException exception = new StockAlreadyExistsException("");
        
        assertEquals("", exception.getMessage());
    }

    @Test
    void testBySymbolWithEmptySymbol() {
        StockAlreadyExistsException exception = StockAlreadyExistsException.bySymbol("");
        
        assertEquals("Stock already exists with symbol: ", exception.getMessage());
    }
}