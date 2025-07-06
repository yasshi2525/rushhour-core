package net.rushhourgame.core.database.repositories;

import net.rushhourgame.core.database.entities.PlatformEntity;
import net.rushhourgame.core.database.entities.StationEntity;
import net.rushhourgame.core.database.entities.LocationEmbeddable;
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
 * PlatformRepositoryの統合テストクラス
 * DataJpaTestアノテーションを使用し、JPA関連のコンポーネントのみをロード
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY) // インメモリデータベースを使用
@Transactional // 各テストメソッドの後にトランザクションをロールバックし、データベースをクリーンアップ
class PlatformRepositoryTest {

    @Autowired
    private PlatformRepository platformRepository;

    @Autowired
    private StationRepository stationRepository; // 関連エンティティの保存用

    /**
     * 各テストの前にデータベースをクリーンアップ
     */
    @BeforeEach
    void setUp() {
        platformRepository.deleteAll();
        stationRepository.deleteAll();
    }

    /**
     * プラットフォームの保存テスト
     * 新しいプラットフォームが正しく保存され、IDが割り当てられることを確認
     */
    @Test
    void savePlatform_shouldPersistPlatform() {
        // テストデータの準備
        StationEntity station = createTestStationEntity("station-1", "駅1", "owner-1", 1000, 1.0, 2.0);
        stationRepository.save(station);

        PlatformEntity platform = createTestPlatformEntity("platform-1", "track-1", 200, station);

        // リポジトリメソッドの実行
        PlatformEntity savedPlatform = platformRepository.save(platform);

        // 検証
        assertThat(savedPlatform).isNotNull();
        assertThat(savedPlatform.getId()).isEqualTo("platform-1");
        assertThat(savedPlatform.getStation().getId()).isEqualTo("station-1");
    }

    /**
     * IDによるプラットフォームの取得テスト
     * 存在するIDでプラットフォームが取得できることを確認
     */
    @Test
    void findById_shouldReturnPlatform_whenPlatformExists() {
        // テストデータの準備
        StationEntity station = createTestStationEntity("station-2", "駅2", "owner-2", 1000, 1.0, 2.0);
        stationRepository.save(station);
        PlatformEntity platform = createTestPlatformEntity("platform-2", "track-2", 200, station);
        platformRepository.save(platform);

        // リポジトリメソッドの実行
        Optional<PlatformEntity> foundPlatform = platformRepository.findById("platform-2");

        // 検証
        assertThat(foundPlatform).isPresent();
        assertThat(foundPlatform.get().getConnectedTrackId()).isEqualTo("track-2");
    }

    /**
     * IDによるプラットフォームの取得テスト
     * 存在しないIDでプラットフォームが取得できないことを確認
     */
    @Test
    void findById_shouldReturnEmpty_whenPlatformDoesNotExist() {
        // リポジトリメソッドの実行
        Optional<PlatformEntity> foundPlatform = platformRepository.findById("non-existent-platform");

        // 検証
        assertThat(foundPlatform).isEmpty();
    }

    /**
     * 全てのプラットフォームの取得テスト
     * データベースに存在する全てのプラットフォームが取得できることを確認
     */
    @Test
    void findAll_shouldReturnAllPlatforms() {
        // テストデータの準備
        StationEntity station1 = createTestStationEntity("station-3", "駅3", "owner-3", 1000, 1.0, 2.0);
        StationEntity station2 = createTestStationEntity("station-4", "駅4", "owner-3", 1000, 1.0, 2.0);
        stationRepository.saveAll(Arrays.asList(station1, station2));

        platformRepository.save(createTestPlatformEntity("platform-3", "track-3", 200, station1));
        platformRepository.save(createTestPlatformEntity("platform-4", "track-4", 250, station2));

        // リポジトリメソッドの実行
        List<PlatformEntity> platforms = platformRepository.findAll();

        // 検証
        assertThat(platforms).hasSize(2);
    }

    /**
     * プラットフォームの更新テスト
     * 既存のプラットフォーム情報が正しく更新されることを確認
     */
    @Test
    void updatePlatform_shouldUpdateExistingPlatform() {
        // テストデータの準備
        StationEntity station = createTestStationEntity("station-5", "駅5", "owner-4", 1000, 1.0, 2.0);
        stationRepository.save(station);
        PlatformEntity originalPlatform = createTestPlatformEntity("platform-5", "track-5", 200, station);
        platformRepository.save(originalPlatform);

        // 更新データの準備
        originalPlatform.setCapacity(220);
        originalPlatform.setConnectedTrackId("track-5-updated");

        // リポジトリメソッドの実行
        PlatformEntity updatedPlatform = platformRepository.save(originalPlatform);

        // 検証
        assertThat(updatedPlatform.getCapacity()).isEqualTo(220);
        assertThat(updatedPlatform.getConnectedTrackId()).isEqualTo("track-5-updated");

        // データベースから直接取得して検証
        Optional<PlatformEntity> foundPlatform = platformRepository.findById("platform-5");
        assertThat(foundPlatform).isPresent();
        assertThat(foundPlatform.get().getCapacity()).isEqualTo(220);
    }

    /**
     * プラットフォームの削除テスト
     * 存在するIDのプラットフォームが正しく削除されることを確認
     */
    @Test
    void deleteById_shouldDeletePlatform_whenPlatformExists() {
        // テストデータの準備
        StationEntity station = createTestStationEntity("station-6", "駅6", "owner-5", 1000, 1.0, 2.0);
        stationRepository.save(station);
        PlatformEntity platform = createTestPlatformEntity("platform-6", "track-6", 200, station);
        platformRepository.save(platform);

        // リポジトリメソッドの実行
        platformRepository.deleteById("platform-6");

        // 検証
        assertThat(platformRepository.findById("platform-6")).isEmpty();
    }

    /**
     * 駅IDによるプラットフォームの検索テスト
     * 特定の駅IDに紐づくプラットフォームが全て取得できることを確認
     */
    @Test
    void findByStation_Id_shouldReturnPlatforms_whenPlatformsExist() {
        // テストデータの準備
        StationEntity stationA = createTestStationEntity("station-A", "駅A", "owner-6", 1000, 1.0, 2.0);
        StationEntity stationB = createTestStationEntity("station-B", "駅B", "owner-6", 1000, 1.0, 2.0);
        stationRepository.saveAll(Arrays.asList(stationA, stationB));

        platformRepository.save(createTestPlatformEntity("platform-7", "track-7", 200, stationA));
        platformRepository.save(createTestPlatformEntity("platform-8", "track-8", 250, stationB));
        platformRepository.save(createTestPlatformEntity("platform-9", "track-9", 220, stationA));

        // リポジトリメソッドの実行
        List<PlatformEntity> platforms = platformRepository.findByStation_Id("station-A");

        // 検証
        assertThat(platforms).hasSize(2);
        assertThat(platforms).extracting(PlatformEntity::getId).containsExactlyInAnyOrder("platform-7", "platform-9");
    }

    /**
     * 接続されている線路IDによるプラットフォームの検索テスト
     * 特定の線路IDに接続されているプラットフォームが全て取得できることを確認
     */
    @Test
    void findByConnectedTrackId_shouldReturnPlatforms() {
        // テストデータの準備
        StationEntity station = createTestStationEntity("station-7", "駅7", "owner-7", 1000, 1.0, 2.0);
        stationRepository.save(station);

        platformRepository.save(createTestPlatformEntity("platform-10", "track-X", 200, station));
        platformRepository.save(createTestPlatformEntity("platform-11", "track-Y", 250, station));
        platformRepository.save(createTestPlatformEntity("platform-12", "track-X", 220, station));

        // リポジトリメソッドの実行
        List<PlatformEntity> platforms = platformRepository.findByConnectedTrackId("track-X");

        // 検証
        assertThat(platforms).hasSize(2);
        assertThat(platforms).extracting(PlatformEntity::getId).containsExactlyInAnyOrder("platform-10", "platform-12");
    }

    /**
     * 指定されたキャパシティ以上のプラットフォームの検索テスト
     * 指定されたキャパシティ以上のプラットフォームが全て取得できることを確認
     */
    @Test
    void findByCapacityGreaterThanEqual_shouldReturnPlatforms() {
        // テストデータの準備
        StationEntity station = createTestStationEntity("station-8", "駅8", "owner-8", 1000, 1.0, 2.0);
        stationRepository.save(station);

        platformRepository.save(createTestPlatformEntity("platform-13", "track-A", 180, station));
        platformRepository.save(createTestPlatformEntity("platform-14", "track-B", 200, station));
        platformRepository.save(createTestPlatformEntity("platform-15", "track-C", 250, station));

        // リポジトリメソッドの実行
        List<PlatformEntity> platforms = platformRepository.findByCapacityGreaterThanEqual(200);

        // 検証
        assertThat(platforms).hasSize(2);
        assertThat(platforms).extracting(PlatformEntity::getId).containsExactlyInAnyOrder("platform-14", "platform-15");
    }

    // ヘルパーメソッド：テスト用のStationEntityを作成
    private StationEntity createTestStationEntity(String id, String name, String ownerId, Integer totalCapacity, Double x, Double y) {
        StationEntity entity = new StationEntity();
        entity.setId(id);
        entity.setName(name);
        entity.setOwnerId(ownerId);
        entity.setTotalCapacity(totalCapacity);
        LocationEmbeddable location = new LocationEmbeddable();
        location.setX(x);
        location.setY(y);
        location.setZ(0.0);
        entity.setLocation(location);
        return entity;
    }

    // ヘルパーメソッド：テスト用のPlatformEntityを作成
    private PlatformEntity createTestPlatformEntity(String id, String connectedTrackId, Integer capacity, StationEntity station) {
        PlatformEntity platform = new PlatformEntity();
        platform.setId(id);
        platform.setConnectedTrackId(connectedTrackId);
        platform.setCapacity(capacity);
        platform.setStation(station);
        return platform;
    }
}
