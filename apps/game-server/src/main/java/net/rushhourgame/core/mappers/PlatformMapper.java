package net.rushhourgame.core.mappers;

import net.rushhourgame.core.database.entities.PlatformEntity;
import net.rushhourgame.models.railway.Platform;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * プラットフォームドメインモデルと永続化モデル間のマッピング
 */
@Mapper(componentModel = "spring")
public interface PlatformMapper {
    
    /**
     * 永続化モデルからドメインモデルへの変換
     */
    @Mapping(target = "stationId", source = "stationId")
    Platform toDomain(PlatformEntity entity);
    
    /**
     * ドメインモデルから永続化モデルへの変換
     */
    @Mapping(target = "station", ignore = true)
    @Mapping(target = "stationId", source = "stationId")
    PlatformEntity toEntity(Platform domain);
    
    /**
     * リストの変換
     */
    List<Platform> toDomainList(List<PlatformEntity> entities);
    List<PlatformEntity> toEntityList(List<Platform> domains);
}