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
        StationEntity station = createTestStationEntity("駅1", "owner-1", 1000, 1.0, 2.0);
        stationRepository.save(station);

        PlatformEntity platform = createTestPlatformEntity("track-1", 200, station);

        // リポジトリメソッドの実行
        PlatformEntity savedPlatform = platformRepository.save(platform);

        // 検証
        assertThat(savedPlatform).isNotNull();
        assertThat(savedPlatform.getId()).isNotNull(); // IDは自動生成される
        assertThat(savedPlatform.getStation().getId()).isEqualTo(station.getId());
    }

    /**
     * IDによるプラットフォームの取得テスト
     * 存在するIDでプラットフォームが取得できることを確認
     */
    @Test
    void findById_shouldReturnPlatform_whenPlatformExists() {
        // テストデータの準備
        StationEntity station = createTestStationEntity("駅2", "owner-2", 1000, 1.0, 2.0);
        stationRepository.save(station);
        PlatformEntity platform = createTestPlatformEntity("track-2", 200, station);
        platformRepository.save(platform);

        // リポジトリメソッドの実行
        Optional<PlatformEntity> foundPlatform = platformRepository.findById(platform.getId());

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
        StationEntity station1 = createTestStationEntity("駅3", "owner-3", 1000, 1.0, 2.0);
        StationEntity station2 = createTestStationEntity("駅4", "owner-3", 1000, 1.0, 2.0);
        stationRepository.saveAll(Arrays.asList(station1, station2));

        platformRepository.save(createTestPlatformEntity("track-3", 200, station1));
        platformRepository.save(createTestPlatformEntity("track-4", 250, station2));

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
        StationEntity station = createTestStationEntity("駅5", "owner-4", 1000, 1.0, 2.0);
        stationRepository.save(station);
        PlatformEntity originalPlatform = createTestPlatformEntity("track-5", 200, station);
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
        Optional<PlatformEntity> foundPlatform = platformRepository.findById(originalPlatform.getId());
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
        StationEntity station = createTestStationEntity("駅6", "owner-5", 1000, 1.0, 2.0);
        stationRepository.save(station);
        PlatformEntity platform = createTestPlatformEntity("track-6", 200, station);
        platformRepository.save(platform);

        // リポジトリメソッドの実行
        platformRepository.deleteById(platform.getId());

        // 検証
        assertThat(platformRepository.findById(platform.getId())).isEmpty();
    }

    /**
     * 駅IDによるプラットフォームの検索テスト
     * 特定の駅IDに紐づくプラットフォームが全て取得できることを確認
     */
    @Test
    void findByStation_Id_shouldReturnPlatforms_whenPlatformsExist() {
        // テストデータの準備
        StationEntity stationA = createTestStationEntity("駅A", "owner-6", 1000, 1.0, 2.0);
        StationEntity stationB = createTestStationEntity("駅B", "owner-6", 1000, 1.0, 2.0);
        stationRepository.saveAll(Arrays.asList(stationA, stationB));

        PlatformEntity platform7 = platformRepository.save(createTestPlatformEntity("track-7", 200, stationA));
        platformRepository.save(createTestPlatformEntity("track-8", 250, stationB));
        PlatformEntity platform9 = platformRepository.save(createTestPlatformEntity("track-9", 220, stationA));

        // リポジトリメソッドの実行
        List<PlatformEntity> platforms = platformRepository.findByStation_Id(stationA.getId());

        // 検証
        assertThat(platforms).hasSize(2);
        assertThat(platforms).extracting(PlatformEntity::getId).containsExactlyInAnyOrder(platform7.getId(), platform9.getId());
    }

    /**
     * 接続されている線路IDによるプラットフォームの検索テスト
     * 特定の線路IDに接続されているプラットフォームが全て取得できることを確認
     */
    @Test
    void findByConnectedTrackId_shouldReturnPlatforms() {
        // テストデータの準備
        StationEntity station = createTestStationEntity("駅7", "owner-7", 1000, 1.0, 2.0);
        stationRepository.save(station);

        PlatformEntity platform10 = platformRepository.save(createTestPlatformEntity("track-X", 200, station));
        platformRepository.save(createTestPlatformEntity("track-Y", 250, station));
        PlatformEntity platform12 = platformRepository.save(createTestPlatformEntity("track-X", 220, station));

        // リポジトリメソッドの実行
        List<PlatformEntity> platforms = platformRepository.findByConnectedTrackId("track-X");

        // 検証
        assertThat(platforms).hasSize(2);
        assertThat(platforms).extracting(PlatformEntity::getId).containsExactlyInAnyOrder(platform10.getId(), platform12.getId());
    }

    /**
     * 指定されたキャパシティ以上のプラットフォームの検索テスト
     * 指定されたキャパシティ以上のプラットフォームが全て取得できることを確認
     */
    @Test
    void findByCapacityGreaterThanEqual_shouldReturnPlatforms() {
        // テストデータの準備
        StationEntity station = createTestStationEntity("駅8", "owner-8", 1000, 1.0, 2.0);
        stationRepository.save(station);

        platformRepository.save(createTestPlatformEntity("track-A", 180, station));
        PlatformEntity platform14 = platformRepository.save(createTestPlatformEntity("track-B", 200, station));
        PlatformEntity platform15 = platformRepository.save(createTestPlatformEntity("track-C", 250, station));

        // リポジトリメソッドの実行
        List<PlatformEntity> platforms = platformRepository.findByCapacityGreaterThanEqual(200);

        // 検証
        assertThat(platforms).hasSize(2);
        assertThat(platforms).extracting(PlatformEntity::getId).containsExactlyInAnyOrder(platform14.getId(), platform15.getId());
    }

    // ヘルパーメソッド：テスト用のStationEntityを作成
    private StationEntity createTestStationEntity(String name, String ownerId, Integer totalCapacity, Double x, Double y) {
        StationEntity entity = new StationEntity();
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
    private PlatformEntity createTestPlatformEntity(String connectedTrackId, Integer capacity, StationEntity station) {
        PlatformEntity platform = new PlatformEntity();
        platform.setConnectedTrackId(connectedTrackId);
        platform.setCapacity(capacity);
        platform.setStation(station);
        return platform;
    }
}
