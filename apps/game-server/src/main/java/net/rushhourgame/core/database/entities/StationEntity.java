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
 * 駅の永続化モデル（JPA Entity）
 */
@Entity
@Table(name = "stations", indexes = {
    @Index(name = "idx_station_name", columnList = "name"),
    @Index(name = "idx_station_owner", columnList = "owner_id"),
    @Index(name = "idx_station_location", columnList = "location_x, location_y")
})
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(exclude = {"platforms", "gates", "corridors"})
public class StationEntity extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "Station name is required")
    @Column(name = "name", nullable = false)
    private String name;

    @NotBlank(message = "Owner ID is required")
    @Column(name = "owner_id", nullable = false)
    private String ownerId;

    @NotNull(message = "Total capacity is required")
    @Min(value = 1, message = "Total capacity must be positive")
    @Column(name = "total_capacity", nullable = false)
    private Integer totalCapacity;

    @NotNull(message = "Location is required")
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