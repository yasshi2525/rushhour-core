
package net.rushhourgame.models.railway;

import lombok.Data;
import java.util.List;

/**
 * 鉄道全体を表すクラス。
 * 線路、駅、電車など、鉄道を構成する要素のIDをリストで保持します。
 */
@Data
public class Railway {
    private String id; // 鉄道ID
    private String ownerId; // 所有者ID
    private List<String> trackIds; // 線路IDのリスト
    private List<String> stationIds; // 駅IDのリスト
    private List<String> trainIds; // 電車IDのリスト
}
