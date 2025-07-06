package net.rushhourgame.models.railway;

import net.rushhourgame.models.common.Location;

/**
 * 駅の改札を表すクラス。
 */
public class Gate {
    private String id; // 改札ID
    private String stationId; // 所属する駅のID
    private int capacity; // 同時通過可能人数
    private double processingTime; // 一人当たりの通過時間
    private Location position; // 改札の位置
}