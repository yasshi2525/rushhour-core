package net.rushhourgame.core.mappers;

import net.rushhourgame.core.database.entities.StationEntity;
import net.rushhourgame.core.database.entities.LocationEmbeddable;
import net.rushhourgame.models.common.Location;
import net.rushhourgame.models.station.Station;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.AfterMapping;

import java.util.List;

/**
 * 駅ドメインモデルと永続化モデル間のマッピング
 */
@Mapper(componentModel = "spring", uses = {PlatformMapper.class, GateMapper.class, CorridorMapper.class})
public interface StationMapper {
    
    /**
     * 永続化モデルからドメインモデルへの変換
     */
    @Mapping(target = "location", source = "location")
    @Mapping(target = "platforms", source = "platforms")
    @Mapping(target = "gates", source = "gates")
    @Mapping(target = "corridors", source = "corridors")
    Station toDomain(StationEntity entity);
    
    /**
     * ドメインモデルから永続化モデルへの変換
     */
    @Mapping(target = "location", source = "location")
    @Mapping(target = "platforms", source = "platforms")
    @Mapping(target = "gates", source = "gates")
    @Mapping(target = "corridors", source = "corridors")
    StationEntity toEntity(Station domain);

    /**
     * ドメインモデルから永続化モデルへの変換後処理
     * 子エンティティのstationフィールドに親エンティティを設定
     */
    @AfterMapping
    default void linkStationToChildren(Station domain, @MappingTarget StationEntity entity) {
        if (domain.getPlatforms() != null && entity.getPlatforms() != null) {
            for (int i = 0; i < domain.getPlatforms().size(); i++) {
                entity.getPlatforms().get(i).setStation(entity);
            }
        }
        if (domain.getGates() != null && entity.getGates() != null) {
            for (int i = 0; i < domain.getGates().size(); i++) {
                entity.getGates().get(i).setStation(entity);
            }
        }
        if (domain.getCorridors() != null && entity.getCorridors() != null) {
            for (int i = 0; i < domain.getCorridors().size(); i++) {
                entity.getCorridors().get(i).setStation(entity);
            }
        }
    }
    
    /**
     * 永続化モデルのリストからドメインモデルのリストへの変換
     */
    List<Station> toDomainList(List<StationEntity> entities);
    
    /**
     * ドメインモデルのリストから永続化モデルのリストへの変換
     */
    List<StationEntity> toEntityList(List<Station> domains);
    
    /**
     * 既存の永続化モデルをドメインモデルで更新
     */
    @Mapping(target = "platforms", ignore = true)
    @Mapping(target = "gates", ignore = true)
    @Mapping(target = "corridors", ignore = true)
    void updateEntityFromDomain(Station domain, @MappingTarget StationEntity entity);
    
    /**
     * LocationEmbeddable と Location の相互変換
     */
    Location toLocation(LocationEmbeddable embeddable);
    LocationEmbeddable toLocationEmbeddable(Location location);
}