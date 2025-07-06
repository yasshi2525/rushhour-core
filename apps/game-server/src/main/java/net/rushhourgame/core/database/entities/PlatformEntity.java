package net.rushhourgame.core.database.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
 * プラットフォームの永続化モデル（JPA Entity）
 */
@Entity
@Table(name = "platforms")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(exclude = "station")
public class PlatformEntity extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(name = "connected_track_id", nullable = false)
    private String connectedTrackId;

    @Column(name = "capacity", nullable = false)
    private Integer capacity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id", nullable = false)
    private StationEntity station;
    
}
