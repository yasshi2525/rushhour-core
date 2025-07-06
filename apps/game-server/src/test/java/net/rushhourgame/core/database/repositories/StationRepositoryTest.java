package net.rushhourgame.core.database.repositories;

import net.rushhourgame.core.database.entities.LocationEmbeddable;
import net.rushhourgame.core.database.entities.StationEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;

/**
 * StationRepositoryのテストクラス
 * 駅エンティティの永続化とクエリ機能をテストする
 */
@DataJpaTest
@ActiveProfiles("test")
class StationRepositoryTest {

    @Autowired
    private StationRepository stationRepository;

    private StationEntity testStation;

    @BeforeEach
    void setUp() {
        // テストデータの準備
        testStation = new StationEntity();
        // ID is auto-generated, no need to set manually
        testStation.setName("テスト駅");
        testStation.setOwnerId("player-001");
        testStation.setTotalCapacity(1000);
        
        LocationEmbeddable location = new LocationEmbeddable();
        location.setX(35.6812);
        location.setY(139.7671);
        location.setZ(0.0);
        testStation.setLocation(location);
    }

    @Test
    @DisplayName("駅エンティティの保存と取得が正しく動作すること")
    void testSaveAndFindById() {
        // 実行
        StationEntity saved = stationRepository.save(testStation);
        
        // 検証
        assertNotNull(saved.getId());
        assertThat(saved.getName()).isEqualTo("テスト駅");
        assertThat(saved.getOwnerId()).isEqualTo("player-001");
        assertThat(saved.getTotalCapacity()).isEqualTo(1000);
        assertThat(saved.getLocation().getX()).isEqualTo(35.6812);
        assertThat(saved.getLocation().getY()).isEqualTo(139.7671);
        
        // IDで再取得
        Optional<StationEntity> found = stationRepository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertThat(found.get().getName()).isEqualTo("テスト駅");
    }

    @Test
    @DisplayName("所有者IDで駅を検索できること")
    void testFindByOwnerId() {
        // 複数の駅を作成
        StationEntity station1 = createStation("駅1", "player-001", 100.0, 100.0);
        StationEntity station2 = createStation("駅2", "player-001", 200.0, 200.0);
        StationEntity station3 = createStation("駅3", "player-002", 300.0, 300.0);
        
        stationRepository.save(station1);
        stationRepository.save(station2);
        stationRepository.save(station3);
        
        // 実行
        List<StationEntity> player1Stations = stationRepository.findByOwnerId("player-001");
        List<StationEntity> player2Stations = stationRepository.findByOwnerId("player-002");
        
        // 検証
        assertThat(player1Stations).hasSize(2);
        assertThat(player2Stations).hasSize(1);
        assertThat(player1Stations.stream().map(StationEntity::getName))
                .containsExactlyInAnyOrder("駅1", "駅2");
    }

    @Test
    @DisplayName("座標範囲内の駅を検索できること")
    void testFindByLocationBounds() {
        // テストデータ作成
        StationEntity station1 = createStation("東京駅", "player-001", 35.6812, 139.7671);
        StationEntity station2 = createStation("新宿駅", "player-001", 35.6896, 139.7006);
        StationEntity station3 = createStation("大阪駅", "player-002", 34.7024, 135.4959);
        
        stationRepository.save(station1);
        stationRepository.save(station2);
        stationRepository.save(station3);
        
        // 東京周辺の範囲で検索
        double minX = 35.6;
        double maxX = 35.7;
        double minY = 139.6;
        double maxY = 139.8;
        
        List<StationEntity> tokyoAreaStations = stationRepository.findByLocationRange(
                minX, maxX, minY, maxY);
        
        // 検証
        assertThat(tokyoAreaStations).hasSize(2);
        assertThat(tokyoAreaStations.stream().map(StationEntity::getName))
                .containsExactlyInAnyOrder("東京駅", "新宿駅");
    }

    @Test
    @DisplayName("駅の更新が正しく動作すること")
    void testUpdate() {
        // 保存
        StationEntity saved = stationRepository.save(testStation);
        String originalId = saved.getId();
        
        // 更新
        saved.setName("更新後の駅");
        saved.setTotalCapacity(2000);
        StationEntity updated = stationRepository.save(saved);
        
        // 検証
        assertThat(updated.getId()).isEqualTo(originalId);
        assertThat(updated.getName()).isEqualTo("更新後の駅");
        assertThat(updated.getTotalCapacity()).isEqualTo(2000);
    }

    @Test
    @DisplayName("駅の削除が正しく動作すること")
    void testDelete() {
        // 保存
        StationEntity saved = stationRepository.save(testStation);
        String id = saved.getId();
        
        // 削除前の確認
        assertTrue(stationRepository.existsById(id));
        
        // 削除
        stationRepository.deleteById(id);
        
        // 削除後の確認
        assertFalse(stationRepository.existsById(id));
        Optional<StationEntity> deleted = stationRepository.findById(id);
        assertFalse(deleted.isPresent());
    }

    @Test
    @DisplayName("容量でソートして駅を取得できること")
    void testFindAllOrderByCapacity() {
        // テストデータ作成
        StationEntity small = createStation("小駅", "player-001", 100.0, 100.0);
        small.setTotalCapacity(100);
        
        StationEntity medium = createStation("中駅", "player-001", 200.0, 200.0);
        medium.setTotalCapacity(500);
        
        StationEntity large = createStation("大駅", "player-001", 300.0, 300.0);
        large.setTotalCapacity(1000);
        
        stationRepository.save(medium);
        stationRepository.save(large);
        stationRepository.save(small);
        
        // 実行
        List<StationEntity> allStations = stationRepository.findAll();
        allStations.sort((s1, s2) -> s2.getTotalCapacity().compareTo(s1.getTotalCapacity()));
        
        // 検証
        assertThat(allStations).hasSize(3);
        assertThat(allStations.get(0).getName()).isEqualTo("大駅");
        assertThat(allStations.get(1).getName()).isEqualTo("中駅");
        assertThat(allStations.get(2).getName()).isEqualTo("小駅");
    }

    @Test
    @DisplayName("名前で駅を検索できること")
    void testFindByName() {
        // テストデータ作成
        stationRepository.save(createStation("東京駅", "player-001", 100.0, 100.0));
        stationRepository.save(createStation("新宿駅", "player-001", 200.0, 200.0));
        stationRepository.save(createStation("東京タワー駅", "player-002", 300.0, 300.0));
        
        // 実行
        Optional<StationEntity> station = stationRepository.findByName("東京駅");
        
        // 検証
        assertTrue(station.isPresent());
        assertThat(station.get().getName()).isEqualTo("東京駅");
    }

    @Test
    @DisplayName("接続された線路IDで駅を検索できること")
    void testFindByConnectedTrackId() {
        // テストデータ作成
        StationEntity station1 = createStation("駅A", "player-001", 100.0, 100.0);
        station1.getConnectedTrackIds().add("track-001");
        station1.getConnectedTrackIds().add("track-002");
        
        StationEntity station2 = createStation("駅B", "player-001", 200.0, 200.0);
        station2.getConnectedTrackIds().add("track-002");
        station2.getConnectedTrackIds().add("track-003");
        
        StationEntity station3 = createStation("駅C", "player-001", 300.0, 300.0);
        station3.getConnectedTrackIds().add("track-003");
        
        stationRepository.save(station1);
        stationRepository.save(station2);
        stationRepository.save(station3);
        
        // 実行
        List<StationEntity> stationsConnectedToTrack2 = stationRepository.findByConnectedTrackId("track-002");
        
        // 検証
        assertThat(stationsConnectedToTrack2).hasSize(2);
        assertThat(stationsConnectedToTrack2.stream().map(StationEntity::getName))
                .containsExactlyInAnyOrder("駅A", "駅B");
    }

    /**
     * テスト用の駅エンティティを作成するヘルパーメソッド
     */
    private StationEntity createStation(String name, String ownerId, double x, double y) {
        StationEntity station = new StationEntity();
        station.setName(name);
        station.setOwnerId(ownerId);
        station.setTotalCapacity(1000);
        
        LocationEmbeddable location = new LocationEmbeddable();
        location.setX(x);
        location.setY(y);
        location.setZ(0.0);
        station.setLocation(location);
        
        return station;
    }
}