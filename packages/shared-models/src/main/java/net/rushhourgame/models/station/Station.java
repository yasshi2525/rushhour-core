package net.rushhourgame.models.station;

import net.rushhourgame.models.common.Location;
import net.rushhourgame.models.railway.Corridor;
import net.rushhourgame.models.railway.Gate;
import net.rushhourgame.models.railway.Platform;

import java.util.List;

/**
 * 駅を表すクラス。
 */
public class Station {
    private String id; // 駅ID
    private String name; // 駅名
    private String ownerId; // 所有者ID
    private int totalCapacity; // 総収容人数
    private Location location; // 駅の位置
    private List<Gate> gates; // 改札リスト
    private List<Platform> platforms; // ホームリスト
    private List<Corridor> corridors; // 通路リスト
    private List<String> connectedTrackIds; // 接続線路IDのリスト
}