package net.rushhourgame.models.railway;

import lombok.Data;

/**
 * 駅の通路を表すクラス。
 */
@Data
public class Corridor {
    private String id; // 通路ID
    private String stationId; // 所属する駅のID
    private double length; // 通路の長さ
    private double width; // 通路の幅
}