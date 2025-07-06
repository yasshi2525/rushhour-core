package net.rushhourgame.core.database.repositories;

import net.rushhourgame.core.database.entities.TrainEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 電車エンティティのリポジトリ
 */
@Repository
public interface TrainRepository extends JpaRepository<TrainEntity, String> {
    
    /**
     * 所有者IDで電車を検索
     */
    List<TrainEntity> findByOwnerId(String ownerId);
    
    /**
     * 電車タイプで電車を検索
     */
    List<TrainEntity> findByTrainType(String trainType);
    
    /**
     * グループIDで電車を検索
     */
    List<TrainEntity> findByGroupId(String groupId);
    
    /**
     * プレイヤー制御フラグで電車を検索
     */
    List<TrainEntity> findByIsPlayerControlled(Boolean isPlayerControlled);
    
    /**
     * 割り当てられた経路IDで電車を検索
     */
    List<TrainEntity> findByAssignedRouteId(String routeId);
    
    /**
     * 指定された定員以上の電車を検索
     */
    List<TrainEntity> findByTotalCapacityGreaterThanEqual(Integer capacity);
    
    /**
     * 所有者IDとプレイヤー制御フラグで電車を検索
     */
    @Query("SELECT t FROM TrainEntity t WHERE t.ownerId = :ownerId AND t.isPlayerControlled = :isPlayerControlled")
    List<TrainEntity> findByOwnerIdAndPlayerControlled(@Param("ownerId") String ownerId, 
                                                        @Param("isPlayerControlled") Boolean isPlayerControlled);
}