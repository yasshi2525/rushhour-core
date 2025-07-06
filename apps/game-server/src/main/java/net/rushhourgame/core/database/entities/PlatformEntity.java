package net.rushhourgame.core.database.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
 * プラットフォームの永続化モデル（JPA Entity）
 */
@Entity
@Table(name = "platforms", indexes = {
    @Index(name = "idx_platform_station", columnList = "station_id"),
    @Index(name = "idx_platform_track", columnList = "connected_track_id")
})
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(exclude = "station")
public class PlatformEntity extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "Connected track ID is required")
    @Column(name = "connected_track_id", nullable = false)
    private String connectedTrackId;

    @NotNull(message = "Capacity is required")
    @Min(value = 1, message = "Capacity must be positive")
    @Column(name = "capacity", nullable = false)
    private Integer capacity;

    @NotNull(message = "Station is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id", nullable = false)
    private StationEntity station;
    
}
