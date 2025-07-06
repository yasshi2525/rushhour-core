package net.rushhourgame.core.database.repositories;

import net.rushhourgame.core.database.entities.StopTimeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;

/**
 * 停車時間エンティティのリポジトリ
 */
@Repository
public interface StopTimeRepository extends JpaRepository<StopTimeEntity, Long> {

    /**
     * スケジュールIDで停車時間を検索
     */
    List<StopTimeEntity> findBySchedule_Id(String scheduleId);

    /**
     * 駅IDで停車時間を検索
     */
    List<StopTimeEntity> findByStationId(String stationId);

    /**
     * 指定された到着時間以降の停車時間を検索
     */
    List<StopTimeEntity> findByArrivalTimeAfter(LocalTime arrivalTime);

    /**
     * 指定された出発時間以前の停車時間を検索
     */
    List<StopTimeEntity> findByDepartureTimeBefore(LocalTime departureTime);

    /**
     * スケジュールIDと駅IDで停車時間を検索
     */
    List<StopTimeEntity> findBySchedule_IdAndStationId(String scheduleId, String stationId);
}
