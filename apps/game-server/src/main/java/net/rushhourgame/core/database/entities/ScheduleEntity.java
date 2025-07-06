package net.rushhourgame.core.database.entities;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
 * スケジュールの永続化モデル（JPA Entity）
 */
@Entity
@Table(name = "schedules", indexes = {
    @Index(name = "idx_schedule_route", columnList = "route_id"),
    @Index(name = "idx_schedule_train", columnList = "train_id")
})
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(exclude = {"train", "stopTimes"})
public class ScheduleEntity extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "Route ID is required")
    @Column(name = "route_id", nullable = false)
    private String routeId;

    @NotNull(message = "Train is required")
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "train_id", nullable = false)
    private TrainEntity train;
    

    @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<StopTimeEntity> stopTimes = new ArrayList<>();
}
