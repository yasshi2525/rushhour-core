
package net.rushhourgame.models.timetable;

import java.util.List;

/**
 * 電車の運行スケジュール（ダイヤ）を表すクラス。
 */
public class Schedule {
    private String id;
    private String routeId;
    private List<StopTime> stopTimes;
}
