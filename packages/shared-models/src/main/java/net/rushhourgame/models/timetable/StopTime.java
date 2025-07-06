
package net.rushhourgame.models.timetable;

import lombok.Data;
import java.time.LocalTime;

/**
 * スケジュール内の個々の停車情報を表すクラス。
 */
@Data
public class StopTime {
    private String stationId;
    private String scheduleId;
    private LocalTime arrivalTime;
    private LocalTime departureTime;
}
