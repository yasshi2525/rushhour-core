package net.rushhourgame.models.railway;

import lombok.Data;
import net.rushhourgame.models.common.Location;

import java.util.List;

/**
 * 線路の分岐・合流点を表すクラス。
 */
@Data
public class Junction {
    private String id; // 分岐点ID
    private String junctionType; // 例: MERGE, SPLIT, CROSS
    private List<String> connectedTrackIds; // 接続されている線路IDのリスト
    private Location position; // 分岐点の位置
}