package net.rushhourgame.core.exceptions;

/**
 * 電車エンティティが見つからない場合の例外
 */
public class TrainNotFoundException extends EntityNotFoundException {
    
    public TrainNotFoundException(String trainId) {
        super("Train not found with id: " + trainId);
    }
    
    public TrainNotFoundException(String trainId, Throwable cause) {
        super("Train not found with id: " + trainId, cause);
    }
}