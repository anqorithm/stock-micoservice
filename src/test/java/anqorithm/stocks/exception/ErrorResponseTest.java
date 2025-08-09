package anqorithm.stocks.exception;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ErrorResponseTest {

    @Test
    void testDefaultConstructor() {
        ErrorResponse errorResponse = new ErrorResponse();
        
        assertNotNull(errorResponse.getTimestamp());
        assertTrue(errorResponse.getTimestamp().isBefore(LocalDateTime.now().plusSeconds(1)));
        assertTrue(errorResponse.getTimestamp().isAfter(LocalDateTime.now().minusSeconds(1)));
        
        assertEquals(0, errorResponse.getStatus());
        assertNull(errorResponse.getError());
        assertNull(errorResponse.getMessage());
        assertNull(errorResponse.getPath());
        assertNull(errorResponse.getDetails());
    }

    @Test
    void testConstructorWithBasicFields() {
        int status = 404;
        String error = "Not Found";
        String message = "Resource not found";
        String path = "/api/stocks/123";
        
        ErrorResponse errorResponse = new ErrorResponse(status, error, message, path);
        
        assertEquals(status, errorResponse.getStatus());
        assertEquals(error, errorResponse.getError());
        assertEquals(message, errorResponse.getMessage());
        assertEquals(path, errorResponse.getPath());
        assertNull(errorResponse.getDetails());
        assertNotNull(errorResponse.getTimestamp());
    }

    @Test
    void testConstructorWithDetails() {
        int status = 400;
        String error = "Validation Error";
        String message = "Input validation failed";
        String path = "/api/stocks";
        List<String> details = Arrays.asList("Name is required", "Price must be positive");
        
        ErrorResponse errorResponse = new ErrorResponse(status, error, message, path, details);
        
        assertEquals(status, errorResponse.getStatus());
        assertEquals(error, errorResponse.getError());
        assertEquals(message, errorResponse.getMessage());
        assertEquals(path, errorResponse.getPath());
        assertEquals(details, errorResponse.getDetails());
        assertNotNull(errorResponse.getTimestamp());
    }

    @Test
    void testSettersAndGetters() {
        ErrorResponse errorResponse = new ErrorResponse();
        
        int status = 500;
        String error = "Internal Server Error";
        String message = "Something went wrong";
        String path = "/api/stocks/create";
        List<String> details = Arrays.asList("Database connection failed");
        LocalDateTime timestamp = LocalDateTime.now();
        
        errorResponse.setStatus(status);
        errorResponse.setError(error);
        errorResponse.setMessage(message);
        errorResponse.setPath(path);
        errorResponse.setDetails(details);
        errorResponse.setTimestamp(timestamp);
        
        assertEquals(status, errorResponse.getStatus());
        assertEquals(error, errorResponse.getError());
        assertEquals(message, errorResponse.getMessage());
        assertEquals(path, errorResponse.getPath());
        assertEquals(details, errorResponse.getDetails());
        assertEquals(timestamp, errorResponse.getTimestamp());
    }

    @Test
    void testWithNullValues() {
        ErrorResponse errorResponse = new ErrorResponse(0, null, null, null, null);
        
        assertEquals(0, errorResponse.getStatus());
        assertNull(errorResponse.getError());
        assertNull(errorResponse.getMessage());
        assertNull(errorResponse.getPath());
        assertNull(errorResponse.getDetails());
        assertNotNull(errorResponse.getTimestamp());
    }

    @Test
    void testWithEmptyDetails() {
        List<String> emptyDetails = Arrays.asList();
        ErrorResponse errorResponse = new ErrorResponse(400, "Bad Request", "Invalid input", "/test", emptyDetails);
        
        assertEquals(emptyDetails, errorResponse.getDetails());
        assertTrue(errorResponse.getDetails().isEmpty());
    }

    @Test
    void testTimestampIsSetAutomatically() {
        LocalDateTime beforeCreation = LocalDateTime.now();
        ErrorResponse errorResponse = new ErrorResponse();
        LocalDateTime afterCreation = LocalDateTime.now();
        
        assertNotNull(errorResponse.getTimestamp());
        assertFalse(errorResponse.getTimestamp().isBefore(beforeCreation));
        assertFalse(errorResponse.getTimestamp().isAfter(afterCreation));
    }

    @Test
    void testTimestampCanBeOverridden() {
        ErrorResponse errorResponse = new ErrorResponse();
        LocalDateTime originalTimestamp = errorResponse.getTimestamp();
        LocalDateTime newTimestamp = LocalDateTime.now().minusHours(1);
        
        errorResponse.setTimestamp(newTimestamp);
        
        assertEquals(newTimestamp, errorResponse.getTimestamp());
        assertNotEquals(originalTimestamp, errorResponse.getTimestamp());
    }

    @Test
    void testMultipleDetails() {
        List<String> details = Arrays.asList(
            "Name field is required and cannot be empty",
            "Price must be greater than 0",
            "Symbol must contain only uppercase letters",
            "Market cap must be non-negative"
        );
        
        ErrorResponse errorResponse = new ErrorResponse(400, "Validation Failed", "Multiple validation errors", "/api/stocks", details);
        
        assertEquals(4, errorResponse.getDetails().size());
        assertTrue(errorResponse.getDetails().contains("Name field is required and cannot be empty"));
        assertTrue(errorResponse.getDetails().contains("Price must be greater than 0"));
        assertTrue(errorResponse.getDetails().contains("Symbol must contain only uppercase letters"));
        assertTrue(errorResponse.getDetails().contains("Market cap must be non-negative"));
    }

    @Test
    void testErrorResponseImmutabilityOfDetails() {
        List<String> originalDetails = Arrays.asList("Error 1", "Error 2");
        ErrorResponse errorResponse = new ErrorResponse(400, "Bad Request", "Test", "/test", originalDetails);
        
        List<String> retrievedDetails = errorResponse.getDetails();
        
        // The retrieved details should be the same reference or same content
        assertEquals(originalDetails.size(), retrievedDetails.size());
        assertEquals("Error 1", retrievedDetails.get(0));
        assertEquals("Error 2", retrievedDetails.get(1));
    }
}