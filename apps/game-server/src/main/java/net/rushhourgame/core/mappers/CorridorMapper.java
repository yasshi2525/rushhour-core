package net.rushhourgame.core.mappers;

import net.rushhourgame.core.database.entities.CorridorEntity;
import net.rushhourgame.models.railway.Corridor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * 通路ドメインモデルと永続化モデル間のマッピング
 */
@Mapper(componentModel = "spring")
public interface CorridorMapper {
    
    /**
     * 永続化モデルからドメインモデルへの変換
     */
    @Mapping(target = "stationId", source = "stationId")
    Corridor toDomain(CorridorEntity entity);
    
    /**
     * ドメインモデルから永続化モデルへの変換
     */
    @Mapping(target = "station", ignore = true)
    @Mapping(target = "stationId", source = "stationId")
    CorridorEntity toEntity(Corridor domain);
    
    /**
     * リストの変換
     */
    List<Corridor> toDomainList(List<CorridorEntity> entities);
    List<CorridorEntity> toEntityList(List<Corridor> domains);
}