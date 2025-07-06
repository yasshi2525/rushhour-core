package net.rushhourgame.core.database.repositories;

import net.rushhourgame.core.database.entities.SignalEntity;
import net.rushhourgame.models.common.SignalType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 信号機エンティティのリポジトリ
 */
@Repository
public interface SignalRepository extends JpaRepository<SignalEntity, String> {

    /**
     * 信号タイプで信号機を検索
     */
    List<SignalEntity> findBySignalType(SignalType signalType);

    /**
     * 保護対象線路IDで信号機を検索
     */
    @Query("SELECT s FROM SignalEntity s WHERE :trackId MEMBER OF s.protectedTrackIds")
    List<SignalEntity> findByProtectedTrackIdsContaining(@Param("trackId") String trackId);

    /**
     * 設置されている線路IDで信号機を検索
     */
    List<SignalEntity> findByTrack_Id(String trackId);
    
    // N+1問題解決のための@EntityGraphアノテーション付きメソッド
    
    /**
     * 信号機と線路を一緒に取得
     */
    @EntityGraph(attributePaths = {"track"})
    @Override
    List<SignalEntity> findAll();
    
    /**
     * IDで信号機と線路を取得
     */
    @EntityGraph(attributePaths = {"track"})
    @Query("SELECT s FROM SignalEntity s WHERE s.id = :id")
    Optional<SignalEntity> findByIdWithTrack(@Param("id") String id);
    
    /**
     * 信号タイプで信号機と線路を取得
     */
    @Query("SELECT s FROM SignalEntity s " +
           "LEFT JOIN FETCH s.track " +
           "WHERE s.signalType = :signalType")
    List<SignalEntity> findBySignalTypeWithTrack(@Param("signalType") SignalType signalType);
    
    /**
     * 線路IDで信号機と線路を取得
     */
    @Query("SELECT s FROM SignalEntity s " +
           "LEFT JOIN FETCH s.track " +
           "WHERE s.track.id = :trackId")
    List<SignalEntity> findByTrack_IdWithTrack(@Param("trackId") String trackId);
    
    // JOIN FETCHを使用したカスタムクエリ
    
    /**
     * JOIN FETCHを使用して信号機と線路を取得
     */
    @Query("SELECT s FROM SignalEntity s " +
           "JOIN FETCH s.track " +
           "WHERE s.signalType = :signalType")
    List<SignalEntity> findBySignalTypeWithJoinFetch(@Param("signalType") SignalType signalType);
    
    /**
     * JOIN FETCHを使用して保護対象線路の信号機と線路を取得
     */
    @Query("SELECT s FROM SignalEntity s " +
           "JOIN FETCH s.track " +
           "WHERE :trackId MEMBER OF s.protectedTrackIds")
    List<SignalEntity> findByProtectedTrackIdWithJoinFetch(@Param("trackId") String trackId);
    
    /**
     * JOIN FETCHを使用して設置線路の信号機と線路を取得
     */
    @Query("SELECT s FROM SignalEntity s " +
           "JOIN FETCH s.track t " +
           "WHERE t.id = :trackId")
    List<SignalEntity> findByTrackIdWithJoinFetch(@Param("trackId") String trackId);
    
    /**
     * JOIN FETCHを使用して全信号機と線路を取得
     */
    @Query("SELECT s FROM SignalEntity s " +
           "JOIN FETCH s.track")
    List<SignalEntity> findAllWithJoinFetch();
}
