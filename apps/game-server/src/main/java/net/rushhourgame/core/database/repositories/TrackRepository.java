package net.rushhourgame.core.database.repositories;

import net.rushhourgame.core.database.entities.TrackEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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
    
    // N+1問題解決のための@EntityGraphアノテーション付きメソッド
    
    /**
     * 線路とカーブ、信号機を一緒に取得
     */
    @EntityGraph(attributePaths = {"curve", "signals"})
    @Query("SELECT t FROM TrackEntity t")
    List<TrackEntity> findAllWithRelations();
    
    /**
     * IDで線路と関連データを取得
     */
    @EntityGraph(attributePaths = {"curve", "signals"})
    @Query("SELECT t FROM TrackEntity t WHERE t.id = :id")
    Optional<TrackEntity> findByIdWithRelations(@Param("id") String id);
    
    /**
     * 所有者IDで線路と関連データを取得
     */
    @EntityGraph(attributePaths = {"curve", "signals"})
    @Query("SELECT t FROM TrackEntity t WHERE t.ownerId = :ownerId")
    List<TrackEntity> findByOwnerIdWithRelations(@Param("ownerId") String ownerId);
    
    /**
     * カーブのみを一緒に取得
     */
    @EntityGraph(attributePaths = {"curve"})
    @Query("SELECT t FROM TrackEntity t")
    List<TrackEntity> findAllWithCurve();
    
    /**
     * 信号機のみを一緒に取得
     */
    @EntityGraph(attributePaths = {"signals"})
    @Query("SELECT t FROM TrackEntity t")
    List<TrackEntity> findAllWithSignals();
    
    /**
     * 接続IDで線路と関連データを取得
     */
    @EntityGraph(attributePaths = {"curve", "signals"})
    @Query("SELECT t FROM TrackEntity t WHERE t.startJunctionId = :junctionId OR t.endJunctionId = :junctionId")
    List<TrackEntity> findByJunctionIdWithRelations(@Param("junctionId") String junctionId);
    
    // JOIN FETCHを使用したカスタムクエリ
    
    /**
     * JOIN FETCHを使用して線路と全関連データを取得
     */
    @Query("SELECT DISTINCT t FROM TrackEntity t " +
           "LEFT JOIN FETCH t.curve " +
           "LEFT JOIN FETCH t.signals " +
           "WHERE t.ownerId = :ownerId")
    List<TrackEntity> findByOwnerIdWithJoinFetch(@Param("ownerId") String ownerId);
    
    /**
     * JOIN FETCHを使用して線路とカーブを取得
     */
    @Query("SELECT DISTINCT t FROM TrackEntity t " +
           "LEFT JOIN FETCH t.curve " +
           "WHERE t.maxSpeed >= :maxSpeed")
    List<TrackEntity> findByMaxSpeedWithCurve(@Param("maxSpeed") Double maxSpeed);
    
    /**
     * JOIN FETCHを使用して線路と信号機を取得
     */
    @Query("SELECT DISTINCT t FROM TrackEntity t " +
           "LEFT JOIN FETCH t.signals " +
           "WHERE t.length BETWEEN :minLength AND :maxLength")
    List<TrackEntity> findByLengthRangeWithSignals(@Param("minLength") Double minLength, @Param("maxLength") Double maxLength);
    
    /**
     * JOIN FETCHを使用して接続線路と全関連データを取得
     */
    @Query("SELECT DISTINCT t FROM TrackEntity t " +
           "LEFT JOIN FETCH t.curve " +
           "LEFT JOIN FETCH t.signals " +
           "WHERE t.startJunctionId = :junctionId OR t.endJunctionId = :junctionId")
    List<TrackEntity> findByJunctionIdWithJoinFetch(@Param("junctionId") String junctionId);
}