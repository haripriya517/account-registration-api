package com.alexa.account.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("GlobalExceptionHandler Unit Tests")
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    @DisplayName("Should handle ResourceNotFoundException with 404 status")
    void testHandleResourceNotFoundException_Returns404() {
        // Arrange
        String errorMessage = "Account request not found with id: TEST-123";
        ResourceNotFoundException exception = new ResourceNotFoundException(errorMessage);

        // Act
        ResponseEntity<Map<String, Object>> response = handler.handleResourceNotFoundException(exception);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().get("status"));
        assertEquals("Not Found", response.getBody().get("error"));
        assertEquals(errorMessage, response.getBody().get("message"));
        assertNotNull(response.getBody().get("timestamp"));
    }

    @Test
    @DisplayName("Should handle InvalidRequestException with 400 status")
    void testHandleInvalidRequestException_Returns400() {
        // Arrange
        String errorMessage = "ID document is mandatory";
        InvalidRequestException exception = new InvalidRequestException(errorMessage);

        // Act
        ResponseEntity<Map<String, Object>> response = handler.handleInvalidRequestException(exception);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().get("status"));
        assertEquals("Bad Request", response.getBody().get("error"));
        assertEquals(errorMessage, response.getBody().get("message"));
        assertNotNull(response.getBody().get("timestamp"));
    }

    @Test
    @DisplayName("Should handle generic Exception with 500 status")
    void testHandleGenericException_Returns500() {
        // Arrange
        String errorMessage = "Unexpected error occurred";
        Exception exception = new Exception(errorMessage);

        // Act
        ResponseEntity<Map<String, Object>> response = handler.handleGenericException(exception);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(500, response.getBody().get("status"));
        assertEquals("Internal Server Error", response.getBody().get("error"));
        assertEquals(errorMessage, response.getBody().get("message"));
        assertNotNull(response.getBody().get("timestamp"));
    }

    @Test
    @DisplayName("Should handle MethodArgumentNotValidException with field errors")
    void testHandleValidationExceptions_WithFieldErrors() {
        // Arrange
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);

        FieldError fieldError1 = new FieldError("accountRequestDTO", "name", "Name is mandatory");
        FieldError fieldError2 = new FieldError("accountRequestDTO", "dateOfBirth", "Date of birth is mandatory");

        List<FieldError> fieldErrors = Arrays.asList(fieldError1, fieldError2);

        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(fieldErrors);

        // Act
        ResponseEntity<Map<String, Object>> response = handler.handleValidationExceptions(exception);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().get("status"));
        assertEquals("Validation Failed", response.getBody().get("error"));
        assertNotNull(response.getBody().get("timestamp"));
        assertNotNull(response.getBody().get("fieldErrors"));

        @SuppressWarnings("unchecked")
        Map<String, String> errors = (Map<String, String>) response.getBody().get("fieldErrors");
        assertEquals(2, errors.size());
        assertEquals("Name is mandatory", errors.get("name"));
        assertEquals("Date of birth is mandatory", errors.get("dateOfBirth"));
    }

    @Test
    @DisplayName("Should handle MethodArgumentNotValidException with single field error")
    void testHandleValidationExceptions_WithSingleFieldError() {
        // Arrange
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);

        FieldError fieldError = new FieldError("accountRequestDTO", "email", "Email must be valid");
        List<FieldError> fieldErrors = Arrays.asList(fieldError);

        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(fieldErrors);

        // Act
        ResponseEntity<Map<String, Object>> response = handler.handleValidationExceptions(exception);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, String> errors = (Map<String, String>) response.getBody().get("fieldErrors");
        assertEquals(1, errors.size());
        assertEquals("Email must be valid", errors.get("email"));
    }

    @Test
    @DisplayName("Should handle MethodArgumentNotValidException with empty field errors")
    void testHandleValidationExceptions_WithEmptyFieldErrors() {
        // Arrange
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);

        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(Arrays.asList());

        // Act
        ResponseEntity<Map<String, Object>> response = handler.handleValidationExceptions(exception);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, String> errors = (Map<String, String>) response.getBody().get("fieldErrors");
        assertTrue(errors.isEmpty());
    }

    @Test
    @DisplayName("Should handle MethodArgumentNotValidException with multiple errors for same field")
    void testHandleValidationExceptions_WithMultipleErrorsSameField() {
        // Arrange
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);

        FieldError fieldError1 = new FieldError("accountRequestDTO", "email", "Email is mandatory");
        FieldError fieldError2 = new FieldError("accountRequestDTO", "email", "Email must be valid");

        List<FieldError> fieldErrors = Arrays.asList(fieldError1, fieldError2);

        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(fieldErrors);

        // Act
        ResponseEntity<Map<String, Object>> response = handler.handleValidationExceptions(exception);

        // Assert
        assertNotNull(response);
        @SuppressWarnings("unchecked")
        Map<String, String> errors = (Map<String, String>) response.getBody().get("fieldErrors");
        assertEquals(1, errors.size());
        assertTrue(errors.containsKey("email"));
    }

    @Test
    @DisplayName("Should include all required fields in ResourceNotFoundException response")
    void testHandleResourceNotFoundException_IncludesAllFields() {
        // Arrange
        ResourceNotFoundException exception = new ResourceNotFoundException("Resource not found");

        // Act
        ResponseEntity<Map<String, Object>> response = handler.handleResourceNotFoundException(exception);

        // Assert
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertTrue(body.containsKey("timestamp"));
        assertTrue(body.containsKey("status"));
        assertTrue(body.containsKey("error"));
        assertTrue(body.containsKey("message"));
        assertEquals(4, body.size());
    }

    @Test
    @DisplayName("Should include all required fields in InvalidRequestException response")
    void testHandleInvalidRequestException_IncludesAllFields() {
        // Arrange
        InvalidRequestException exception = new InvalidRequestException("Invalid request");

        // Act
        ResponseEntity<Map<String, Object>> response = handler.handleInvalidRequestException(exception);

        // Assert
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertTrue(body.containsKey("timestamp"));
        assertTrue(body.containsKey("status"));
        assertTrue(body.containsKey("error"));
        assertTrue(body.containsKey("message"));
        assertEquals(4, body.size());
    }

    @Test
    @DisplayName("Should include all required fields in generic Exception response")
    void testHandleGenericException_IncludesAllFields() {
        // Arrange
        Exception exception = new Exception("Generic error");

        // Act
        ResponseEntity<Map<String, Object>> response = handler.handleGenericException(exception);

        // Assert
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertTrue(body.containsKey("timestamp"));
        assertTrue(body.containsKey("status"));
        assertTrue(body.containsKey("error"));
        assertTrue(body.containsKey("message"));
        assertEquals(4, body.size());
    }

    @Test
    @DisplayName("Should include all required fields in validation exception response")
    void testHandleValidationExceptions_IncludesAllFields() {
        // Arrange
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);

        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(Arrays.asList());

        // Act
        ResponseEntity<Map<String, Object>> response = handler.handleValidationExceptions(exception);

        // Assert
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertTrue(body.containsKey("timestamp"));
        assertTrue(body.containsKey("status"));
        assertTrue(body.containsKey("error"));
        assertTrue(body.containsKey("fieldErrors"));
        assertEquals(4, body.size());
    }

    @Test
    @DisplayName("Should handle RuntimeException as generic exception")
    void testHandleGenericException_WithRuntimeException() {
        // Arrange
        RuntimeException exception = new RuntimeException("Runtime error");

        // Act
        ResponseEntity<Map<String, Object>> response = handler.handleGenericException(exception);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Runtime error", response.getBody().get("message"));
    }

    @Test
    @DisplayName("Should handle NullPointerException as generic exception")
    void testHandleGenericException_WithNullPointerException() {
        // Arrange
        NullPointerException exception = new NullPointerException("Null pointer");

        // Act
        ResponseEntity<Map<String, Object>> response = handler.handleGenericException(exception);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(500, response.getBody().get("status"));
    }

    @Test
    @DisplayName("Should handle exception with null message")
    void testHandleGenericException_WithNullMessage() {
        // Arrange
        Exception exception = new Exception((String) null);

        // Act
        ResponseEntity<Map<String, Object>> response = handler.handleGenericException(exception);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody().get("message"));
    }

    @Test
    @DisplayName("Should handle ResourceNotFoundException with empty message")
    void testHandleResourceNotFoundException_WithEmptyMessage() {
        // Arrange
        ResourceNotFoundException exception = new ResourceNotFoundException("");

        // Act
        ResponseEntity<Map<String, Object>> response = handler.handleResourceNotFoundException(exception);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("", response.getBody().get("message"));
    }

    @Test
    @DisplayName("Should handle InvalidRequestException with special characters in message")
    void testHandleInvalidRequestException_WithSpecialCharacters() {
        // Arrange
        String specialMessage = "Invalid request: <script>alert('test')</script>";
        InvalidRequestException exception = new InvalidRequestException(specialMessage);

        // Act
        ResponseEntity<Map<String, Object>> response = handler.handleInvalidRequestException(exception);

        // Assert
        assertNotNull(response);
        assertEquals(specialMessage, response.getBody().get("message"));
    }
}

