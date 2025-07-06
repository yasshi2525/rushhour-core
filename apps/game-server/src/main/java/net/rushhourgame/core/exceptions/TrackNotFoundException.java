package net.rushhourgame.core.exceptions;

/**
 * 線路エンティティが見つからない場合の例外
 */
public class TrackNotFoundException extends EntityNotFoundException {
    
    public TrackNotFoundException(String trackId) {
        super("Track not found with id: " + trackId);
    }
    
    public TrackNotFoundException(String trackId, Throwable cause) {
        super("Track not found with id: " + trackId, cause);
    }
}