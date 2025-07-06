package net.rushhourgame.core.exceptions;

/**
 * バリデーションエラーの例外
 */
public class ValidationException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    
    public ValidationException(String message) {
        super(message);
    }
    
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public ValidationException(String fieldName, String value, String reason) {
        super(String.format("Validation failed for field '%s' with value '%s': %s", fieldName, value, reason));
    }
}