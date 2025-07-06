package net.rushhourgame.core.database.repositories;

import net.rushhourgame.core.database.entities.ScheduleEntity;
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
 * ScheduleRepositoryの統合テストクラス
 * DataJpaTestアノテーションを使用し、JPA関連のコンポーネントのみをロード
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY) // インメモリデータベースを使用
@Transactional // 各テストメソッドの後にトランザクションをロールバックし、データベースをクリーンアップ
class ScheduleRepositoryTest {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private TrainRepository trainRepository; // 関連エンティティの保存用

    /**
     * 各テストの前にデータベースをクリーンアップ
     */
    @BeforeEach
    void setUp() {
        scheduleRepository.deleteAll();
        trainRepository.deleteAll();
    }

    /**
     * スケジュールの保存テスト
     * 新しいスケジュールが正しく保存され、IDが割り当てられることを確認
     */
    @Test
    void saveSchedule_shouldPersistSchedule() {
        // テストデータの準備
        TrainEntity train = createTestTrainEntity("train-1", "owner-1", "EXPRESS", null, 500, 8, true, "route-1");
        trainRepository.save(train);

        ScheduleEntity schedule = createTestScheduleEntity("schedule-1", "route-1", train);

        // リポジトリメソッドの実行
        ScheduleEntity savedSchedule = scheduleRepository.save(schedule);

        // 検証
        assertThat(savedSchedule).isNotNull();
        assertThat(savedSchedule.getId()).isEqualTo("schedule-1");
        assertThat(savedSchedule.getTrain().getId()).isEqualTo("train-1");
    }

    /**
     * IDによるスケジュールの取得テスト
     * 存在するIDでスケジュールが取得できることを確認
     */
    @Test
    void findById_shouldReturnSchedule_whenScheduleExists() {
        // テストデータの準備
        TrainEntity train = createTestTrainEntity("train-2", "owner-2", "LOCAL", null, 300, 6, false, null);
        trainRepository.save(train);
        ScheduleEntity schedule = createTestScheduleEntity("schedule-2", "route-2", train);
        scheduleRepository.save(schedule);

        // リポジトリメソッドの実行
        Optional<ScheduleEntity> foundSchedule = scheduleRepository.findById("schedule-2");

        // 検証
        assertThat(foundSchedule).isPresent();
        assertThat(foundSchedule.get().getRouteId()).isEqualTo("route-2");
    }

    /**
     * IDによるスケジュールの取得テスト
     * 存在しないIDでスケジュールが取得できないことを確認
     */
    @Test
    void findById_shouldReturnEmpty_whenScheduleDoesNotExist() {
        // リポジトリメソッドの実行
        Optional<ScheduleEntity> foundSchedule = scheduleRepository.findById("non-existent-schedule");

        // 検証
        assertThat(foundSchedule).isEmpty();
    }

    /**
     * 全てのスケジュールの取得テスト
     * データベースに存在する全てのスケジュールが取得できることを確認
     */
    @Test
    void findAll_shouldReturnAllSchedules() {
        // テストデータの準備
        TrainEntity train1 = createTestTrainEntity("train-3", "owner-3", "RAPID", null, 400, 8, true, "route-3");
        TrainEntity train2 = createTestTrainEntity("train-4", "owner-3", "EXPRESS", null, 600, 10, false, "route-4");
        trainRepository.saveAll(Arrays.asList(train1, train2));

        scheduleRepository.save(createTestScheduleEntity("schedule-3", "route-3", train1));
        scheduleRepository.save(createTestScheduleEntity("schedule-4", "route-4", train2));

        // リポジトリメソッドの実行
        List<ScheduleEntity> schedules = scheduleRepository.findAll();

        // 検証
        assertThat(schedules).hasSize(2);
    }

    /**
     * スケジュールの更新テスト
     * 既存のスケジュール情報が正しく更新されることを確認
     */
    @Test
    void updateSchedule_shouldUpdateExistingSchedule() {
        // テストデータの準備
        TrainEntity train = createTestTrainEntity("train-5", "owner-4", "LOCAL", null, 200, 4, false, null);
        trainRepository.save(train);
        ScheduleEntity originalSchedule = createTestScheduleEntity("schedule-5", "route-5", train);
        scheduleRepository.save(originalSchedule);

        // 更新データの準備
        originalSchedule.setRouteId("route-5-updated");

        // リポジトリメソッドの実行
        ScheduleEntity updatedSchedule = scheduleRepository.save(originalSchedule);

        // 検証
        assertThat(updatedSchedule.getRouteId()).isEqualTo("route-5-updated");

        // データベースから直接取得して検証
        Optional<ScheduleEntity> foundSchedule = scheduleRepository.findById("schedule-5");
        assertThat(foundSchedule).isPresent();
        assertThat(foundSchedule.get().getRouteId()).isEqualTo("route-5-updated");
    }

    /**
     * スケジュールの削除テスト
     * 存在するIDのスケジュールが正しく削除されることを確認
     */
    @Test
    void deleteById_shouldDeleteSchedule_whenScheduleExists() {
        // テストデータの準備
        TrainEntity train = createTestTrainEntity("train-6", "owner-5", "LOCAL", null, 150, 4, true, null);
        trainRepository.save(train);
        ScheduleEntity schedule = createTestScheduleEntity("schedule-6", "route-6", train);
        scheduleRepository.save(schedule);

        // リポジトリメソッドの実行
        scheduleRepository.deleteById("schedule-6");

        // 検証
        assertThat(scheduleRepository.findById("schedule-6")).isEmpty();
    }

    /**
     * 経路IDによるスケジュールの検索テスト
     * 特定の経路IDに紐づくスケジュールが全て取得できることを確認
     */
    @Test
    void findByRouteId_shouldReturnSchedules_whenSchedulesExist() {
        // テストデータの準備
        TrainEntity train1 = createTestTrainEntity("train-7", "owner-6", "EXPRESS", null, 500, 8, true, "route-7");
        TrainEntity train2 = createTestTrainEntity("train-8", "owner-6", "LOCAL", null, 300, 6, false, null);
        TrainEntity train3 = createTestTrainEntity("train-9", "owner-6", "RAPID", null, 400, 8, true, "route-7");
        trainRepository.saveAll(Arrays.asList(train1, train2, train3));

        scheduleRepository.save(createTestScheduleEntity("schedule-7", "route-7", train1));
        scheduleRepository.save(createTestScheduleEntity("schedule-8", "route-8", train2));
        scheduleRepository.save(createTestScheduleEntity("schedule-9", "route-7", train3));

        // リポジトリメソッドの実行
        List<ScheduleEntity> schedules = scheduleRepository.findByRouteId("route-7");

        // 検証
        assertThat(schedules).hasSize(2);
        assertThat(schedules).extracting(ScheduleEntity::getId).containsExactlyInAnyOrder("schedule-7", "schedule-9");
    }

    /**
     * 電車IDによるスケジュールの検索テスト
     * 特定の電車IDに紐づくスケジュールが取得できることを確認
     */
    @Test
    void findByTrain_Id_shouldReturnSchedule_whenScheduleExists() {
        // テストデータの準備
        TrainEntity train = createTestTrainEntity("train-10", "owner-7", "EXPRESS", null, 500, 8, true, "route-10");
        trainRepository.save(train);

        ScheduleEntity schedule = createTestScheduleEntity("schedule-10", "route-10", train);
        scheduleRepository.save(schedule);

        // リポジトリメソッドの実行
        Optional<ScheduleEntity> foundSchedule = scheduleRepository.findByTrain_Id("train-10");

        // 検証
        assertThat(foundSchedule).isPresent();
        assertThat(foundSchedule.get().getId()).isEqualTo("schedule-10");
    }

    /**
     * 電車IDによるスケジュールの検索テスト
     * 存在しない電車IDでスケジュールが取得できないことを確認
     */
    @Test
    void findByTrain_Id_shouldReturnEmpty_whenScheduleDoesNotExist() {
        // リポジトリメソッドの実行
        Optional<ScheduleEntity> foundSchedule = scheduleRepository.findByTrain_Id("non-existent-train");

        // 検証
        assertThat(foundSchedule).isEmpty();
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
}
