package net.rushhourgame.core.database.entities;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 改札の永続化モデル（JPA Entity）
 */
@Entity
@Table(name = "gates", indexes = {
    @Index(name = "idx_gate_station", columnList = "station_id")
})
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(exclude = "station")
public class GateEntity extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull(message = "Capacity is required")
    @Min(value = 1, message = "Capacity must be positive")
    @Column(name = "capacity", nullable = false)
    private Integer capacity;

    @NotNull(message = "Processing time is required")
    @Min(value = 1, message = "Processing time must be positive")
    @Column(name = "processing_time", nullable = false)
    private Double processingTime;

    @NotNull(message = "Position is required")
    @Embedded
    private LocationEmbeddable position;

    @NotNull(message = "Station is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id", nullable = false)
    private StationEntity station;
    
}
