package net.rushhourgame.core.database.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * プラットフォームの永続化モデル（JPA Entity）
 */
@Entity
@Table(name = "platforms")
@Data
@EqualsAndHashCode(of = "id")
@ToString(exclude = "station")
public class PlatformEntity {
    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "connected_track_id", nullable = false)
    private String connectedTrackId;

    @Column(name = "capacity", nullable = false)
    private Integer capacity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id", nullable = false)
    private StationEntity station;

    @Column(name = "station_id", insertable = false, updatable = false)
    private String stationId;
}