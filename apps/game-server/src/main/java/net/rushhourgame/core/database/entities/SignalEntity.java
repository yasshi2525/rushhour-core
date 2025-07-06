package net.rushhourgame.core.database.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import net.rushhourgame.models.common.SignalType;

/**
 * 信号機の永続化モデル（JPA Entity）
 */
@Entity
@Table(name = "signals", indexes = {
    @Index(name = "idx_signal_type", columnList = "signal_type"),
    @Index(name = "idx_signal_track", columnList = "track_id")
})
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(exclude = "track")
public class SignalEntity extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull(message = "Signal type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "signal_type", nullable = false)
    private SignalType signalType;

    @NotNull(message = "Position is required")
    @Embedded
    private LocationEmbeddable position;

    @ElementCollection
    @CollectionTable(name = "signal_protected_tracks", joinColumns = @JoinColumn(name = "signal_id"))
    @Column(name = "track_id")
    private List<String> protectedTrackIds = new ArrayList<>();

    @NotNull(message = "Track is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "track_id", nullable = false)
    private TrackEntity track;
    
}
