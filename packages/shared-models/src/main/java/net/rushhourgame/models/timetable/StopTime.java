
package net.rushhourgame.models.timetable;

import java.time.LocalTime;

/**
 * スケジュール内の個々の停車情報を表すクラス。
 */
public class StopTime {
    private String stationId;
    private LocalTime arrivalTime;
    private LocalTime departureTime;
}
