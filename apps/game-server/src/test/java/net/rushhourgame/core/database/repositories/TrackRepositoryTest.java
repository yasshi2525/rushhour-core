package net.rushhourgame.core.database.repositories;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import net.rushhourgame.core.database.entities.LocationEmbeddable;
import net.rushhourgame.core.database.entities.Point3DEmbeddable;
import net.rushhourgame.core.database.entities.SignalEntity;
import net.rushhourgame.core.database.entities.TrackEntity;

/**
 * TrackRepositoryの統合テストクラス
 * DataJpaTestアノテーションを使用し、JPA関連のコンポーネントのみをロード
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY) // インメモリデータベースを使用
@Transactional // 各テストメソッドの後にトランザクションをロールバックし、データベースをクリーンアップ
class TrackRepositoryTest {

    @Autowired
    private TrackRepository trackRepository;

    /**
     * 各テストの前にデータベースをクリーンアップ
     */
    @BeforeEach
    void setUp() {
        trackRepository.deleteAll();
    }

    /**
     * 線路の保存テスト
     * 新しい線路が正しく保存され、IDが割り当てられることを確認
     */
    @Test
    void saveTrack_shouldPersistTrackAndRelatedEntities() {
        // テストデータの準備
        TrackEntity track = createTestTrackEntity("track-1", "owner-1", 100.0, 120.0, "junction-A", "junction-B");

        // 曲線ポイントの追加
        Point3DEmbeddable point1 = createTestPoint3DEmbeddable(1.0, 2.0, 3.0, 0, track);
        Point3DEmbeddable point2 = createTestPoint3DEmbeddable(4.0, 5.0, 6.0, 1, track);
        track.setCurve(Arrays.asList(point1, point2));

        // 信号機の追加
        SignalEntity signal1 = createTestSignalEntity("signal-1", "TYPE_A", track);
        track.setSignals(Arrays.asList(signal1));

        // リポジトリメソッドの実行
        TrackEntity savedTrack = trackRepository.saveAndFlush(track);

        // 検証
        assertThat(savedTrack).isNotNull();
        assertThat(savedTrack.getId()).isEqualTo("track-1");
        assertThat(savedTrack.getCurve()).hasSize(2);
        assertThat(savedTrack.getSignals()).hasSize(1);

        // データベースから直接取得して検証
        Optional<TrackEntity> foundTrack = trackRepository.findById("track-1");
        assertThat(foundTrack).isPresent();
        assertThat(foundTrack.get().getCurve()).hasSize(2);
        assertThat(foundTrack.get().getSignals()).hasSize(1);
    }

    /**
     * IDによる線路の取得テスト
     * 存在するIDで線路が取得できることを確認
     */
    @Test
    void findById_shouldReturnTrack_whenTrackExists() {
        // テストデータの準備
        TrackEntity track = createTestTrackEntity("track-2", "owner-2", 200.0, 150.0, null, null);
        trackRepository.save(track);

        // リポジトリメソッドの実行
        Optional<TrackEntity> foundTrack = trackRepository.findById("track-2");

        // 検証
        assertThat(foundTrack).isPresent();
        assertThat(foundTrack.get().getOwnerId()).isEqualTo("owner-2");
    }

    /**
     * IDによる線路の取得テスト
     * 存在しないIDで線路が取得できないことを確認
     */
    @Test
    void findById_shouldReturnEmpty_whenTrackDoesNotExist() {
        // リポジトリメソッドの実行
        Optional<TrackEntity> foundTrack = trackRepository.findById("non-existent-track");

        // 検証
        assertThat(foundTrack).isEmpty();
    }

    /**
     * 全ての線路の取得テスト
     * データベースに存在する全ての線路が取得できることを確認
     */
    @Test
    void findAll_shouldReturnAllTracks() {
        // テストデータの準備
        trackRepository.save(createTestTrackEntity("track-3", "owner-3", 50.0, 80.0, null, null));
        trackRepository.save(createTestTrackEntity("track-4", "owner-3", 75.0, 90.0, null, null));

        // リポジトリメソッドの実行
        List<TrackEntity> tracks = trackRepository.findAll();

        // 検証
        assertThat(tracks).hasSize(2);
    }

    /**
     * 線路の更新テスト
     * 既存の線路情報が正しく更新されることを確認
     */
    @Test
    void updateTrack_shouldUpdateExistingTrack() {
        // テストデータの準備
        TrackEntity originalTrack = createTestTrackEntity("track-5", "owner-4", 150.0, 100.0, null, null);
        trackRepository.save(originalTrack);

        // 更新データの準備
        originalTrack.setLength(160.0);
        originalTrack.setMaxSpeed(110.0);

        // リポジトリメソッドの実行
        TrackEntity updatedTrack = trackRepository.save(originalTrack);

        // 検証
        assertThat(updatedTrack.getLength()).isEqualTo(160.0);
        assertThat(updatedTrack.getMaxSpeed()).isEqualTo(110.0);

        // データベースから直接取得して検証
        Optional<TrackEntity> foundTrack = trackRepository.findById("track-5");
        assertThat(foundTrack).isPresent();
        assertThat(foundTrack.get().getLength()).isEqualTo(160.0);
    }

    /**
     * 線路の削除テスト
     * 存在するIDの線路が正しく削除されることを確認
     */
    @Test
    void deleteById_shouldDeleteTrack_whenTrackExists() {
        // テストデータの準備
        TrackEntity track = createTestTrackEntity("track-6", "owner-5", 80.0, 70.0, null, null);
        trackRepository.save(track);

        // リポジトリメソッドの実行
        trackRepository.deleteById("track-6");

        // 検証
        assertThat(trackRepository.findById("track-6")).isEmpty();
    }

    /**
     * 所有者IDによる線路の検索テスト
     * 特定の所有者IDに紐づく線路が全て取得できることを確認
     */
    @Test
    void findByOwnerId_shouldReturnTracks_whenTracksExist() {
        // テストデータの準備
        trackRepository.save(createTestTrackEntity("track-7", "owner-6", 100.0, 100.0, null, null));
        trackRepository.save(createTestTrackEntity("track-8", "owner-6", 110.0, 110.0, null, null));
        trackRepository.save(createTestTrackEntity("track-9", "owner-7", 120.0, 120.0, null, null));

        // リポジトリメソッドの実行
        List<TrackEntity> tracks = trackRepository.findByOwnerId("owner-6");

        // 検証
        assertThat(tracks).hasSize(2);
        assertThat(tracks).extracting(TrackEntity::getOwnerId).containsOnly("owner-6");
    }

    /**
     * 開始または終了接続点IDによる線路の検索テスト
     * 特定の接続点IDに紐づく線路が全て取得できることを確認
     */
    @Test
    void findByJunctionId_shouldReturnTracks_whenTracksExist() {
        // テストデータの準備
        trackRepository.save(createTestTrackEntity("track-10", "owner-8", 100.0, 100.0, "junction-X", "junction-Y"));
        trackRepository.save(createTestTrackEntity("track-11", "owner-8", 110.0, 110.0, "junction-Y", "junction-Z"));
        trackRepository.save(createTestTrackEntity("track-12", "owner-8", 120.0, 120.0, "junction-A", "junction-B"));

        // リポジトリメソッドの実行
        List<TrackEntity> tracks = trackRepository.findByJunctionId("junction-Y");

        // 検証
        assertThat(tracks).hasSize(2);
        assertThat(tracks).extracting(TrackEntity::getId).containsExactlyInAnyOrder("track-10", "track-11");
    }

    /**
     * 最高速度以上の線路の検索テスト
     * 指定された最高速度以上の線路が全て取得できることを確認
     */
    @Test
    void findByMaxSpeedGreaterThanEqual_shouldReturnTracks() {
        // テストデータの準備
        trackRepository.save(createTestTrackEntity("track-13", "owner-9", 100.0, 100.0, null, null));
        trackRepository.save(createTestTrackEntity("track-14", "owner-9", 110.0, 120.0, null, null));
        trackRepository.save(createTestTrackEntity("track-15", "owner-9", 120.0, 90.0, null, null));

        // リポジトリメソッドの実行
        List<TrackEntity> tracks = trackRepository.findByMaxSpeedGreaterThanEqual(100.0);

        // 検証
        assertThat(tracks).hasSize(2);
        assertThat(tracks).extracting(TrackEntity::getId).containsExactlyInAnyOrder("track-13", "track-14");
    }

    /**
     * 指定された長さ範囲内の線路の検索テスト
     * 指定された長さ範囲内の線路が全て取得できることを確認
     */
    @Test
    void findByLengthBetween_shouldReturnTracks() {
        // テストデータの準備
        trackRepository.save(createTestTrackEntity("track-16", "owner-10", 50.0, 100.0, null, null));
        trackRepository.save(createTestTrackEntity("track-17", "owner-10", 75.0, 100.0, null, null));
        trackRepository.save(createTestTrackEntity("track-18", "owner-10", 100.0, 100.0, null, null));
        trackRepository.save(createTestTrackEntity("track-19", "owner-10", 120.0, 100.0, null, null));

        // リポジトリメソッドの実行
        List<TrackEntity> tracks = trackRepository.findByLengthBetween(60.0, 110.0);

        // 検証
        assertThat(tracks).hasSize(2);
        assertThat(tracks).extracting(TrackEntity::getId).containsExactlyInAnyOrder("track-17", "track-18");
    }

    // ヘルパーメソッド：テスト用のTrackEntityを作成
    private TrackEntity createTestTrackEntity(String id, String ownerId, Double length, Double maxSpeed, String startJunctionId, String endJunctionId) {
        TrackEntity entity = new TrackEntity();
        entity.setId(id);
        entity.setOwnerId(ownerId);
        entity.setLength(length);
        entity.setMaxSpeed(maxSpeed);
        entity.setStartJunctionId(startJunctionId);
        entity.setEndJunctionId(endJunctionId);
        return entity;
    }

    // ヘルパーメソッド：テスト用のPoint3DEmbeddableを作成
    private Point3DEmbeddable createTestPoint3DEmbeddable(Double x, Double y, Double z, Integer sequenceOrder, TrackEntity track) {
        Point3DEmbeddable point = new Point3DEmbeddable();
        point.setX(x);
        point.setY(y);
        point.setZ(z);
        point.setSequenceOrder(sequenceOrder);
        point.setTrack(track);
        return point;
    }

    // ヘルパーメソッド：テスト用のSignalEntityを作成
    private SignalEntity createTestSignalEntity(String id, String signalType, TrackEntity track) {
        SignalEntity signal = new SignalEntity();
        signal.setId(id);
        signal.setSignalType(signalType);
        LocationEmbeddable position = new LocationEmbeddable();
        position.setX(1.0); position.setY(2.0); position.setZ(3.0);
        signal.setPosition(position);
        signal.setTrack(track);
        return signal;
    }
}
