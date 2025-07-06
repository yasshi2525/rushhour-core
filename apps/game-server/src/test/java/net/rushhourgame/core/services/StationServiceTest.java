package net.rushhourgame.core.services;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import net.rushhourgame.core.database.entities.LocationEmbeddable;
import net.rushhourgame.core.database.entities.StationEntity;
import net.rushhourgame.core.database.repositories.StationRepository;
import net.rushhourgame.core.exceptions.EntityNotFoundException;
import net.rushhourgame.core.mappers.StationMapper;
import net.rushhourgame.models.common.Location;
import net.rushhourgame.models.railway.Corridor;
import net.rushhourgame.models.railway.Gate;
import net.rushhourgame.models.railway.Platform;
import net.rushhourgame.models.station.Station;

/**
 * StationServiceの統合テストクラス
 * Spring Bootのテストコンテキストを使用し、実際のデータベースとマッパーを検証
 */
@SpringBootTest
@Transactional // 各テストメソッドの後にトランザクションをロールバックし、データベースをクリーンアップ
class StationServiceTest {

    @Autowired
    private StationService stationService;

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private StationMapper stationMapper; // マッパーのテストも兼ねるため注入

    /**
     * 各テストの前にデータベースをクリーンアップ
     */
    @BeforeEach
    void setUp() {
        stationRepository.deleteAll();
    }

    /**
     * 駅の保存テスト
     * 新しい駅が正しく保存され、IDが割り当てられることを確認
     */
    @Test
    void saveStation_shouldPersistStationAndRelatedEntities() {
        // テストデータの準備
        String stationId = "station-1";
        Station station = createTestStation(stationId, "東京駅", "owner-1", 1000, 35.6895, 139.6917,
                Arrays.asList(createTestPlatform("platform-1", stationId, "track-1", 200)),
                Arrays.asList(createTestGate("gate-1", stationId, 50, 10.0, 35.6890, 139.6910)),
                Arrays.asList(createTestCorridor("corridor-1", stationId, 50.0, 5.0))
        );

        // サービスメソッドの実行
        Station savedStation = stationService.save(station);

        // 検証
        assertThat(savedStation).isNotNull();
        assertThat(savedStation.getId()).isEqualTo("station-1");
        assertThat(savedStation.getName()).isEqualTo("東京駅");
        assertThat(savedStation.getPlatforms()).hasSize(1);
        assertThat(savedStation.getGates()).hasSize(1);
        assertThat(savedStation.getCorridors()).hasSize(1);

        // データベースから直接取得して検証
        Optional<StationEntity> foundEntity = stationRepository.findById("station-1");
        assertThat(foundEntity).isPresent();
        assertThat(foundEntity.get().getPlatforms()).hasSize(1);
        assertThat(foundEntity.get().getGates()).hasSize(1);
        assertThat(foundEntity.get().getCorridors()).hasSize(1);
    }

    /**
     * IDによる駅の取得テスト
     * 存在するIDで駅が取得できることを確認
     */
    @Test
    void findById_shouldReturnStation_whenStationExists() {
        // テストデータの準備
        StationEntity entity = createTestStationEntity("station-2", "大阪駅", "owner-2", 800, 34.6937, 135.5022);
        stationRepository.save(entity);

        // サービスメソッドの実行
        Optional<Station> foundStation = stationService.findById("station-2");

        // 検証
        assertThat(foundStation).isPresent();
        assertThat(foundStation.get().getName()).isEqualTo("大阪駅");
    }

    /**
     * IDによる駅の取得テスト
     * 存在しないIDで駅が取得できないことを確認
     */
    @Test
    void findById_shouldReturnEmpty_whenStationDoesNotExist() {
        // サービスメソッドの実行
        Optional<Station> foundStation = stationService.findById("non-existent-id");

        // 検証
        assertThat(foundStation).isEmpty();
    }

    /**
     * 名前による駅の取得テスト
     * 存在する名前で駅が取得できることを確認
     */
    @Test
    void findByName_shouldReturnStation_whenStationExists() {
        // テストデータの準備
        StationEntity entity = createTestStationEntity("station-3", "名古屋駅", "owner-3", 700, 35.1707, 136.8816);
        stationRepository.save(entity);

        // サービスメソッドの実行
        Optional<Station> foundStation = stationService.findByName("名古屋駅");

        // 検証
        assertThat(foundStation).isPresent();
        assertThat(foundStation.get().getId()).isEqualTo("station-3");
    }

    /**
     * 名前による駅の取得テスト
     * 存在しない名前で駅が取得できないことを確認
     */
    @Test
    void findByName_shouldReturnEmpty_whenStationDoesNotExist() {
        // サービスメソッドの実行
        Optional<Station> foundStation = stationService.findByName("存在しない駅");

        // 検証
        assertThat(foundStation).isEmpty();
    }

    /**
     * 所有者IDによる駅の取得テスト
     * 特定の所有者IDに紐づく駅が全て取得できることを確認
     */
    @Test
    void findByOwnerId_shouldReturnStations_whenStationsExist() {
        // テストデータの準備
        stationRepository.save(createTestStationEntity("station-4", "札幌駅", "owner-4", 500, 43.0686, 141.3507));
        stationRepository.save(createTestStationEntity("station-5", "仙台駅", "owner-4", 600, 38.2682, 140.8701));
        stationRepository.save(createTestStationEntity("station-6", "福岡駅", "owner-5", 900, 33.5903, 130.4017));

        // サービスメソッドの実行
        List<Station> stations = stationService.findByOwnerId("owner-4");

        // 検証
        assertThat(stations).hasSize(2);
        assertThat(stations).extracting(Station::getOwnerId).containsOnly("owner-4");
    }

    /**
     * 全ての駅の取得テスト
     * データベースに存在する全ての駅が取得できることを確認
     */
    @Test
    void findAll_shouldReturnAllStations() {
        // テストデータの準備
        stationRepository.save(createTestStationEntity("station-7", "広島駅", "owner-6", 400, 34.3963, 132.4594));
        stationRepository.save(createTestStationEntity("station-8", "岡山駅", "owner-6", 300, 34.6617, 133.9167));

        // サービスメソッドの実行
        List<Station> stations = stationService.findAll();

        // 検証
        assertThat(stations).hasSize(2);
    }

    /**
     * 接続されている線路IDによる駅の検索テスト
     * 特定の線路に接続されている駅が取得できることを確認
     */
    @Test
    void findByConnectedTrackId_shouldReturnStations() {
        // テストデータの準備
        StationEntity station1 = createTestStationEntity("station-9", "京都駅", "owner-7", 750, 35.0000, 135.7000);
        station1.setConnectedTrackIds(Arrays.asList("track-A", "track-B"));
        stationRepository.save(station1);

        StationEntity station2 = createTestStationEntity("station-10", "新横浜駅", "owner-7", 650, 35.5000, 139.6000);
        station2.setConnectedTrackIds(Arrays.asList("track-B", "track-C"));
        stationRepository.save(station2);

        // サービスメソッドの実行
        List<Station> stations = stationService.findByConnectedTrackId("track-B");

        // 検証
        assertThat(stations).hasSize(2);
        assertThat(stations).extracting(Station::getName).containsExactlyInAnyOrder("京都駅", "新横浜駅");
    }

    /**
     * 指定された範囲内の駅の検索テスト
     * 特定の座標範囲内の駅が取得できることを確認
     */
    @Test
    void findByLocationRange_shouldReturnStationsInGivenRange() {
        // テストデータの準備
        stationRepository.save(createTestStationEntity("station-11", "新宿駅", "owner-8", 1200, 35.6895, 139.6917)); // 範囲内
        stationRepository.save(createTestStationEntity("station-12", "渋谷駅", "owner-8", 1100, 35.6581, 139.7017)); // 範囲内
        stationRepository.save(createTestStationEntity("station-13", "横浜駅", "owner-8", 1000, 35.4437, 139.6380)); // 範囲外

        // サービスメソッドの実行
        List<Station> stations = stationService.findByLocationRange(35.6, 35.7, 139.6, 139.8);

        // 検証
        assertThat(stations).hasSize(2);
        assertThat(stations).extracting(Station::getName).containsExactlyInAnyOrder("新宿駅", "渋谷駅");
    }

    /**
     * 駅の更新テスト
     * 既存の駅情報が正しく更新されることを確認
     */
    @Test
    void updateStation_shouldUpdateExistingStation() {
        // テストデータの準備
        StationEntity originalEntity = createTestStationEntity("station-14", "旧駅名", "owner-9", 500, 30.0, 130.0);
        stationRepository.save(originalEntity);

        Station updatedStation = stationMapper.toDomain(originalEntity);
        updatedStation.setName("新駅名");
        updatedStation.setTotalCapacity(600);
        updatedStation.getLocation().setX(31.0);

        // サービスメソッドの実行
        Station resultStation = stationService.update(updatedStation);

        // 検証
        assertThat(resultStation).isNotNull();
        assertThat(resultStation.getId()).isEqualTo("station-14");
        assertThat(resultStation.getName()).isEqualTo("新駅名");
        assertThat(resultStation.getTotalCapacity()).isEqualTo(600);
        assertThat(resultStation.getLocation().getX()).isEqualTo(31.0);

        // データベースから直接取得して検証
        Optional<StationEntity> foundEntity = stationRepository.findById("station-14");
        assertThat(foundEntity).isPresent();
        assertThat(foundEntity.get().getName()).isEqualTo("新駅名");
        assertThat(foundEntity.get().getTotalCapacity()).isEqualTo(600);
        assertThat(foundEntity.get().getLocation().getX()).isEqualTo(31.0);
    }

    /**
     * 駅の更新テスト
     * 存在しない駅を更新しようとした場合にEntityNotFoundExceptionがスローされることを確認
     */
    @Test
    void updateStation_shouldThrowException_whenStationDoesNotExist() {
        // 存在しない駅のドメインモデル
        Station nonExistentStation = createTestStation("non-existent-update-id", "架空駅", "owner-X", 100, 0.0, 0.0, null, null, null);

        // サービスメソッドの実行と例外の検証
        assertThrows(EntityNotFoundException.class, () -> stationService.update(nonExistentStation));
    }

    /**
     * 駅の削除テスト
     * 存在するIDの駅が正しく削除されることを確認
     */
    @Test
    void deleteById_shouldDeleteStation_whenStationExists() {
        // テストデータの準備
        StationEntity entity = createTestStationEntity("station-15", "削除対象駅", "owner-10", 200, 10.0, 10.0);
        stationRepository.save(entity);

        // サービスメソッドの実行
        stationService.deleteById("station-15");

        // 検証
        assertThat(stationRepository.findById("station-15")).isEmpty();
    }

    /**
     * 駅の存在チェックテスト
     * 存在するIDでtrueが返されることを確認
     */
    @Test
    void existsById_shouldReturnTrue_whenStationExists() {
        // テストデータの準備
        StationEntity entity = createTestStationEntity("station-16", "存在チェック駅", "owner-11", 150, 20.0, 20.0);
        stationRepository.save(entity);

        // サービスメソッドの実行と検証
        assertThat(stationService.existsById("station-16")).isTrue();
    }

    /**
     * 駅の存在チェックテスト
     * 存在しないIDでfalseが返されることを確認
     */
    @Test
    void existsById_shouldReturnFalse_whenStationDoesNotExist() {
        // サービスメソッドの実行と検証
        assertThat(stationService.existsById("non-existent-check-id")).isFalse();
    }

    // ヘルパーメソッド：テスト用のStationドメインモデルを作成
    private Station createTestStation(String id, String name, String ownerId, Integer totalCapacity,
                                      Double x, Double y, List<Platform> platforms, List<Gate> gates, List<Corridor> corridors) {
        Station station = new Station();
        station.setId(id);
        station.setName(name);
        station.setOwnerId(ownerId);
        station.setTotalCapacity(totalCapacity);
        station.setLocation(new Location(x, y, 0.0)); // Z座標はデフォルトで0.0
        if (platforms != null) {
            station.setPlatforms(platforms);
        }
        if (gates != null) {
            station.setGates(gates);
        }
        if (corridors != null) {
            station.setCorridors(corridors);
        }
        return station;
    }

    // ヘルパーメソッド：テスト用のPlatformドメインモデルを作成
    private Platform createTestPlatform(String id, String stationId, String connectedTrackId, Integer capacity) {
        Platform platform = new Platform();
        platform.setId(id);
        platform.setStationId(stationId);
        platform.setConnectedTrackId(connectedTrackId);
        platform.setCapacity(capacity);
        return platform;
    }

    // ヘルパーメソッド：テスト用のGateドメインモデルを作成
    private Gate createTestGate(String id, String stationId, Integer capacity, Double processingTime, Double x, Double y) {
        Gate gate = new Gate();
        gate.setId(id);
        gate.setStationId(stationId);
        gate.setCapacity(capacity);
        gate.setProcessingTime(processingTime);
        gate.setPosition(new Location(x, y, 0.0)); // Z座標はデフォルトで0.0
        return gate;
    }

    // ヘルパーメソッド：テスト用のCorridorドメインモデルを作成
    private Corridor createTestCorridor(String id, String stationId, Double length, Double width) {
        Corridor corridor = new Corridor();
        corridor.setId(id);
        corridor.setStationId(stationId);
        corridor.setLength(length);
        corridor.setWidth(width);
        return corridor;
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
}
