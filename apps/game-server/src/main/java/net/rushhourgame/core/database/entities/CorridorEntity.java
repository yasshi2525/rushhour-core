package net.rushhourgame.core.database.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 通路の永続化モデル（JPA Entity）
 */
@Entity
@Table(name = "corridors")
@Data
@EqualsAndHashCode(of = "id")
@ToString(exclude = "station")
public class CorridorEntity {
    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "length", nullable = false)
    private Double length;

    @Column(name = "width", nullable = false)
    private Double width;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id", nullable = false)
    private StationEntity station;

    @Column(name = "station_id", insertable = false, updatable = false)
    private String stationId;
}