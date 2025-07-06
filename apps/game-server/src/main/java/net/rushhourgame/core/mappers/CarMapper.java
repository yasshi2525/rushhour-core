package net.rushhourgame.core.mappers;

import net.rushhourgame.core.database.entities.CarEntity;
import net.rushhourgame.models.railway.Car;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * 車両ドメインモデルと永続化モデル間のマッピング
 */
@Mapper(componentModel = "spring")
public interface CarMapper {
    
    /**
     * 永続化モデルからドメインモデルへの変換
     */
    @Mapping(target = "trainId", source = "trainId")
    Car toDomain(CarEntity entity);
    
    /**
     * ドメインモデルから永続化モデルへの変換
     */
    @Mapping(target = "train", ignore = true)
    @Mapping(target = "trainId", source = "trainId")
    CarEntity toEntity(Car domain);
    
    /**
     * リストの変換
     */
    List<Car> toDomainList(List<CarEntity> entities);
    List<CarEntity> toEntityList(List<Car> domains);
}