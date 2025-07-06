package net.rushhourgame.core.database.repositories;

import net.rushhourgame.core.database.entities.TrainEntity;
import net.rushhourgame.models.common.TrainType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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
    List<TrainEntity> findByTrainType(TrainType trainType);
    
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
    
    // N+1問題解決のための@EntityGraphアノテーション付きメソッド
    
    /**
     * 電車と車両、スケジュールを一緒に取得
     */
    @EntityGraph(attributePaths = {"cars", "schedule"})
    @Query("SELECT t FROM TrainEntity t")
    List<TrainEntity> findAllWithRelations();
    
    /**
     * IDで電車と関連データを取得
     */
    @EntityGraph(attributePaths = {"cars", "schedule"})
    @Query("SELECT t FROM TrainEntity t WHERE t.id = :id")
    Optional<TrainEntity> findByIdWithRelations(@Param("id") String id);
    
    /**
     * 所有者IDで電車と関連データを取得
     */
    @EntityGraph(attributePaths = {"cars", "schedule"})
    @Query("SELECT t FROM TrainEntity t WHERE t.ownerId = :ownerId")
    List<TrainEntity> findByOwnerIdWithRelations(@Param("ownerId") String ownerId);
    
    /**
     * 電車タイプで電車と関連データを取得
     */
    @EntityGraph(attributePaths = {"cars", "schedule"})
    @Query("SELECT t FROM TrainEntity t WHERE t.trainType = :trainType")
    List<TrainEntity> findByTrainTypeWithRelations(@Param("trainType") TrainType trainType);
    
    /**
     * 車両のみを一緒に取得
     */
    @EntityGraph(attributePaths = {"cars"})
    @Query("SELECT t FROM TrainEntity t")
    List<TrainEntity> findAllWithCars();
    
    /**
     * スケジュールのみを一緒に取得
     */
    @EntityGraph(attributePaths = {"schedule"})
    @Query("SELECT t FROM TrainEntity t")
    List<TrainEntity> findAllWithSchedule();
    
    // JOIN FETCHを使用したカスタムクエリ
    
    /**
     * JOIN FETCHを使用して電車と車両、スケジュールを取得
     */
    @Query("SELECT DISTINCT t FROM TrainEntity t " +
           "LEFT JOIN FETCH t.cars " +
           "LEFT JOIN FETCH t.schedule " +
           "WHERE t.ownerId = :ownerId")
    List<TrainEntity> findByOwnerIdWithJoinFetch(@Param("ownerId") String ownerId);
    
    /**
     * JOIN FETCHを使用して電車と車両を取得
     */
    @Query("SELECT DISTINCT t FROM TrainEntity t " +
           "LEFT JOIN FETCH t.cars " +
           "WHERE t.trainType = :trainType")
    List<TrainEntity> findByTrainTypeWithCars(@Param("trainType") TrainType trainType);
    
    /**
     * JOIN FETCHを使用してプレイヤー制御の電車とスケジュールを取得
     */
    @Query("SELECT DISTINCT t FROM TrainEntity t " +
           "LEFT JOIN FETCH t.schedule " +
           "WHERE t.isPlayerControlled = :isPlayerControlled")
    List<TrainEntity> findByPlayerControlledWithSchedule(@Param("isPlayerControlled") Boolean isPlayerControlled);
    
    /**
     * JOIN FETCHを使用してグループIDで電車と全関連データを取得
     */
    @Query("SELECT DISTINCT t FROM TrainEntity t " +
           "LEFT JOIN FETCH t.cars " +
           "LEFT JOIN FETCH t.schedule " +
           "WHERE t.groupId = :groupId")
    List<TrainEntity> findByGroupIdWithAllRelations(@Param("groupId") String groupId);
}