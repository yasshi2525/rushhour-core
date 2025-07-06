
package net.rushhourgame.models.timetable;

import lombok.Data;
import java.util.List;

/**
 * 電車の運行スケジュール（ダイヤ）を表すクラス。
 */
@Data
public class Schedule {
    private String id;
    private String routeId;
    private String trainId;
    private List<StopTime> stopTimes;
}
