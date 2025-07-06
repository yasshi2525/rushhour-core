package net.rushhourgame.core.exceptions;

/**
 * エンティティが見つからない場合の例外
 */
public class EntityNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    
    public EntityNotFoundException(String message) {
        super(message);
    }
    
    public EntityNotFoundException(String entityType, String id) {
        super(String.format("%s not found with id: %s", entityType, id));
    }
    
    public EntityNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}