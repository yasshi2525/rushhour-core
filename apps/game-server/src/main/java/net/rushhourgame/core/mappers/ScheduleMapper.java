package net.rushhourgame.core.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import net.rushhourgame.core.database.entities.ScheduleEntity;
import net.rushhourgame.models.timetable.Schedule;

/**
 * スケジュールドメインモデルと永続化モデル間のマッピング
 */
@Mapper(componentModel = "spring", uses = {StopTimeMapper.class})
public interface ScheduleMapper {
    
    /**
     * 永続化モデルからドメインモデルへの変換
     */
    @Mapping(target = "stopTimes", source = "stopTimes")
    @Mapping(target = "trainId", source = "train.id")
    Schedule toDomain(ScheduleEntity entity);
    
    /**
     * ドメインモデルから永続化モデルへの変換
     */
    @Mapping(target = "train", ignore = true)
    @Mapping(target = "stopTimes", source = "stopTimes")
    ScheduleEntity toEntity(Schedule domain);
    
    /**
     * リストの変換
     */
    List<Schedule> toDomainList(List<ScheduleEntity> entities);
    List<ScheduleEntity> toEntityList(List<Schedule> domains);
}
