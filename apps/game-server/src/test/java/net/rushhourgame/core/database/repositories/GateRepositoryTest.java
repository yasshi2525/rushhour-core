package net.rushhourgame.core.database.repositories;

import net.rushhourgame.core.database.entities.GateEntity;
import net.rushhourgame.core.database.entities.LocationEmbeddable;
import net.rushhourgame.core.database.entities.StationEntity;
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
 * GateRepositoryの統合テストクラス
 * DataJpaTestアノテーションを使用し、JPA関連のコンポーネントのみをロード
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY) // インメモリデータベースを使用
@Transactional // 各テストメソッドの後にトランザクションをロールバックし、データベースをクリーンアップ
class GateRepositoryTest {

    @Autowired
    private GateRepository gateRepository;

    @Autowired
    private StationRepository stationRepository; // 関連エンティティの保存用

    /**
     * 各テストの前にデータベースをクリーンアップ
     */
    @BeforeEach
    void setUp() {
        gateRepository.deleteAll();
        stationRepository.deleteAll();
    }

    /**
     * 改札の保存テスト
     * 新しい改札が正しく保存され、IDが割り当てられることを確認
     */
    @Test
    void saveGate_shouldPersistGate() {
        // テストデータの準備
        StationEntity station = createTestStationEntity("駅1", "owner-1", 1000, 1.0, 2.0);
        stationRepository.save(station);

        GateEntity gate = createTestGateEntity(100, 5.0, station);

        // リポジトリメソッドの実行
        GateEntity savedGate = gateRepository.save(gate);

        // 検証
        assertThat(savedGate).isNotNull();
        assertThat(savedGate.getId()).isNotNull(); // IDは自動生成される
        assertThat(savedGate.getStation().getId()).isEqualTo(station.getId());
    }

    /**
     * IDによる改札の取得テスト
     * 存在するIDで改札が取得できることを確認
     */
    @Test
    void findById_shouldReturnGate_whenGateExists() {
        // テストデータの準備
        StationEntity station = createTestStationEntity("駅2", "owner-2", 1000, 1.0, 2.0);
        stationRepository.save(station);
        GateEntity gate = createTestGateEntity(100, 5.0, station);
        gateRepository.save(gate);

        // リポジトリメソッドの実行
        Optional<GateEntity> foundGate = gateRepository.findById(gate.getId());

        // 検証
        assertThat(foundGate).isPresent();
        assertThat(foundGate.get().getCapacity()).isEqualTo(100);
    }

    /**
     * IDによる改札の取得テスト
     * 存在しないIDで改札が取得できないことを確認
     */
    @Test
    void findById_shouldReturnEmpty_whenGateDoesNotExist() {
        // リポジトリメソッドの実行
        Optional<GateEntity> foundGate = gateRepository.findById("non-existent-gate");

        // 検証
        assertThat(foundGate).isEmpty();
    }

    /**
     * 全ての改札の取得テスト
     * データベースに存在する全ての改札が取得できることを確認
     */
    @Test
    void findAll_shouldReturnAllGates() {
        // テストデータの準備
        StationEntity station1 = createTestStationEntity("駅3", "owner-3", 1000, 1.0, 2.0);
        StationEntity station2 = createTestStationEntity("駅4", "owner-3", 1000, 1.0, 2.0);
        stationRepository.saveAll(Arrays.asList(station1, station2));

        gateRepository.save(createTestGateEntity(100, 5.0, station1));
        gateRepository.save(createTestGateEntity(120, 6.0, station2));

        // リポジトリメソッドの実行
        List<GateEntity> gates = gateRepository.findAll();

        // 検証
        assertThat(gates).hasSize(2);
    }

    /**
     * 改札の更新テスト
     * 既存の改札情報が正しく更新されることを確認
     */
    @Test
    void updateGate_shouldUpdateExistingGate() {
        // テストデータの準備
        StationEntity station = createTestStationEntity("駅5", "owner-4", 1000, 1.0, 2.0);
        stationRepository.save(station);
        GateEntity originalGate = createTestGateEntity(100, 5.0, station);
        gateRepository.save(originalGate);

        // 更新データの準備
        originalGate.setCapacity(150);
        originalGate.setProcessingTime(4.5);

        // リポジトリメソッドの実行
        GateEntity updatedGate = gateRepository.save(originalGate);

        // 検証
        assertThat(updatedGate.getCapacity()).isEqualTo(150);
        assertThat(updatedGate.getProcessingTime()).isEqualTo(4.5);

        // データベースから直接取得して検証
        Optional<GateEntity> foundGate = gateRepository.findById(originalGate.getId());
        assertThat(foundGate).isPresent();
        assertThat(foundGate.get().getCapacity()).isEqualTo(150);
    }

    /**
     * 改札の削除テスト
     * 存在するIDの改札が正しく削除されることを確認
     */
    @Test
    void deleteById_shouldDeleteGate_whenGateExists() {
        // テストデータの準備
        StationEntity station = createTestStationEntity("駅6", "owner-5", 1000, 1.0, 2.0);
        stationRepository.save(station);
        GateEntity gate = createTestGateEntity(100, 5.0, station);
        gateRepository.save(gate);

        // リポジトリメソッドの実行
        gateRepository.deleteById(gate.getId());

        // 検証
        assertThat(gateRepository.findById(gate.getId())).isEmpty();
    }

    /**
     * 駅IDによる改札の検索テスト
     * 特定の駅IDに紐づく改札が全て取得できることを確認
     */
    @Test
    void findByStation_Id_shouldReturnGates_whenGatesExist() {
        // テストデータの準備
        StationEntity stationA = createTestStationEntity("駅A", "owner-6", 1000, 1.0, 2.0);
        StationEntity stationB = createTestStationEntity("駅B", "owner-6", 1000, 1.0, 2.0);
        stationRepository.saveAll(Arrays.asList(stationA, stationB));

        GateEntity gate7 = gateRepository.save(createTestGateEntity(100, 5.0, stationA));
        gateRepository.save(createTestGateEntity(120, 6.0, stationB));
        GateEntity gate9 = gateRepository.save(createTestGateEntity(110, 5.5, stationA));

        // リポジトリメソッドの実行
        List<GateEntity> gates = gateRepository.findByStation_Id(stationA.getId());

        // 検証
        assertThat(gates).hasSize(2);
        assertThat(gates).extracting(GateEntity::getId).containsExactlyInAnyOrder(gate7.getId(), gate9.getId());
    }

    /**
     * 処理時間以上の改札の検索テスト
     * 指定された処理時間以上の改札が全て取得できることを確認
     */
    @Test
    void findByProcessingTimeGreaterThanEqual_shouldReturnGates() {
        // テストデータの準備
        StationEntity station = createTestStationEntity("駅7", "owner-7", 1000, 1.0, 2.0);
        stationRepository.save(station);

        GateEntity gate10 = gateRepository.save(createTestGateEntity(100, 5.0, station));
        GateEntity gate11 = gateRepository.save(createTestGateEntity(120, 6.0, station));
        gateRepository.save(createTestGateEntity(90, 4.0, station));

        // リポジトリメソッドの実行
        List<GateEntity> gates = gateRepository.findByProcessingTimeGreaterThanEqual(5.0);

        // 検証
        assertThat(gates).hasSize(2);
        assertThat(gates).extracting(GateEntity::getId).containsExactlyInAnyOrder(gate10.getId(), gate11.getId());
    }

    /**
     * 指定されたキャパシティ範囲内の改札の検索テスト
     * 指定されたキャパシティ範囲内の改札が全て取得できることを確認
     */
    @Test
    void findByCapacityBetween_shouldReturnGates() {
        // テストデータの準備
        StationEntity station = createTestStationEntity("駅8", "owner-8", 1000, 1.0, 2.0);
        stationRepository.save(station);

        gateRepository.save(createTestGateEntity(80, 5.0, station));
        GateEntity gate14 = gateRepository.save(createTestGateEntity(100, 5.0, station));
        GateEntity gate15 = gateRepository.save(createTestGateEntity(120, 5.0, station));
        gateRepository.save(createTestGateEntity(150, 5.0, station));

        // リポジトリメソッドの実行
        List<GateEntity> gates = gateRepository.findByCapacityBetween(90, 130);

        // 検証
        assertThat(gates).hasSize(2);
        assertThat(gates).extracting(GateEntity::getId).containsExactlyInAnyOrder(gate14.getId(), gate15.getId());
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

    // ヘルパーメソッド：テスト用のGateEntityを作成
    private GateEntity createTestGateEntity(Integer capacity, Double processingTime, StationEntity station) {
        GateEntity gate = new GateEntity();
        gate.setCapacity(capacity);
        gate.setProcessingTime(processingTime);
        LocationEmbeddable position = new LocationEmbeddable();
        position.setX(1.0); position.setY(2.0); position.setZ(3.0);
        gate.setPosition(position);
        gate.setStation(station);
        return gate;
    }
}
