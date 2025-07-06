package net.rushhourgame.core.database.repositories;

import net.rushhourgame.core.database.entities.ScheduleEntity;
import net.rushhourgame.core.database.entities.StopTimeEntity;
import net.rushhourgame.core.database.entities.TrainEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * StopTimeRepositoryの統合テストクラス
 * DataJpaTestアノテーションを使用し、JPA関連のコンポーネントのみをロード
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY) // インメモリデータベースを使用
@Transactional // 各テストメソッドの後にトランザクションをロールバックし、データベースをクリーンアップ
class StopTimeRepositoryTest {

    @Autowired
    private StopTimeRepository stopTimeRepository;

    @Autowired
    private ScheduleRepository scheduleRepository; // 関連エンティティの保存用

    @Autowired
    private TrainRepository trainRepository; // 関連エンティティの保存用

    /**
     * 各テストの前にデータベースをクリーンアップ
     */
    @BeforeEach
    void setUp() {
        stopTimeRepository.deleteAll();
        scheduleRepository.deleteAll();
        trainRepository.deleteAll();
    }

    /**
     * 停車時間の保存テスト
     * 新しい停車時間が正しく保存され、IDが割り当てられることを確認
     */
    @Test
    void saveStopTime_shouldPersistStopTime() {
        // テストデータの準備
        TrainEntity train = createTestTrainEntity("train-1", "owner-1", "EXPRESS", null, 500, 8, true, "route-1");
        trainRepository.save(train);
        ScheduleEntity schedule = createTestScheduleEntity("schedule-1", "route-1", train);
        scheduleRepository.save(schedule);

        StopTimeEntity stopTime = createTestStopTimeEntity("station-1", LocalTime.of(9, 0), LocalTime.of(9, 5), 1, schedule);

        // リポジトリメソッドの実行
        StopTimeEntity savedStopTime = stopTimeRepository.save(stopTime);

        // 検証
        assertThat(savedStopTime).isNotNull();
        assertThat(savedStopTime.getId()).isNotNull(); // IDは自動生成される
        assertThat(savedStopTime.getStationId()).isEqualTo("station-1");
        assertThat(savedStopTime.getSchedule().getId()).isEqualTo("schedule-1");
    }

    /**
     * IDによる停車時間の取得テスト
     * 存在するIDで停車時間が取得できることを確認
     */
    @Test
    void findById_shouldReturnStopTime_whenStopTimeExists() {
        // テストデータの準備
        TrainEntity train = createTestTrainEntity("train-2", "owner-2", "LOCAL", null, 300, 6, false, null);
        trainRepository.save(train);
        ScheduleEntity schedule = createTestScheduleEntity("schedule-2", "route-2", train);
        scheduleRepository.save(schedule);
        StopTimeEntity stopTime = createTestStopTimeEntity("station-2", LocalTime.of(10, 0), LocalTime.of(10, 2), 1, schedule);
        stopTimeRepository.save(stopTime);

        // リポジトリメソッドの実行
        Optional<StopTimeEntity> foundStopTime = stopTimeRepository.findById(stopTime.getId());

        // 検証
        assertThat(foundStopTime).isPresent();
        assertThat(foundStopTime.get().getStationId()).isEqualTo("station-2");
    }

    /**
     * IDによる停車時間の取得テスト
     * 存在しないIDで停車時間が取得できないことを確認
     */
    @Test
    void findById_shouldReturnEmpty_whenStopTimeDoesNotExist() {
        // リポジトリメソッドの実行
        Optional<StopTimeEntity> foundStopTime = stopTimeRepository.findById(999L);

        // 検証
        assertThat(foundStopTime).isEmpty();
    }

    /**
     * 全ての停車時間の取得テスト
     * データベースに存在する全ての停車時間が取得できることを確認
     */
    @Test
    void findAll_shouldReturnAllStopTimes() {
        // テストデータの準備
        TrainEntity train1 = createTestTrainEntity("train-3", "owner-3", "RAPID", null, 400, 8, true, "route-3");
        TrainEntity train2 = createTestTrainEntity("train-4", "owner-3", "EXPRESS", null, 600, 10, false, "route-4");
        trainRepository.saveAll(Arrays.asList(train1, train2));
        ScheduleEntity schedule1 = createTestScheduleEntity("schedule-3", "route-3", train1);
        ScheduleEntity schedule2 = createTestScheduleEntity("schedule-4", "route-4", train2);
        scheduleRepository.saveAll(Arrays.asList(schedule1, schedule2));

        stopTimeRepository.save(createTestStopTimeEntity("station-3", LocalTime.of(11, 0), LocalTime.of(11, 1), 1, schedule1));
        stopTimeRepository.save(createTestStopTimeEntity("station-4", LocalTime.of(12, 0), LocalTime.of(12, 2), 1, schedule2));

        // リポジトリメソッドの実行
        List<StopTimeEntity> stopTimes = stopTimeRepository.findAll();

        // 検証
        assertThat(stopTimes).hasSize(2);
    }

    /**
     * 停車時間の更新テスト
     * 既存の停車時間情報が正しく更新されることを確認
     */
    @Test
    void updateStopTime_shouldUpdateExistingStopTime() {
        // テストデータの準備
        TrainEntity train = createTestTrainEntity("train-5", "owner-4", "LOCAL", null, 200, 4, false, null);
        trainRepository.save(train);
        ScheduleEntity schedule = createTestScheduleEntity("schedule-5", "route-5", train);
        scheduleRepository.save(schedule);
        StopTimeEntity originalStopTime = createTestStopTimeEntity("station-5", LocalTime.of(13, 0), LocalTime.of(13, 1), 1, schedule);
        stopTimeRepository.save(originalStopTime);

        // 更新データの準備
        originalStopTime.setDepartureTime(LocalTime.of(13, 3));
        originalStopTime.setSequenceOrder(2);

        // リポジトリメソッドの実行
        StopTimeEntity updatedStopTime = stopTimeRepository.save(originalStopTime);

        // 検証
        assertThat(updatedStopTime.getDepartureTime()).isEqualTo(LocalTime.of(13, 3));
        assertThat(updatedStopTime.getSequenceOrder()).isEqualTo(2);

        // データベースから直接取得して検証
        Optional<StopTimeEntity> foundStopTime = stopTimeRepository.findById(originalStopTime.getId());
        assertThat(foundStopTime).isPresent();
        assertThat(foundStopTime.get().getDepartureTime()).isEqualTo(LocalTime.of(13, 3));
    }

    /**
     * 停車時間の削除テスト
     * 存在するIDの停車時間が正しく削除されることを確認
     */
    @Test
    void deleteById_shouldDeleteStopTime_whenStopTimeExists() {
        // テストデータの準備
        TrainEntity train = createTestTrainEntity("train-6", "owner-5", "LOCAL", null, 150, 4, true, null);
        trainRepository.save(train);
        ScheduleEntity schedule = createTestScheduleEntity("schedule-6", "route-6", train);
        scheduleRepository.save(schedule);
        StopTimeEntity stopTime = createTestStopTimeEntity("station-6", LocalTime.of(14, 0), LocalTime.of(14, 1), 1, schedule);
        stopTimeRepository.save(stopTime);

        // リポジトリメソッドの実行
        stopTimeRepository.deleteById(stopTime.getId());

        // 検証
        assertThat(stopTimeRepository.findById(stopTime.getId())).isEmpty();
    }

    /**
     * スケジュールIDによる停車時間の検索テスト
     * 特定のスケジュールIDに紐づく停車時間が全て取得できることを確認
     */
    @Test
    void findBySchedule_Id_shouldReturnStopTimes_whenStopTimesExist() {
        // テストデータの準備
        TrainEntity train1 = createTestTrainEntity("train-7", "owner-6", "EXPRESS", null, 500, 8, true, "route-7");
        TrainEntity train2 = createTestTrainEntity("train-8", "owner-6", "LOCAL", null, 300, 6, false, null);
        trainRepository.saveAll(Arrays.asList(train1, train2));
        ScheduleEntity scheduleA = createTestScheduleEntity("schedule-A", "route-A", train1);
        ScheduleEntity scheduleB = createTestScheduleEntity("schedule-B", "route-B", train2);
        scheduleRepository.saveAll(Arrays.asList(scheduleA, scheduleB));

        stopTimeRepository.save(createTestStopTimeEntity("station-7", LocalTime.of(15, 0), LocalTime.of(15, 1), 1, scheduleA));
        stopTimeRepository.save(createTestStopTimeEntity("station-8", LocalTime.of(16, 0), LocalTime.of(16, 1), 1, scheduleB));
        stopTimeRepository.save(createTestStopTimeEntity("station-9", LocalTime.of(15, 5), LocalTime.of(15, 6), 2, scheduleA));

        // リポジトリメソッドの実行
        List<StopTimeEntity> stopTimes = stopTimeRepository.findBySchedule_Id("schedule-A");

        // 検証
        assertThat(stopTimes).hasSize(2);
        assertThat(stopTimes).extracting(StopTimeEntity::getStationId).containsExactlyInAnyOrder("station-7", "station-9");
    }

    /**
     * 駅IDによる停車時間の検索テスト
     * 特定の駅IDに紐づく停車時間が全て取得できることを確認
     */
    @Test
    void findByStationId_shouldReturnStopTimes_whenStopTimesExist() {
        // テストデータの準備
        TrainEntity train = createTestTrainEntity("train-9", "owner-7", "EXPRESS", null, 500, 8, true, "route-9");
        trainRepository.save(train);
        ScheduleEntity schedule = createTestScheduleEntity("schedule-9", "route-9", train);
        scheduleRepository.save(schedule);

        stopTimeRepository.save(createTestStopTimeEntity("station-X", LocalTime.of(17, 0), LocalTime.of(17, 1), 1, schedule));
        stopTimeRepository.save(createTestStopTimeEntity("station-Y", LocalTime.of(18, 0), LocalTime.of(18, 1), 2, schedule));
        stopTimeRepository.save(createTestStopTimeEntity("station-X", LocalTime.of(19, 0), LocalTime.of(19, 1), 3, schedule));

        // リポジトリメソッドの実行
        List<StopTimeEntity> stopTimes = stopTimeRepository.findByStationId("station-X");

        // 検証
        assertThat(stopTimes).hasSize(2);
        assertThat(stopTimes).extracting(StopTimeEntity::getStationId).containsOnly("station-X");
    }

    /**
     * 指定された到着時間以降の停車時間の検索テスト
     * 指定された到着時間以降の停車時間が全て取得できることを確認
     */
    @Test
    void findByArrivalTimeAfter_shouldReturnStopTimes() {
        // テストデータの準備
        TrainEntity train = createTestTrainEntity("train-10", "owner-8", "LOCAL", null, 300, 6, false, null);
        trainRepository.save(train);
        ScheduleEntity schedule = createTestScheduleEntity("schedule-10", "route-10", train);
        scheduleRepository.save(schedule);

        stopTimeRepository.save(createTestStopTimeEntity("station-A", LocalTime.of(8, 0), LocalTime.of(8, 1), 1, schedule));
        stopTimeRepository.save(createTestStopTimeEntity("station-B", LocalTime.of(9, 0), LocalTime.of(9, 1), 2, schedule));
        stopTimeRepository.save(createTestStopTimeEntity("station-C", LocalTime.of(10, 0), LocalTime.of(10, 1), 3, schedule));

        // リポジトリメソッドの実行
        List<StopTimeEntity> stopTimes = stopTimeRepository.findByArrivalTimeAfter(LocalTime.of(8, 30));

        // 検証
        assertThat(stopTimes).hasSize(2);
        assertThat(stopTimes).extracting(StopTimeEntity::getStationId).containsExactlyInAnyOrder("station-B", "station-C");
    }

    /**
     * 指定された出発時間以前の停車時間の検索テスト
     * 指定された出発時間以前の停車時間が全て取得できることを確認
     */
    @Test
    void findByDepartureTimeBefore_shouldReturnStopTimes() {
        // テストデータの準備
        TrainEntity train = createTestTrainEntity("train-11", "owner-9", "LOCAL", null, 300, 6, false, null);
        trainRepository.save(train);
        ScheduleEntity schedule = createTestScheduleEntity("schedule-11", "route-11", train);
        scheduleRepository.save(schedule);

        stopTimeRepository.save(createTestStopTimeEntity("station-D", LocalTime.of(8, 0), LocalTime.of(8, 1), 1, schedule));
        stopTimeRepository.save(createTestStopTimeEntity("station-E", LocalTime.of(9, 0), LocalTime.of(9, 1), 2, schedule));
        stopTimeRepository.save(createTestStopTimeEntity("station-F", LocalTime.of(10, 0), LocalTime.of(10, 1), 3, schedule));

        // リポジトリメソッドの実行
        List<StopTimeEntity> stopTimes = stopTimeRepository.findByDepartureTimeBefore(LocalTime.of(9, 30));

        // 検証
        assertThat(stopTimes).hasSize(2);
        assertThat(stopTimes).extracting(StopTimeEntity::getStationId).containsExactlyInAnyOrder("station-D", "station-E");
    }

    /**
     * スケジュールIDと駅IDによる停車時間の検索テスト
     * 特定のスケジュールIDと駅IDに紐づく停車時間が全て取得できることを確認
     */
    @Test
    void findBySchedule_IdAndStationId_shouldReturnStopTimes() {
        // テストデータの準備
        TrainEntity train1 = createTestTrainEntity("train-12", "owner-10", "EXPRESS", null, 500, 8, true, "route-12");
        TrainEntity train2 = createTestTrainEntity("train-13", "owner-10", "EXPRESS", null, 500, 8, true, "route-13");
        trainRepository.save(train1);
        trainRepository.save(train2);
        
        ScheduleEntity schedule1 = createTestScheduleEntity("schedule-X", "route-X", train1);
        ScheduleEntity schedule2 = createTestScheduleEntity("schedule-Y", "route-Y", train2);
        scheduleRepository.saveAll(Arrays.asList(schedule1, schedule2));

        stopTimeRepository.save(createTestStopTimeEntity("station-P", LocalTime.of(20, 0), LocalTime.of(20, 1), 1, schedule1));
        stopTimeRepository.save(createTestStopTimeEntity("station-Q", LocalTime.of(21, 0), LocalTime.of(21, 1), 2, schedule1));
        stopTimeRepository.save(createTestStopTimeEntity("station-P", LocalTime.of(22, 0), LocalTime.of(22, 1), 3, schedule2));

        // リポジトリメソッドの実行
        List<StopTimeEntity> stopTimes = stopTimeRepository.findBySchedule_IdAndStationId("schedule-X", "station-P");

        // 検証
        assertThat(stopTimes).hasSize(1);
        assertThat(stopTimes).extracting(StopTimeEntity::getStationId).containsOnly("station-P");
        assertThat(stopTimes).extracting(st -> st.getSchedule().getId()).containsOnly("schedule-X");
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

    // ヘルパーメソッド：テスト用のScheduleEntityを作成
    private ScheduleEntity createTestScheduleEntity(String id, String routeId, TrainEntity train) {
        ScheduleEntity schedule = new ScheduleEntity();
        schedule.setId(id);
        schedule.setRouteId(routeId);
        schedule.setTrain(train);
        return schedule;
    }

    // ヘルパーメソッド：テスト用のStopTimeEntityを作成
    private StopTimeEntity createTestStopTimeEntity(String stationId, LocalTime arrivalTime, LocalTime departureTime, Integer sequenceOrder, ScheduleEntity schedule) {
        StopTimeEntity stopTime = new StopTimeEntity();
        stopTime.setStationId(stationId);
        stopTime.setArrivalTime(arrivalTime);
        stopTime.setDepartureTime(departureTime);
        stopTime.setSequenceOrder(sequenceOrder);
        stopTime.setSchedule(schedule);
        return stopTime;
    }
}
