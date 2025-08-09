package anqorithm.stocks.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StockNotFoundExceptionTest {

    @Test
    void testConstructorWithMessage() {
        String message = "Stock not found";
        StockNotFoundException exception = new StockNotFoundException(message);
        
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testConstructorWithMessageAndCause() {
        String message = "Stock not found";
        RuntimeException cause = new RuntimeException("Database error");
        StockNotFoundException exception = new StockNotFoundException(message, cause);
        
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testByIdStaticMethod() {
        Long id = 123L;
        StockNotFoundException exception = StockNotFoundException.byId(id);
        
        assertEquals("Stock not found with id: 123", exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testBySymbolStaticMethod() {
        String symbol = "AAPL";
        StockNotFoundException exception = StockNotFoundException.bySymbol(symbol);
        
        assertEquals("Stock not found with symbol: AAPL", exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testByIdWithNullId() {
        StockNotFoundException exception = StockNotFoundException.byId(null);
        
        assertEquals("Stock not found with id: null", exception.getMessage());
    }

    @Test
    void testBySymbolWithNullSymbol() {
        StockNotFoundException exception = StockNotFoundException.bySymbol(null);
        
        assertEquals("Stock not found with symbol: null", exception.getMessage());
    }

    @Test
    void testExceptionIsRuntimeException() {
        StockNotFoundException exception = new StockNotFoundException("test");
        
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    void testExceptionCanBeThrown() {
        assertThrows(StockNotFoundException.class, () -> {
            throw new StockNotFoundException("Test exception");
        });
    }

    @Test
    void testExceptionWithEmptyMessage() {
        StockNotFoundException exception = new StockNotFoundException("");
        
        assertEquals("", exception.getMessage());
    }
}