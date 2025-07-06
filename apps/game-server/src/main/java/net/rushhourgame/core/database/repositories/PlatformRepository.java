package net.rushhourgame.core.database.repositories;

import net.rushhourgame.core.database.entities.PlatformEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * プラットフォームエンティティのリポジトリ
 */
@Repository
public interface PlatformRepository extends JpaRepository<PlatformEntity, String> {

    /**
     * 駅IDでプラットフォームを検索
     */
    List<PlatformEntity> findByStation_Id(String stationId);

    /**
     * 接続されている線路IDでプラットフォームを検索
     */
    List<PlatformEntity> findByConnectedTrackId(String connectedTrackId);

    /**
     * 指定されたキャパシティ以上のプラットフォームを検索
     */
    List<PlatformEntity> findByCapacityGreaterThanEqual(Integer capacity);
}
