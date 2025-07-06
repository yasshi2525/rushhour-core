package net.rushhourgame.core.database.entities;

import jakarta.persistence.*;
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
@Table(name = "tracks")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(exclude = {"curve", "signals"})
public class TrackEntity extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(name = "owner_id", nullable = false)
    private String ownerId;

    @Column(name = "length", nullable = false)
    private Double length;

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