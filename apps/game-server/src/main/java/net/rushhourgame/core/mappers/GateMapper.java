package net.rushhourgame.core.mappers;

import net.rushhourgame.core.database.entities.GateEntity;
import net.rushhourgame.models.railway.Gate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * 改札ドメインモデルと永続化モデル間のマッピング
 */
@Mapper(componentModel = "spring")
public interface GateMapper {
    
    /**
     * 永続化モデルからドメインモデルへの変換
     */
    @Mapping(target = "stationId", source = "stationId")
    @Mapping(target = "position", source = "position")
    Gate toDomain(GateEntity entity);
    
    /**
     * ドメインモデルから永続化モデルへの変換
     */
    @Mapping(target = "station", ignore = true)
    @Mapping(target = "stationId", source = "stationId")
    @Mapping(target = "position", source = "position")
    GateEntity toEntity(Gate domain);
    
    /**
     * リストの変換
     */
    List<Gate> toDomainList(List<GateEntity> entities);
    List<GateEntity> toEntityList(List<Gate> domains);
}