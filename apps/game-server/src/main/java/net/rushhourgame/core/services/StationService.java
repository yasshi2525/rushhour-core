package net.rushhourgame.core.services;

import lombok.RequiredArgsConstructor;
import net.rushhourgame.core.database.entities.StationEntity;
import net.rushhourgame.core.database.repositories.StationRepository;
import net.rushhourgame.core.exceptions.EntityNotFoundException;
import net.rushhourgame.core.mappers.StationMapper;
import net.rushhourgame.models.station.Station;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 駅ドメインサービス
 * ドメインモデルのみを扱い、永続化の詳細は隠蔽
 */
@Service
@RequiredArgsConstructor
@Transactional
public class StationService {
    
    private final StationRepository stationRepository;
    private final StationMapper stationMapper;
    
    /**
     * 駅を保存
     */
    public Station save(Station station) {
        StationEntity entity = stationMapper.toEntity(station);
        StationEntity savedEntity = stationRepository.save(entity);
        return stationMapper.toDomain(savedEntity);
    }
    
    /**
     * IDで駅を取得
     */
    @Transactional(readOnly = true)
    public Optional<Station> findById(String id) {
        return stationRepository.findById(id)
            .map(stationMapper::toDomain);
    }
    
    /**
     * 名前で駅を取得
     */
    @Transactional(readOnly = true)
    public Optional<Station> findByName(String name) {
        return stationRepository.findByName(name)
            .map(stationMapper::toDomain);
    }
    
    /**
     * 所有者IDで駅を取得
     */
    @Transactional(readOnly = true)
    public List<Station> findByOwnerId(String ownerId) {
        List<StationEntity> entities = stationRepository.findByOwnerId(ownerId);
        return stationMapper.toDomainList(entities);
    }
    
    /**
     * 全ての駅を取得
     */
    @Transactional(readOnly = true)
    public List<Station> findAll() {
        List<StationEntity> entities = stationRepository.findAll();
        return stationMapper.toDomainList(entities);
    }
    
    /**
     * 接続されている線路IDで駅を検索
     */
    @Transactional(readOnly = true)
    public List<Station> findByConnectedTrackId(String trackId) {
        List<StationEntity> entities = stationRepository.findByConnectedTrackId(trackId);
        return stationMapper.toDomainList(entities);
    }
    
    /**
     * 指定された範囲内の駅を検索
     */
    @Transactional(readOnly = true)
    public List<Station> findByLocationRange(Double minX, Double maxX, Double minY, Double maxY) {
        List<StationEntity> entities = stationRepository.findByLocationRange(minX, maxX, minY, maxY);
        return stationMapper.toDomainList(entities);
    }
    
    /**
     * 駅を更新
     */
    public Station update(Station station) {
        StationEntity entity = stationRepository.findById(station.getId())
            .orElseThrow(() -> new EntityNotFoundException("Station", station.getId()));
        
        stationMapper.updateEntityFromDomain(station, entity);
        StationEntity savedEntity = stationRepository.save(entity);
        return stationMapper.toDomain(savedEntity);
    }
    
    /**
     * 駅を削除
     */
    public void deleteById(String id) {
        stationRepository.deleteById(id);
    }
    
    /**
     * 駅が存在するかチェック
     */
    @Transactional(readOnly = true)
    public boolean existsById(String id) {
        return stationRepository.existsById(id);
    }
}