package net.rushhourgame.core.database.repositories;

import net.rushhourgame.core.database.entities.ScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * スケジュールエンティティのリポジトリ
 */
@Repository
public interface ScheduleRepository extends JpaRepository<ScheduleEntity, String> {

    /**
     * 経路IDでスケジュールを検索
     */
    List<ScheduleEntity> findByRouteId(String routeId);

    /**
     * 電車IDでスケジュールを検索
     */
    Optional<ScheduleEntity> findByTrain_Id(String trainId);
}
