package net.rushhourgame.models.railway;

import lombok.Data;
import net.rushhourgame.models.common.Location;

import java.util.List;

/**
 * 信号機を表すクラス。
 */
@Data
public class Signal {
    private String id; // 信号機ID
    private String trackId; // 設置されている線路ID
    private String signalType; // 例: BLOCK, PATH, ABSOLUTE
    private List<String> protectedTrackIds; // 保護対象の線路IDリスト
    private Location position; // 信号機の位置
}