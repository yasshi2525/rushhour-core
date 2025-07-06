package net.rushhourgame.core.database.repositories;

import net.rushhourgame.core.database.entities.CarEntity;
import net.rushhourgame.core.database.entities.TrainEntity;
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
 * CarRepositoryの統合テストクラス
 * DataJpaTestアノテーションを使用し、JPA関連のコンポーネントのみをロード
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY) // インメモリデータベースを使用
@Transactional // 各テストメソッドの後にトランザクションをロールバックし、データベースをクリーンアップ
class CarRepositoryTest {

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private TrainRepository trainRepository; // 関連エンティティの保存用

    /**
     * 各テストの前にデータベースをクリーンアップ
     */
    @BeforeEach
    void setUp() {
        carRepository.deleteAll();
        trainRepository.deleteAll();
    }

    /**
     * 車両の保存テスト
     * 新しい車両が正しく保存され、IDが割り当てられることを確認
     */
    @Test
    void saveCar_shouldPersistCar() {
        // テストデータの準備
        TrainEntity train = createTestTrainEntity("train-1", "owner-1", "EXPRESS", null, 500, 8, true, "route-1");
        trainRepository.save(train);

        CarEntity car = createTestCarEntity("car-1", 100, 4, train);

        // リポジトリメソッドの実行
        CarEntity savedCar = carRepository.save(car);

        // 検証
        assertThat(savedCar).isNotNull();
        assertThat(savedCar.getId()).isEqualTo("car-1");
        assertThat(savedCar.getTrain().getId()).isEqualTo("train-1");
    }

    /**
     * IDによる車両の取得テスト
     * 存在するIDで車両が取得できることを確認
     */
    @Test
    void findById_shouldReturnCar_whenCarExists() {
        // テストデータの準備
        TrainEntity train = createTestTrainEntity("train-2", "owner-2", "LOCAL", null, 300, 6, false, null);
        trainRepository.save(train);
        CarEntity car = createTestCarEntity("car-2", 100, 4, train);
        carRepository.save(car);

        // リポジトリメソッドの実行
        Optional<CarEntity> foundCar = carRepository.findById("car-2");

        // 検証
        assertThat(foundCar).isPresent();
        assertThat(foundCar.get().getCapacity()).isEqualTo(100);
    }

    /**
     * IDによる車両の取得テスト
     * 存在しないIDで車両が取得できないことを確認
     */
    @Test
    void findById_shouldReturnEmpty_whenCarDoesNotExist() {
        // リポジトリメソッドの実行
        Optional<CarEntity> foundCar = carRepository.findById("non-existent-car");

        // 検証
        assertThat(foundCar).isEmpty();
    }

    /**
     * 全ての車両の取得テスト
     * データベースに存在する全ての車両が取得できることを確認
     */
    @Test
    void findAll_shouldReturnAllCars() {
        // テストデータの準備
        TrainEntity train1 = createTestTrainEntity("train-3", "owner-3", "RAPID", null, 400, 8, true, "route-2");
        TrainEntity train2 = createTestTrainEntity("train-4", "owner-3", "EXPRESS", null, 600, 10, false, "route-3");
        trainRepository.saveAll(Arrays.asList(train1, train2));

        carRepository.save(createTestCarEntity("car-3", 100, 4, train1));
        carRepository.save(createTestCarEntity("car-4", 120, 6, train2));

        // リポジトリメソッドの実行
        List<CarEntity> cars = carRepository.findAll();

        // 検証
        assertThat(cars).hasSize(2);
    }

    /**
     * 車両の更新テスト
     * 既存の車両情報が正しく更新されることを確認
     */
    @Test
    void updateCar_shouldUpdateExistingCar() {
        // テストデータの準備
        TrainEntity train = createTestTrainEntity("train-5", "owner-4", "LOCAL", null, 200, 4, false, null);
        trainRepository.save(train);
        CarEntity originalCar = createTestCarEntity("car-5", 100, 4, train);
        carRepository.save(originalCar);

        // 更新データの準備
        originalCar.setCapacity(110);
        originalCar.setDoorCount(5);

        // リポジトリメソッドの実行
        CarEntity updatedCar = carRepository.save(originalCar);

        // 検証
        assertThat(updatedCar.getCapacity()).isEqualTo(110);
        assertThat(updatedCar.getDoorCount()).isEqualTo(5);

        // データベースから直接取得して検証
        Optional<CarEntity> foundCar = carRepository.findById("car-5");
        assertThat(foundCar).isPresent();
        assertThat(foundCar.get().getCapacity()).isEqualTo(110);
    }

    /**
     * 車両の削除テスト
     * 存在するIDの車両が正しく削除されることを確認
     */
    @Test
    void deleteById_shouldDeleteCar_whenCarExists() {
        // テストデータの準備
        TrainEntity train = createTestTrainEntity("train-6", "owner-5", "LOCAL", null, 150, 4, true, null);
        trainRepository.save(train);
        CarEntity car = createTestCarEntity("car-6", 100, 4, train);
        carRepository.save(car);

        // リポジトリメソッドの実行
        carRepository.deleteById("car-6");

        // 検証
        assertThat(carRepository.findById("car-6")).isEmpty();
    }

    /**
     * 電車IDによる車両の検索テスト
     * 特定の電車IDに紐づく車両が全て取得できることを確認
     */
    @Test
    void findByTrain_Id_shouldReturnCars_whenCarsExist() {
        // テストデータの準備
        TrainEntity trainA = createTestTrainEntity("train-A", "owner-6", "EXPRESS", null, 500, 8, true, "route-4");
        TrainEntity trainB = createTestTrainEntity("train-B", "owner-6", "LOCAL", null, 300, 6, false, null);
        trainRepository.saveAll(Arrays.asList(trainA, trainB));

        carRepository.save(createTestCarEntity("car-7", 100, 4, trainA));
        carRepository.save(createTestCarEntity("car-8", 120, 6, trainB));
        carRepository.save(createTestCarEntity("car-9", 110, 5, trainA));

        // リポジトリメソッドの実行
        List<CarEntity> cars = carRepository.findByTrain_Id("train-A");

        // 検証
        assertThat(cars).hasSize(2);
        assertThat(cars).extracting(CarEntity::getId).containsExactlyInAnyOrder("car-7", "car-9");
    }

    /**
     * 指定されたキャパシティ以上の車両の検索テスト
     * 指定されたキャパシティ以上の車両が全て取得できることを確認
     */
    @Test
    void findByCapacityGreaterThanEqual_shouldReturnCars() {
        // テストデータの準備
        TrainEntity train = createTestTrainEntity("train-7", "owner-7", "EXPRESS", null, 500, 8, true, "route-5");
        trainRepository.save(train);

        carRepository.save(createTestCarEntity("car-10", 90, 4, train));
        carRepository.save(createTestCarEntity("car-11", 100, 4, train));
        carRepository.save(createTestCarEntity("car-12", 110, 4, train));

        // リポジトリメソッドの実行
        List<CarEntity> cars = carRepository.findByCapacityGreaterThanEqual(100);

        // 検証
        assertThat(cars).hasSize(2);
        assertThat(cars).extracting(CarEntity::getId).containsExactlyInAnyOrder("car-11", "car-12");
    }

    /**
     * ドア数による車両の検索テスト
     * 特定のドア数に紐づく車両が全て取得できることを確認
     */
    @Test
    void findByDoorCount_shouldReturnCars() {
        // テストデータの準備
        TrainEntity train = createTestTrainEntity("train-8", "owner-8", "LOCAL", null, 300, 6, false, null);
        trainRepository.save(train);

        carRepository.save(createTestCarEntity("car-13", 100, 4, train));
        carRepository.save(createTestCarEntity("car-14", 100, 6, train));
        carRepository.save(createTestCarEntity("car-15", 100, 4, train));

        // リポジトリメソッドの実行
        List<CarEntity> cars = carRepository.findByDoorCount(4);

        // 検証
        assertThat(cars).hasSize(2);
        assertThat(cars).extracting(CarEntity::getId).containsExactlyInAnyOrder("car-13", "car-15");
    }

    // ヘルパーメソッド：テスト用のTrainEntityを作成
    private TrainEntity createTestTrainEntity(String id, String ownerId, String trainType, String groupId, Integer totalCapacity, Integer doorCount, Boolean isPlayerControlled, String assignedRouteId) {
        TrainEntity entity = new TrainEntity();
        entity.setId(id);
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
    private CarEntity createTestCarEntity(String id, Integer capacity, Integer doorCount, TrainEntity train) {
        CarEntity car = new CarEntity();
        car.setId(id);
        car.setCapacity(capacity);
        car.setDoorCount(doorCount);
        car.setTrain(train);
        return car;
    }
}
