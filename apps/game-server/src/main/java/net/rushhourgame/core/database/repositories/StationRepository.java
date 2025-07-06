package net.rushhourgame.core.database.repositories;

import net.rushhourgame.core.database.entities.StationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 駅エンティティのリポジトリ
 */
@Repository
public interface StationRepository extends JpaRepository<StationEntity, String> {
    
    /**
     * 名前で駅を検索
     */
    Optional<StationEntity> findByName(String name);
    
    /**
     * 所有者IDで駅を検索
     */
    List<StationEntity> findByOwnerId(String ownerId);
    
    /**
     * 接続されている線路IDで駅を検索
     */
    @Query("SELECT s FROM StationEntity s WHERE :trackId MEMBER OF s.connectedTrackIds")
    List<StationEntity> findByConnectedTrackId(@Param("trackId") String trackId);
    
    /**
     * 指定された範囲内の駅を検索
     */
    @Query("SELECT s FROM StationEntity s WHERE " +
           "s.location.x BETWEEN :minX AND :maxX AND " +
           "s.location.y BETWEEN :minY AND :maxY")
    List<StationEntity> findByLocationRange(@Param("minX") Double minX, @Param("maxX") Double maxX,
                                            @Param("minY") Double minY, @Param("maxY") Double maxY);
}