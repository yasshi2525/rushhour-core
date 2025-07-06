package net.rushhourgame.core.mappers;

import net.rushhourgame.core.database.entities.TrainEntity;
import net.rushhourgame.models.train.Train;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

/**
 * 電車ドメインモデルと永続化モデル間のマッピング
 */
@Mapper(componentModel = "spring", uses = {CarMapper.class, ScheduleMapper.class})
public interface TrainMapper {
    
    /**
     * 永続化モデルからドメインモデルへの変換
     */
    @Mapping(target = "cars", source = "cars")
    @Mapping(target = "schedule", source = "schedule")
    @Mapping(target = "assignedRoute", ignore = true) // Route オブジェクトは別途設定
    @Mapping(target = "playerControlled", source = "isPlayerControlled")
    Train toDomain(TrainEntity entity);
    
    /**
     * ドメインモデルから永続化モデルへの変換
     */
    @Mapping(target = "cars", source = "cars")
    @Mapping(target = "schedule", source = "schedule")
    @Mapping(target = "assignedRouteId", source = "assignedRoute.id")
    @Mapping(target = "isPlayerControlled", source = "playerControlled")
    TrainEntity toEntity(Train domain);
    
    /**
     * 永続化モデルのリストからドメインモデルのリストへの変換
     */
    List<Train> toDomainList(List<TrainEntity> entities);
    
    /**
     * ドメインモデルのリストから永続化モデルのリストへの変換
     */
    List<TrainEntity> toEntityList(List<Train> domains);
    
    /**
     * 既存の永続化モデルをドメインモデルで更新
     */
    @Mapping(target = "cars", ignore = true)
    @Mapping(target = "schedule", ignore = true)
    @Mapping(target = "assignedRouteId", source = "assignedRoute.id")
    @Mapping(target = "isPlayerControlled", source = "playerControlled")
    void updateEntityFromDomain(Train domain, @MappingTarget TrainEntity entity);
}