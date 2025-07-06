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

import net.rushhourgame.core.database.entities.CarEntity;
import net.rushhourgame.core.database.entities.ScheduleEntity;
import net.rushhourgame.core.database.entities.TrainEntity;
import net.rushhourgame.models.common.TrainType;

/**
 * TrainRepositoryの統合テストクラス
 * DataJpaTestアノテーションを使用し、JPA関連のコンポーネントのみをロード
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY) // インメモリデータベースを使用
@Transactional // 各テストメソッドの後にトランザクションをロールバックし、データベースをクリーンアップ
class TrainRepositoryTest {

    @Autowired
    private TrainRepository trainRepository;

    /**
     * 各テストの前にデータベースをクリーンアップ
     */
    @BeforeEach
    void setUp() {
        trainRepository.deleteAll();
    }

    /**
     * 電車の保存テスト
     * 新しい電車が正しく保存され、IDが割り当てられることを確認
     */
    @Test
    void saveTrain_shouldPersistTrainAndRelatedEntities() {
        // テストデータの準備
        TrainEntity train = createTestTrainEntity("owner-1", TrainType.EXPRESS, null, 500, 8, true, "route-1");

        // 車両の追加
        CarEntity car1 = createTestCarEntity(100, 4, train);
        CarEntity car2 = createTestCarEntity(100, 4, train);
        train.setCars(Arrays.asList(car1, car2));

        // スケジュールの追加
        ScheduleEntity schedule = createTestScheduleEntity("route-1", train);
        train.setSchedule(schedule);

        // リポジトリメソッドの実行
        TrainEntity savedTrain = trainRepository.save(train);

        // 検証
        assertThat(savedTrain).isNotNull();
        assertThat(savedTrain.getId()).isNotNull(); // IDは自動生成される
        assertThat(savedTrain.getCars()).hasSize(2);
        assertThat(savedTrain.getSchedule()).isNotNull();

        // データベースから直接取得して検証
        Optional<TrainEntity> foundTrain = trainRepository.findById(savedTrain.getId());
        assertThat(foundTrain).isPresent();
        assertThat(foundTrain.get().getCars()).hasSize(2);
        assertThat(foundTrain.get().getSchedule()).isNotNull();
    }

    /**
     * IDによる電車の取得テスト
     * 存在するIDで電車が取得できることを確認
     */
    @Test
    void findById_shouldReturnTrain_whenTrainExists() {
        // テストデータの準備
        TrainEntity train = createTestTrainEntity("owner-2", TrainType.LOCAL, null, 300, 6, false, null);
        trainRepository.save(train);

        // リポジトリメソッドの実行
        Optional<TrainEntity> foundTrain = trainRepository.findById(train.getId());

        // 検証
        assertThat(foundTrain).isPresent();
        assertThat(foundTrain.get().getTrainType()).isEqualTo(TrainType.LOCAL);
    }

    /**
     * IDによる電車の取得テスト
     * 存在しないIDで電車が取得できないことを確認
     */
    @Test
    void findById_shouldReturnEmpty_whenTrainDoesNotExist() {
        // リポジトリメソッドの実行
        Optional<TrainEntity> foundTrain = trainRepository.findById("non-existent-train");

        // 検証
        assertThat(foundTrain).isEmpty();
    }

    /**
     * 全ての電車の取得テスト
     * データベースに存在する全ての電車が取得できることを確認
     */
    @Test
    void findAll_shouldReturnAllTrains() {
        // テストデータの準備
        trainRepository.save(createTestTrainEntity("owner-3", TrainType.RAPID, null, 400, 8, true, "route-2"));
        trainRepository.save(createTestTrainEntity("owner-3", TrainType.EXPRESS, null, 600, 10, false, "route-3"));

        // リポジトリメソッドの実行
        List<TrainEntity> trains = trainRepository.findAll();

        // 検証
        assertThat(trains).hasSize(2);
    }

    /**
     * 電車の更新テスト
     * 既存の電車情報が正しく更新されることを確認
     */
    @Test
    void updateTrain_shouldUpdateExistingTrain() {
        // テストデータの準備
        TrainEntity originalTrain = createTestTrainEntity("owner-4", TrainType.LOCAL, null, 200, 4, false, null);
        trainRepository.save(originalTrain);

        // 更新データの準備
        originalTrain.setTrainType(TrainType.EXPRESS);
        originalTrain.setTotalCapacity(250);

        // リポジトリメソッドの実行
        TrainEntity updatedTrain = trainRepository.save(originalTrain);

        // 検証
        assertThat(updatedTrain.getTrainType()).isEqualTo(TrainType.EXPRESS);
        assertThat(updatedTrain.getTotalCapacity()).isEqualTo(250);

        // データベースから直接取得して検証
        Optional<TrainEntity> foundTrain = trainRepository.findById(originalTrain.getId());
        assertThat(foundTrain).isPresent();
        assertThat(foundTrain.get().getTrainType()).isEqualTo(TrainType.EXPRESS);
    }

    /**
     * 電車の削除テスト
     * 存在するIDの電車が正しく削除されることを確認
     */
    @Test
    void deleteById_shouldDeleteTrain_whenTrainExists() {
        // テストデータの準備
        TrainEntity train = createTestTrainEntity("owner-5", TrainType.LOCAL, null, 150, 4, true, null);
        trainRepository.save(train);

        // リポジトリメソッドの実行
        trainRepository.deleteById(train.getId());

        // 検証
        assertThat(trainRepository.findById(train.getId())).isEmpty();
    }

    /**
     * 所有者IDによる電車の検索テスト
     * 特定の所有者IDに紐づく電車が全て取得できることを確認
     */
    @Test
    void findByOwnerId_shouldReturnTrains_whenTrainsExist() {
        // テストデータの準備
        trainRepository.save(createTestTrainEntity("owner-6", TrainType.EXPRESS, null, 500, 8, true, "route-4"));
        trainRepository.save(createTestTrainEntity("owner-6", TrainType.LOCAL, null, 300, 6, false, null));
        trainRepository.save(createTestTrainEntity("owner-7", TrainType.RAPID, null, 400, 8, true, "route-5"));

        // リポジトリメソッドの実行
        List<TrainEntity> trains = trainRepository.findByOwnerId("owner-6");

        // 検証
        assertThat(trains).hasSize(2);
        assertThat(trains).extracting(TrainEntity::getOwnerId).containsOnly("owner-6");
    }

    /**
     * 電車タイプによる電車の検索テスト
     * 特定の電車タイプに紐づく電車が全て取得できることを確認
     */
    @Test
    void findByTrainType_shouldReturnTrains_whenTrainsExist() {
        // テストデータの準備
        trainRepository.save(createTestTrainEntity("owner-8", TrainType.EXPRESS, null, 500, 8, true, "route-6"));
        trainRepository.save(createTestTrainEntity("owner-8", TrainType.LOCAL, null, 300, 6, false, null));
        trainRepository.save(createTestTrainEntity("owner-8", TrainType.EXPRESS, null, 600, 10, false, "route-7"));

        // リポジトリメソッドの実行
        List<TrainEntity> trains = trainRepository.findByTrainType(TrainType.EXPRESS);

        // 検証
        assertThat(trains).hasSize(2);
        assertThat(trains).extracting(TrainEntity::getTrainType).containsOnly(TrainType.EXPRESS);
    }

    /**
     * グループIDによる電車の検索テスト
     * 特定のグループIDに紐づく電車が全て取得できることを確認
     */
    @Test
    void findByGroupId_shouldReturnTrains_whenTrainsExist() {
        // テストデータの準備
        trainRepository.save(createTestTrainEntity("owner-9", TrainType.LOCAL, "group-A", 300, 6, false, null));
        trainRepository.save(createTestTrainEntity("owner-9", TrainType.LOCAL, "group-A", 300, 6, false, null));
        trainRepository.save(createTestTrainEntity("owner-9", TrainType.EXPRESS, "group-B", 500, 8, true, "route-8"));

        // リポジトリメソッドの実行
        List<TrainEntity> trains = trainRepository.findByGroupId("group-A");

        // 検証
        assertThat(trains).hasSize(2);
        assertThat(trains).extracting(TrainEntity::getGroupId).containsOnly("group-A");
    }

    /**
     * プレイヤー制御フラグによる電車の検索テスト
     * プレイヤー制御フラグがtrueの電車が全て取得できることを確認
     */
    @Test
    void findByIsPlayerControlled_shouldReturnTrains_whenPlayerControlledIsTrue() {
        // テストデータの準備
        trainRepository.save(createTestTrainEntity("owner-10", TrainType.EXPRESS, null, 500, 8, true, "route-9"));
        trainRepository.save(createTestTrainEntity("owner-10", TrainType.LOCAL, null, 300, 6, false, null));
        trainRepository.save(createTestTrainEntity("owner-10", TrainType.RAPID, null, 400, 8, true, "route-10"));

        // リポジトリメソッドの実行
        List<TrainEntity> trains = trainRepository.findByIsPlayerControlled(true);

        // 検証
        assertThat(trains).hasSize(2);
        assertThat(trains).extracting(TrainEntity::getIsPlayerControlled).containsOnly(true);
    }

    /**
     * 割り当てられた経路IDによる電車の検索テスト
     * 特定の経路IDに紐づく電車が全て取得できることを確認
     */
    @Test
    void findByAssignedRouteId_shouldReturnTrains_whenTrainsExist() {
        // テストデータの準備
        trainRepository.save(createTestTrainEntity("owner-11", TrainType.EXPRESS, null, 500, 8, true, "route-11"));
        trainRepository.save(createTestTrainEntity("owner-11", TrainType.LOCAL, null, 300, 6, false, null));
        trainRepository.save(createTestTrainEntity("owner-11", TrainType.RAPID, null, 400, 8, true, "route-11"));

        // リポジトリメソッドの実行
        List<TrainEntity> trains = trainRepository.findByAssignedRouteId("route-11");

        // 検証
        assertThat(trains).hasSize(2);
        assertThat(trains).extracting(TrainEntity::getAssignedRouteId).containsOnly("route-11");
    }

    /**
     * 指定された定員以上の電車の検索テスト
     * 指定された定員以上の電車が全て取得できることを確認
     */
    @Test
    void findByTotalCapacityGreaterThanEqual_shouldReturnTrains() {
        // テストデータの準備
        trainRepository.save(createTestTrainEntity("owner-12", TrainType.LOCAL, null, 200, 4, false, null));
        TrainEntity train23 = trainRepository.save(createTestTrainEntity("owner-12", TrainType.EXPRESS, null, 500, 8, true, "route-12"));
        TrainEntity train24 = trainRepository.save(createTestTrainEntity("owner-12", TrainType.RAPID, null, 400, 8, false, null));

        // リポジトリメソッドの実行
        List<TrainEntity> trains = trainRepository.findByTotalCapacityGreaterThanEqual(400);

        // 検証
        assertThat(trains).hasSize(2);
        assertThat(trains).extracting(TrainEntity::getId).containsExactlyInAnyOrder(train23.getId(), train24.getId());
    }

    /**
     * 所有者IDとプレイヤー制御フラグによる電車の検索テスト
     * 特定の所有者IDとプレイヤー制御フラグに紐づく電車が全て取得できることを確認
     */
    @Test
    void findByOwnerIdAndPlayerControlled_shouldReturnTrains() {
        // テストデータの準備
        TrainEntity train25 = trainRepository.save(createTestTrainEntity("owner-13", TrainType.EXPRESS, null, 500, 8, true, "route-13"));
        trainRepository.save(createTestTrainEntity("owner-13", TrainType.LOCAL, null, 300, 6, false, null));
        trainRepository.save(createTestTrainEntity("owner-14", TrainType.RAPID, null, 400, 8, true, "route-14"));

        // リポジトリメソッドの実行
        List<TrainEntity> trains = trainRepository.findByOwnerIdAndPlayerControlled("owner-13", true);

        // 検証
        assertThat(trains).hasSize(1);
        assertThat(trains).extracting(TrainEntity::getId).containsExactly(train25.getId());
    }

    // ヘルパーメソッド：テスト用のTrainEntityを作成
    private TrainEntity createTestTrainEntity(String ownerId, TrainType trainType, String groupId, Integer totalCapacity, Integer doorCount, Boolean isPlayerControlled, String assignedRouteId) {
        TrainEntity entity = new TrainEntity();
        entity.setOwnerId(ownerId);
        entity.setTrainType(trainType);
        entity.setGroupId(groupId);
        entity.setTotalCapacity(totalCapacity);
        entity.setDoorCount(doorCount);
        entity.setIsPlayerControlled(isPlayerControlled);
        entity.setAssignedRouteId(assignedRouteId);
        return entity;
    }

    // ヘルパーメソッド：テスト用のCarEntityを作成
    private CarEntity createTestCarEntity(Integer capacity, Integer doorCount, TrainEntity train) {
        CarEntity car = new CarEntity();
        car.setCapacity(capacity);
        car.setDoorCount(doorCount);
        car.setTrain(train);
        return car;
    }

    // ヘルパーメソッド：テスト用のScheduleEntityを作成
    private ScheduleEntity createTestScheduleEntity(String routeId, TrainEntity train) {
        ScheduleEntity schedule = new ScheduleEntity();
        schedule.setRouteId(routeId);
        schedule.setTrain(train);
        return schedule;
    }
}
