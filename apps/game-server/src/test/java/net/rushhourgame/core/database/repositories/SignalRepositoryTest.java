package net.rushhourgame.core.database.repositories;

import net.rushhourgame.core.database.entities.LocationEmbeddable;
import net.rushhourgame.core.database.entities.SignalEntity;
import net.rushhourgame.core.database.entities.TrackEntity;
import net.rushhourgame.models.common.SignalType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * SignalRepositoryの統合テストクラス
 * DataJpaTestアノテーションを使用し、JPA関連のコンポーネントのみをロード
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY) // インメモリデータベースを使用
@Transactional // 各テストメソッドの後にトランザクションをロールバックし、データベースをクリーンアップ
class SignalRepositoryTest {

    @Autowired
    private SignalRepository signalRepository;

    @Autowired
    private TrackRepository trackRepository; // 関連エンティティの保存用

    /**
     * 各テストの前にデータベースをクリーンアップ
     */
    @BeforeEach
    void setUp() {
        signalRepository.deleteAll();
        trackRepository.deleteAll();
    }

    /**
     * 信号機の保存テスト
     * 新しい信号機が正しく保存され、IDが割り当てられることを確認
     */
    @Test
    void saveSignal_shouldPersistSignal() {
        // テストデータの準備
        TrackEntity track = createTestTrackEntity("owner-1", 100.0, 120.0, null, null);
        trackRepository.save(track);

        SignalEntity signal = createTestSignalEntity(SignalType.BLOCK, track);
        signal.setProtectedTrackIds(Arrays.asList("protected-track-1", "protected-track-2"));

        // リポジトリメソッドの実行
        SignalEntity savedSignal = signalRepository.save(signal);

        // 検証
        assertThat(savedSignal).isNotNull();
        assertThat(savedSignal.getId()).isNotNull(); // IDは自動生成される
        assertThat(savedSignal.getProtectedTrackIds()).hasSize(2);

        // データベースから直接取得して検証
        Optional<SignalEntity> foundSignal = signalRepository.findById(savedSignal.getId());
        assertThat(foundSignal).isPresent();
        assertThat(foundSignal.get().getProtectedTrackIds()).hasSize(2);
    }

    /**
     * IDによる信号機の取得テスト
     * 存在するIDで信号機が取得できることを確認
     */
    @Test
    void findById_shouldReturnSignal_whenSignalExists() {
        // テストデータの準備
        TrackEntity track = createTestTrackEntity("owner-2", 100.0, 120.0, null, null);
        trackRepository.save(track);
        SignalEntity signal = createTestSignalEntity(SignalType.PATH, track);
        signalRepository.save(signal);

        // リポジトリメソッドの実行
        Optional<SignalEntity> foundSignal = signalRepository.findById(signal.getId());

        // 検証
        assertThat(foundSignal).isPresent();
        assertThat(foundSignal.get().getSignalType()).isEqualTo(SignalType.PATH);
    }

    /**
     * IDによる信号機の取得テスト
     * 存在しないIDで信号機が取得できないことを確認
     */
    @Test
    void findById_shouldReturnEmpty_whenSignalDoesNotExist() {
        // リポジトリメソッドの実行
        Optional<SignalEntity> foundSignal = signalRepository.findById("non-existent-signal");

        // 検証
        assertThat(foundSignal).isEmpty();
    }

    /**
     * 全ての信号機の取得テスト
     * データベースに存在する全ての信号機が取得できることを確認
     */
    @Test
    void findAll_shouldReturnAllSignals() {
        // テストデータの準備
        TrackEntity track1 = createTestTrackEntity("owner-3", 100.0, 120.0, null, null);
        TrackEntity track2 = createTestTrackEntity("owner-3", 100.0, 120.0, null, null);
        trackRepository.saveAll(Arrays.asList(track1, track2));

        signalRepository.save(createTestSignalEntity(SignalType.BLOCK, track1));
        signalRepository.save(createTestSignalEntity(SignalType.PATH, track2));

        // リポジトリメソッドの実行
        List<SignalEntity> signals = signalRepository.findAll();

        // 検証
        assertThat(signals).hasSize(2);
    }

    /**
     * 信号機の更新テスト
     * 既存の信号機情報が正しく更新されることを確認
     */
    @Test
    void updateSignal_shouldUpdateExistingSignal() {
        // テストデータの準備
        TrackEntity track = createTestTrackEntity("owner-4", 100.0, 120.0, null, null);
        trackRepository.save(track);
        SignalEntity originalSignal = createTestSignalEntity(SignalType.BLOCK, track);
        signalRepository.save(originalSignal);

        // 更新データの準備
        originalSignal.setSignalType(SignalType.ABSOLUTE);
        LocationEmbeddable newPosition = new LocationEmbeddable();
        newPosition.setX(10.0); newPosition.setY(20.0); newPosition.setZ(30.0);
        originalSignal.setPosition(newPosition);

        // リポジトリメソッドの実行
        SignalEntity updatedSignal = signalRepository.save(originalSignal);

        // 検証
        assertThat(updatedSignal.getSignalType()).isEqualTo(SignalType.ABSOLUTE);
        assertThat(updatedSignal.getPosition().getX()).isEqualTo(10.0);

        // データベースから直接取得して検証
        Optional<SignalEntity> foundSignal = signalRepository.findById(originalSignal.getId());
        assertThat(foundSignal).isPresent();
        assertThat(foundSignal.get().getSignalType()).isEqualTo(SignalType.ABSOLUTE);
    }

    /**
     * 信号機の削除テスト
     * 存在するIDの信号機が正しく削除されることを確認
     */
    @Test
    void deleteById_shouldDeleteSignal_whenSignalExists() {
        // テストデータの準備
        TrackEntity track = createTestTrackEntity("owner-5", 100.0, 120.0, null, null);
        trackRepository.save(track);
        SignalEntity signal = createTestSignalEntity(SignalType.BLOCK, track);
        signalRepository.save(signal);

        // リポジトリメソッドの実行
        signalRepository.deleteById(signal.getId());

        // 検証
        assertThat(signalRepository.findById(signal.getId())).isEmpty();
    }

    /**
     * 信号タイプによる信号機の検索テスト
     * 特定の信号タイプに紐づく信号機が全て取得できることを確認
     */
    @Test
    void findBySignalType_shouldReturnSignals_whenSignalsExist() {
        // テストデータの準備
        TrackEntity track1 = createTestTrackEntity("owner-6", 100.0, 120.0, null, null);
        TrackEntity track2 = createTestTrackEntity("owner-6", 100.0, 120.0, null, null);
        trackRepository.saveAll(Arrays.asList(track1, track2));

        signalRepository.save(createTestSignalEntity(SignalType.BLOCK, track1));
        signalRepository.save(createTestSignalEntity(SignalType.PATH, track2));
        signalRepository.save(createTestSignalEntity(SignalType.BLOCK, track1));

        // リポジトリメソッドの実行
        List<SignalEntity> signals = signalRepository.findBySignalType(SignalType.BLOCK);

        // 検証
        assertThat(signals).hasSize(2);
        assertThat(signals).extracting(SignalEntity::getSignalType).containsOnly(SignalType.BLOCK);
    }

    /**
     * 保護対象線路IDによる信号機の検索テスト
     * 特定の保護対象線路IDに紐づく信号機が全て取得できることを確認
     */
    @Test
    void findByProtectedTrackIdsContaining_shouldReturnSignals() {
        // テストデータの準備
        TrackEntity track1 = createTestTrackEntity("owner-7", 100.0, 120.0, null, null);
        TrackEntity track2 = createTestTrackEntity("owner-7", 100.0, 120.0, null, null);
        trackRepository.saveAll(Arrays.asList(track1, track2));

        SignalEntity signal1 = createTestSignalEntity(SignalType.BLOCK, track1);
        signal1.setProtectedTrackIds(Arrays.asList("p-track-A", "p-track-B"));
        signalRepository.save(signal1);

        SignalEntity signal2 = createTestSignalEntity(SignalType.PATH, track2);
        signal2.setProtectedTrackIds(Arrays.asList("p-track-B", "p-track-C"));
        signalRepository.save(signal2);

        // リポジトリメソッドの実行
        List<SignalEntity> signals = signalRepository.findByProtectedTrackIdsContaining("p-track-B");

        // 検証
        assertThat(signals).hasSize(2);
        assertThat(signals).extracting(SignalEntity::getId).containsExactlyInAnyOrder(signal1.getId(), signal2.getId());
    }

    /**
     * 設置されている線路IDによる信号機の検索テスト
     * 特定の線路IDに設置されている信号機が全て取得できることを確認
     */
    @Test
    void findByTrack_Id_shouldReturnSignals() {
        // テストデータの準備
        TrackEntity trackA = createTestTrackEntity("owner-8", 100.0, 120.0, null, null);
        TrackEntity trackB = createTestTrackEntity("owner-8", 100.0, 120.0, null, null);
        trackRepository.saveAll(Arrays.asList(trackA, trackB));

        SignalEntity signal12 = signalRepository.save(createTestSignalEntity(SignalType.BLOCK, trackA));
        signalRepository.save(createTestSignalEntity(SignalType.PATH, trackB));
        SignalEntity signal14 = signalRepository.save(createTestSignalEntity(SignalType.BLOCK, trackA));

        // リポジトリメソッドの実行
        List<SignalEntity> signals = signalRepository.findByTrack_Id(trackA.getId());

        // 検証
        assertThat(signals).hasSize(2);
        assertThat(signals).extracting(SignalEntity::getId).containsExactlyInAnyOrder(signal12.getId(), signal14.getId());
    }

    // ヘルパーメソッド：テスト用のTrackEntityを作成
    private TrackEntity createTestTrackEntity(String ownerId, Double length, Double maxSpeed, String startJunctionId, String endJunctionId) {
        TrackEntity entity = new TrackEntity();
        entity.setOwnerId(ownerId);
        entity.setLength(length);
        entity.setMaxSpeed(maxSpeed);
        entity.setStartJunctionId(startJunctionId);
        entity.setEndJunctionId(endJunctionId);
        return entity;
    }

    // ヘルパーメソッド：テスト用のSignalEntityを作成
    private SignalEntity createTestSignalEntity(SignalType signalType, TrackEntity track) {
        SignalEntity signal = new SignalEntity();
        signal.setSignalType(signalType);
        LocationEmbeddable position = new LocationEmbeddable();
        position.setX(1.0); position.setY(2.0); position.setZ(3.0);
        signal.setPosition(position);
        signal.setTrack(track);
        return signal;
    }
}
