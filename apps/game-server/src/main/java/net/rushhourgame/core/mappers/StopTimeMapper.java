package net.rushhourgame.core.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import net.rushhourgame.core.database.entities.StopTimeEntity;
import net.rushhourgame.models.timetable.StopTime;

/**
 * 停車時間ドメインモデルと永続化モデル間のマッピング
 */
@Mapper(componentModel = "spring")
public interface StopTimeMapper {
    
    /**
     * 永続化モデルからドメインモデルへの変換
     */
    @Mapping(target = "scheduleId", source = "schedule.id")
    StopTime toDomain(StopTimeEntity entity);
    
    /**
     * ドメインモデルから永続化モデルへの変換
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "schedule", ignore = true)
    @Mapping(target = "sequenceOrder", ignore = true)
    StopTimeEntity toEntity(StopTime domain);
    
    /**
     * リストの変換
     */
    List<StopTime> toDomainList(List<StopTimeEntity> entities);
    List<StopTimeEntity> toEntityList(List<StopTime> domains);
}
