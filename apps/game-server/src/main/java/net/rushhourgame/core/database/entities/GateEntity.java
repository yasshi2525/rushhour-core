package net.rushhourgame.core.database.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 改札の永続化モデル（JPA Entity）
 */
@Entity
@Table(name = "gates")
@Data
@EqualsAndHashCode(of = "id")
@ToString(exclude = "station")
public class GateEntity {
    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "capacity", nullable = false)
    private Integer capacity;

    @Column(name = "processing_time", nullable = false)
    private Double processingTime;

    @Embedded
    private LocationEmbeddable position;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id", nullable = false)
    private StationEntity station;

    @Column(name = "station_id", insertable = false, updatable = false)
    private String stationId;
}