package net.rushhourgame.core.database.repositories;

import net.rushhourgame.core.database.entities.SignalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 信号機エンティティのリポジトリ
 */
@Repository
public interface SignalRepository extends JpaRepository<SignalEntity, String> {

    /**
     * 信号タイプで信号機を検索
     */
    List<SignalEntity> findBySignalType(String signalType);

    /**
     * 保護対象線路IDで信号機を検索
     */
    @Query("SELECT s FROM SignalEntity s WHERE :trackId MEMBER OF s.protectedTrackIds")
    List<SignalEntity> findByProtectedTrackIdsContaining(@Param("trackId") String trackId);

    /**
     * 設置されている線路IDで信号機を検索
     */
    List<SignalEntity> findByTrack_Id(String trackId);
}
