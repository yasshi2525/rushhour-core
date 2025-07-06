package net.rushhourgame.models.train;

import lombok.Data;
import net.rushhourgame.models.railway.Car;
import net.rushhourgame.models.route.Route;
import net.rushhourgame.models.timetable.Schedule;

import java.util.List;

/**
 * 電車を表すクラス。
 */
@Data
public class Train {
    private String id; // 電車ID
    private String ownerId; // 所有者ID
    private String trainType; // 例: 普通、快速、特急など
    private String groupId; // 電車グループID
    private int totalCapacity; // 総定員
    private int doorCount; // 総ドア数
    private boolean isPlayerControlled; // プレイヤー制御フラグ
    private List<Car> cars; // 車両編成
    private Route assignedRoute; // 割り当てられた経路
    private Schedule schedule; // 運行スケジュール
}