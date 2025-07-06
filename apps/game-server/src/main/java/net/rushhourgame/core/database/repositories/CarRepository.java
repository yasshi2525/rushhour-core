package net.rushhourgame.core.database.repositories;

import net.rushhourgame.core.database.entities.CarEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 車両エンティティのリポジトリ
 */
@Repository
public interface CarRepository extends JpaRepository<CarEntity, String> {

    /**
     * 電車IDで車両を検索
     */
    List<CarEntity> findByTrain_Id(String trainId);

    /**
     * 指定されたキャパシティ以上の車両を検索
     */
    List<CarEntity> findByCapacityGreaterThanEqual(Integer capacity);

    /**
     * ドア数で車両を検索
     */
    List<CarEntity> findByDoorCount(Integer doorCount);
}
