package net.rushhourgame.core.database.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * スケジュールの永続化モデル（JPA Entity）
 */
@Entity
@Table(name = "schedules")
@Data
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"train", "stopTimes"})
public class ScheduleEntity {
    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "route_id", nullable = false)
    private String routeId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "train_id", nullable = false)
    private TrainEntity train;

    @Column(name = "train_id", insertable = false, updatable = false)
    private String trainId;

    @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<StopTimeEntity> stopTimes = new ArrayList<>();
}