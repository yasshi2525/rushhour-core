package net.rushhourgame.models.railway;

import lombok.Data;
import net.rushhourgame.models.common.Point3D;

import java.util.List;

/**
 * 線路セグメントを表すクラス。
 */
@Data
public class Track {
    private String id; // 線路ID
    private String ownerId; // 所有者ID
    private double length; // 線路長
    private double maxSpeed; // 最高速度
    private String startJunctionId; // 開始接続点ID
    private String endJunctionId; // 終了接続点ID
    private List<Point3D> curve; // 曲線座標列
    private List<Signal> signals; // 信号機リスト
}