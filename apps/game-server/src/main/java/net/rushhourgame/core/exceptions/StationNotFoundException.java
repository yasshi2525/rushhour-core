package net.rushhourgame.core.exceptions;

/**
 * 駅エンティティが見つからない場合の例外
 */
public class StationNotFoundException extends EntityNotFoundException {
    
    public StationNotFoundException(String stationId) {
        super("Station not found with id: " + stationId);
    }
    
    public StationNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public static StationNotFoundException byName(String stationName) {
        return new StationNotFoundException("Station not found with name: " + stationName);
    }
}