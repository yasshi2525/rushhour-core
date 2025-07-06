package net.rushhourgame.core.database.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * 信号機の永続化モデル（JPA Entity）
 */
@Entity
@Table(name = "signals")
@Data
@EqualsAndHashCode(of = "id")
@ToString(exclude = "track")
public class SignalEntity {
    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "signal_type", nullable = false)
    private String signalType;

    @Embedded
    private LocationEmbeddable position;

    @ElementCollection
    @CollectionTable(name = "signal_protected_tracks", joinColumns = @JoinColumn(name = "signal_id"))
    @Column(name = "track_id")
    private List<String> protectedTrackIds = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "track_id", nullable = false)
    private TrackEntity track;

    @Column(name = "track_id", insertable = false, updatable = false)
    private String trackId;
}