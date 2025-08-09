package anqorithm.stocks.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Mock
    private WebRequest webRequest;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private ConstraintViolation<Object> constraintViolation;

    @BeforeEach
    void setUp() {
        when(webRequest.getDescription(false)).thenReturn("uri=/api/v1/stocks/123");
    }

    @Test
    void testHandleStockNotFound() {
        StockNotFoundException exception = new StockNotFoundException("Stock not found with id: 123");
        
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleStockNotFound(exception, webRequest);
        
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        ErrorResponse errorResponse = response.getBody();
        assertNotNull(errorResponse);
        assertEquals(404, errorResponse.getStatus());
        assertEquals("Stock Not Found", errorResponse.getError());
        assertEquals("Stock not found with id: 123", errorResponse.getMessage());
        assertEquals("/api/v1/stocks/123", errorResponse.getPath());
        assertNotNull(errorResponse.getTimestamp());
    }

    @Test
    void testHandleStockAlreadyExists() {
        StockAlreadyExistsException exception = new StockAlreadyExistsException("Stock already exists with symbol: AAPL");
        
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleStockAlreadyExists(exception, webRequest);
        
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        ErrorResponse errorResponse = response.getBody();
        assertNotNull(errorResponse);
        assertEquals(409, errorResponse.getStatus());
        assertEquals("Stock Already Exists", errorResponse.getError());
        assertEquals("Stock already exists with symbol: AAPL", errorResponse.getMessage());
        assertEquals("/api/v1/stocks/123", errorResponse.getPath());
    }

    @Test
    void testHandleEntityNotFound() {
        EntityNotFoundException exception = new EntityNotFoundException("Entity not found");
        
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleEntityNotFound(exception, webRequest);
        
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        ErrorResponse errorResponse = response.getBody();
        assertNotNull(errorResponse);
        assertEquals(404, errorResponse.getStatus());
        assertEquals("Entity Not Found", errorResponse.getError());
        assertEquals("Entity not found", errorResponse.getMessage());
    }

    @Test
    void testHandleMethodArgumentNotValidException() {
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        
        FieldError fieldError1 = new FieldError("stock", "symbol", "Symbol is required");
        FieldError fieldError2 = new FieldError("stock", "price", "Price must be positive");
        List<FieldError> fieldErrors = Arrays.asList(fieldError1, fieldError2);
        
        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(fieldErrors);
        when(exception.getMessage()).thenReturn("Validation failed");
        
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleValidationExceptions(exception, webRequest);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorResponse errorResponse = response.getBody();
        assertNotNull(errorResponse);
        assertEquals(400, errorResponse.getStatus());
        assertEquals("Validation Failed", errorResponse.getError());
        assertEquals("Input validation failed", errorResponse.getMessage());
        
        List<String> details = errorResponse.getDetails();
        assertNotNull(details);
        assertEquals(2, details.size());
        assertTrue(details.contains("symbol: Symbol is required"));
        assertTrue(details.contains("price: Price must be positive"));
    }

    @Test
    void testHandleConstraintViolationException() {
        Set<ConstraintViolation<Object>> violations = new HashSet<>();
        violations.add(constraintViolation);
        
        jakarta.validation.Path mockPath = mock(jakarta.validation.Path.class);
        when(mockPath.toString()).thenReturn("currentPrice");
        when(constraintViolation.getPropertyPath()).thenReturn(mockPath);
        when(constraintViolation.getMessage()).thenReturn("must be greater than 0");
        
        ConstraintViolationException exception = new ConstraintViolationException("Constraint violation", violations);
        
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleConstraintViolationException(exception, webRequest);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorResponse errorResponse = response.getBody();
        assertNotNull(errorResponse);
        assertEquals(400, errorResponse.getStatus());
        assertEquals("Constraint Violation", errorResponse.getError());
        assertEquals("Data constraint violation", errorResponse.getMessage());
        
        List<String> details = errorResponse.getDetails();
        assertNotNull(details);
        assertEquals(1, details.size());
        assertTrue(details.contains("currentPrice: must be greater than 0"));
    }

    @Test
    void testHandleDataIntegrityViolationWithDuplicateKey() {
        DataIntegrityViolationException exception = new DataIntegrityViolationException("duplicate key value violates unique constraint");
        
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleDataIntegrityViolation(exception, webRequest);
        
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        ErrorResponse errorResponse = response.getBody();
        assertNotNull(errorResponse);
        assertEquals(409, errorResponse.getStatus());
        assertEquals("Data Integrity Violation", errorResponse.getError());
        assertEquals("Duplicate entry detected. The resource already exists.", errorResponse.getMessage());
    }

    @Test
    void testHandleDataIntegrityViolationWithForeignKey() {
        DataIntegrityViolationException exception = new DataIntegrityViolationException("foreign key constraint fails");
        
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleDataIntegrityViolation(exception, webRequest);
        
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        ErrorResponse errorResponse = response.getBody();
        assertNotNull(errorResponse);
        assertEquals("Referenced entity does not exist.", errorResponse.getMessage());
    }

    @Test
    void testHandleDataIntegrityViolationWithNotNull() {
        DataIntegrityViolationException exception = new DataIntegrityViolationException("not-null constraint violation");
        
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleDataIntegrityViolation(exception, webRequest);
        
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        ErrorResponse errorResponse = response.getBody();
        assertNotNull(errorResponse);
        assertEquals("Required field is missing.", errorResponse.getMessage());
    }

    @Test
    void testHandleDataIntegrityViolationGeneric() {
        DataIntegrityViolationException exception = new DataIntegrityViolationException("Some other constraint violation");
        
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleDataIntegrityViolation(exception, webRequest);
        
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        ErrorResponse errorResponse = response.getBody();
        assertNotNull(errorResponse);
        assertEquals("Data integrity constraint violated", errorResponse.getMessage());
    }

    @Test
    void testHandleIllegalArgumentException() {
        IllegalArgumentException exception = new IllegalArgumentException("Invalid argument provided");
        
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleIllegalArgumentException(exception, webRequest);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorResponse errorResponse = response.getBody();
        assertNotNull(errorResponse);
        assertEquals(400, errorResponse.getStatus());
        assertEquals("Invalid Argument", errorResponse.getError());
        assertEquals("Invalid argument provided", errorResponse.getMessage());
    }

    @Test
    void testHandleMethodArgumentTypeMismatchException() {
        MethodArgumentTypeMismatchException exception = mock(MethodArgumentTypeMismatchException.class);
        
        when(exception.getValue()).thenReturn("invalid");
        when(exception.getName()).thenReturn("id");
        when(exception.getRequiredType()).thenReturn((Class) Long.class);
        when(exception.getMessage()).thenReturn("Type mismatch");
        
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleTypeMismatchException(exception, webRequest);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorResponse errorResponse = response.getBody();
        assertNotNull(errorResponse);
        assertEquals(400, errorResponse.getStatus());
        assertEquals("Type Mismatch", errorResponse.getError());
        assertEquals("Invalid value 'invalid' for parameter 'id'. Expected type: Long", errorResponse.getMessage());
    }

    @Test
    void testHandleHttpMessageNotReadableException() {
        HttpMessageNotReadableException exception = mock(HttpMessageNotReadableException.class);
        when(exception.getMessage()).thenReturn("JSON parse error");
        
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleHttpMessageNotReadableException(exception, webRequest);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorResponse errorResponse = response.getBody();
        assertNotNull(errorResponse);
        assertEquals(400, errorResponse.getStatus());
        assertEquals("Malformed JSON", errorResponse.getError());
        assertEquals("Request body contains invalid JSON", errorResponse.getMessage());
    }

    @Test
    void testHandleSQLException() {
        SQLException exception = new SQLException("Database connection failed");
        
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleSQLException(exception, webRequest);
        
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        ErrorResponse errorResponse = response.getBody();
        assertNotNull(errorResponse);
        assertEquals(500, errorResponse.getStatus());
        assertEquals("Database Error", errorResponse.getError());
        assertEquals("A database error occurred. Please try again later.", errorResponse.getMessage());
    }

    @Test
    void testHandleGenericException() {
        RuntimeException exception = new RuntimeException("Unexpected error occurred");
        
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleGenericException(exception, webRequest);
        
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        ErrorResponse errorResponse = response.getBody();
        assertNotNull(errorResponse);
        assertEquals(500, errorResponse.getStatus());
        assertEquals("Internal Server Error", errorResponse.getError());
        assertEquals("An unexpected error occurred. Please try again later.", errorResponse.getMessage());
    }

    @Test
    void testUriProcessing() {
        when(webRequest.getDescription(false)).thenReturn("uri=/api/v1/stocks/create");
        
        StockNotFoundException exception = new StockNotFoundException("Test");
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleStockNotFound(exception, webRequest);
        
        ErrorResponse errorResponse = response.getBody();
        assertNotNull(errorResponse);
        assertEquals("/api/v1/stocks/create", errorResponse.getPath());
    }

    @Test
    void testEmptyValidationErrors() {
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        
        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(Arrays.asList());
        when(exception.getMessage()).thenReturn("Validation failed");
        
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleValidationExceptions(exception, webRequest);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorResponse errorResponse = response.getBody();
        assertNotNull(errorResponse);
        assertNotNull(errorResponse.getDetails());
        assertTrue(errorResponse.getDetails().isEmpty());
    }

    @Test
    void testEmptyConstraintViolations() {
        Set<ConstraintViolation<Object>> violations = new HashSet<>();
        ConstraintViolationException exception = new ConstraintViolationException("No violations", violations);
        
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleConstraintViolationException(exception, webRequest);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorResponse errorResponse = response.getBody();
        assertNotNull(errorResponse);
        assertNotNull(errorResponse.getDetails());
        assertTrue(errorResponse.getDetails().isEmpty());
    }

    @Test
    void testDataIntegrityViolationWithNullMessage() {
        DataIntegrityViolationException exception = new DataIntegrityViolationException("Test", new RuntimeException());
        
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleDataIntegrityViolation(exception, webRequest);
        
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        ErrorResponse errorResponse = response.getBody();
        assertNotNull(errorResponse);
        assertEquals("Data integrity constraint violated", errorResponse.getMessage());
    }
}