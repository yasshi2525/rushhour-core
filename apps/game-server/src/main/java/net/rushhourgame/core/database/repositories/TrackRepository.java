package net.rushhourgame.core.database.repositories;

import net.rushhourgame.core.database.entities.TrackEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 線路エンティティのリポジトリ
 */
@Repository
public interface TrackRepository extends JpaRepository<TrackEntity, String> {
    
    /**
     * 所有者IDで線路を検索
     */
    List<TrackEntity> findByOwnerId(String ownerId);
    
    /**
     * 開始または終了接続点IDで線路を検索
     */
    @Query("SELECT t FROM TrackEntity t WHERE t.startJunctionId = :junctionId OR t.endJunctionId = :junctionId")
    List<TrackEntity> findByJunctionId(@Param("junctionId") String junctionId);
    
    /**
     * 最高速度以上の線路を検索
     */
    List<TrackEntity> findByMaxSpeedGreaterThanEqual(Double maxSpeed);
    
    /**
     * 指定された長さ範囲内の線路を検索
     */
    List<TrackEntity> findByLengthBetween(Double minLength, Double maxLength);
}