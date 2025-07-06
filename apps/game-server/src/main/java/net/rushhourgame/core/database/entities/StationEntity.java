package net.rushhourgame.core.database.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 駅の永続化モデル（JPA Entity）
 */
@Entity
@Table(name = "stations")
@Data
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"platforms", "gates", "corridors"})
public class StationEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "owner_id", nullable = false)
    private String ownerId;

    @Column(name = "total_capacity", nullable = false)
    private Integer totalCapacity;

    @Embedded
    private LocationEmbeddable location;

    @OneToMany(mappedBy = "station", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PlatformEntity> platforms = new ArrayList<>();

    @OneToMany(mappedBy = "station", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<GateEntity> gates = new ArrayList<>();

    @OneToMany(mappedBy = "station", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CorridorEntity> corridors = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "station_connected_tracks", joinColumns = @JoinColumn(name = "station_id"))
    @Column(name = "track_id")
    private List<String> connectedTrackIds = new ArrayList<>();
}