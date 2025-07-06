package net.rushhourgame.core.database.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 線路の永続化モデル（JPA Entity）
 */
@Entity
@Table(name = "tracks", indexes = {
    @Index(name = "idx_track_owner", columnList = "owner_id"),
    @Index(name = "idx_track_start_junction", columnList = "start_junction_id"),
    @Index(name = "idx_track_end_junction", columnList = "end_junction_id")
})
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(exclude = {"curve", "signals"})
public class TrackEntity extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "Owner ID is required")
    @Column(name = "owner_id", nullable = false)
    private String ownerId;

    @NotNull(message = "Length is required")
    @Min(value = 1, message = "Length must be positive")
    @Column(name = "length", nullable = false)
    private Double length;

    @NotNull(message = "Max speed is required")
    @Min(value = 1, message = "Max speed must be positive")
    @Column(name = "max_speed", nullable = false)
    private Double maxSpeed;

    @Column(name = "start_junction_id")
    private String startJunctionId;

    @Column(name = "end_junction_id")
    private String endJunctionId;

    @OneToMany(mappedBy = "track", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Point3DEmbeddable> curve = new ArrayList<>();

    @OneToMany(mappedBy = "track", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SignalEntity> signals = new ArrayList<>();
}