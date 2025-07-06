package net.rushhourgame.core.exceptions;

/**
 * ビジネスロジック違反の例外
 */
public class BusinessLogicException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    
    public BusinessLogicException(String message) {
        super(message);
    }
    
    public BusinessLogicException(String message, Throwable cause) {
        super(message, cause);
    }
}