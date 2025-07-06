package net.rushhourgame.core.database.repositories;

import net.rushhourgame.core.database.entities.StationEntity;
import org.springframework.data.jpa.repository.EntityGraph;
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
    
    // N+1問題解決のための@EntityGraphアノテーション付きメソッド
    
    /**
     * 駅とプラットフォーム、改札口、通路を一緒に取得
     */
    @EntityGraph(attributePaths = {"platforms", "gates", "corridors"})
    @Query("SELECT s FROM StationEntity s")
    List<StationEntity> findAllWithRelations();
    
    /**
     * IDで駅と関連データを取得
     */
    @EntityGraph(attributePaths = {"platforms", "gates", "corridors"})
    @Query("SELECT s FROM StationEntity s WHERE s.id = :id")
    Optional<StationEntity> findByIdWithRelations(@Param("id") String id);
    
    /**
     * 名前で駅と関連データを取得
     */
    @EntityGraph(attributePaths = {"platforms", "gates", "corridors"})
    @Query("SELECT s FROM StationEntity s WHERE s.name = :name")
    Optional<StationEntity> findByNameWithRelations(@Param("name") String name);
    
    /**
     * 所有者IDで駅と関連データを取得
     */
    @EntityGraph(attributePaths = {"platforms", "gates", "corridors"})
    @Query("SELECT s FROM StationEntity s WHERE s.ownerId = :ownerId")
    List<StationEntity> findByOwnerIdWithRelations(@Param("ownerId") String ownerId);
    
    /**
     * プラットフォームのみを一緒に取得
     */
    @EntityGraph(attributePaths = {"platforms"})
    @Query("SELECT s FROM StationEntity s")
    List<StationEntity> findAllWithPlatforms();
    
    /**
     * 改札口のみを一緒に取得
     */
    @EntityGraph(attributePaths = {"gates"})
    @Query("SELECT s FROM StationEntity s")
    List<StationEntity> findAllWithGates();
    
    /**
     * 通路のみを一緒に取得
     */
    @EntityGraph(attributePaths = {"corridors"})
    @Query("SELECT s FROM StationEntity s")
    List<StationEntity> findAllWithCorridors();
    
    // JOIN FETCHを使用したカスタムクエリ
    
    /**
     * JOIN FETCHを使用して駅と全関連データを取得
     */
    @Query("SELECT DISTINCT s FROM StationEntity s " +
           "LEFT JOIN FETCH s.platforms " +
           "LEFT JOIN FETCH s.gates " +
           "LEFT JOIN FETCH s.corridors " +
           "WHERE s.ownerId = :ownerId")
    List<StationEntity> findByOwnerIdWithJoinFetch(@Param("ownerId") String ownerId);
    
    /**
     * JOIN FETCHを使用して駅とプラットフォームを取得
     */
    @Query("SELECT DISTINCT s FROM StationEntity s " +
           "LEFT JOIN FETCH s.platforms " +
           "WHERE s.name = :name")
    Optional<StationEntity> findByNameWithPlatforms(@Param("name") String name);
    
    /**
     * JOIN FETCHを使用して指定範囲の駅と関連データを取得
     */
    @Query("SELECT DISTINCT s FROM StationEntity s " +
           "LEFT JOIN FETCH s.platforms " +
           "LEFT JOIN FETCH s.gates " +
           "LEFT JOIN FETCH s.corridors " +
           "WHERE s.location.x BETWEEN :minX AND :maxX AND " +
           "s.location.y BETWEEN :minY AND :maxY")
    List<StationEntity> findByLocationRangeWithJoinFetch(@Param("minX") Double minX, @Param("maxX") Double maxX,
                                                          @Param("minY") Double minY, @Param("maxY") Double maxY);
    
    /**
     * JOIN FETCHを使用して接続線路の駅と関連データを取得
     */
    @Query("SELECT DISTINCT s FROM StationEntity s " +
           "LEFT JOIN FETCH s.platforms " +
           "LEFT JOIN FETCH s.gates " +
           "LEFT JOIN FETCH s.corridors " +
           "WHERE :trackId MEMBER OF s.connectedTrackIds")
    List<StationEntity> findByConnectedTrackIdWithJoinFetch(@Param("trackId") String trackId);
}