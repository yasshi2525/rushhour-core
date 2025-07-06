package net.rushhourgame.core.database.repositories;

import net.rushhourgame.core.database.entities.GateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 改札エンティティのリポジトリ
 */
@Repository
public interface GateRepository extends JpaRepository<GateEntity, String> {

    /**
     * 駅IDで改札を検索
     */
    List<GateEntity> findByStation_Id(String stationId);

    /**
     * 処理時間以上の改札を検索
     */
    List<GateEntity> findByProcessingTimeGreaterThanEqual(Double processingTime);

    /**
     * 指定されたキャパシティ範囲内の改札を検索
     */
    List<GateEntity> findByCapacityBetween(Integer minCapacity, Integer maxCapacity);
}
