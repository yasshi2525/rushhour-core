package net.rushhourgame.models.railway;

/**
 * 駅のプラットフォーム（ホーム）を表すクラス。
 */
public class Platform {
    private String id; // ホームID
    private String stationId; // 所属する駅のID
    private String connectedTrackId; // 接続されている線路ID
    private int capacity; // 収容定員
}