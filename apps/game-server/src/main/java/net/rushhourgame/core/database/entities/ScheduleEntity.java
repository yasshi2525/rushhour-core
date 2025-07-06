package net.rushhourgame.core.database.entities;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

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
    

    @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<StopTimeEntity> stopTimes = new ArrayList<>();
}
