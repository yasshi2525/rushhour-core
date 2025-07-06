package net.rushhourgame.core.database.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import net.rushhourgame.models.common.TrainType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 電車の永続化モデル（JPA Entity）
 */
@Entity
@Table(name = "trains", indexes = {
    @Index(name = "idx_train_owner", columnList = "owner_id"),
    @Index(name = "idx_train_type", columnList = "train_type"),
    @Index(name = "idx_train_route", columnList = "assigned_route_id"),
    @Index(name = "idx_train_group", columnList = "group_id"),
    @Index(name = "idx_train_player_controlled", columnList = "is_player_controlled")
})
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(exclude = {"cars", "schedule"})
public class TrainEntity extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "Owner ID is required")
    @Column(name = "owner_id", nullable = false)
    private String ownerId;

    @NotNull(message = "Train type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "train_type", nullable = false)
    private TrainType trainType;

    @Column(name = "group_id")
    private String groupId;

    @NotNull(message = "Total capacity is required")
    @Min(value = 1, message = "Total capacity must be positive")
    @Column(name = "total_capacity", nullable = false)
    private Integer totalCapacity;

    @NotNull(message = "Door count is required")
    @Min(value = 1, message = "Door count must be positive")
    @Column(name = "door_count", nullable = false)
    private Integer doorCount;

    @NotNull(message = "Player controlled flag is required")
    @Column(name = "is_player_controlled", nullable = false)
    private Boolean isPlayerControlled;

    @Column(name = "assigned_route_id")
    private String assignedRouteId;

    @OneToMany(mappedBy = "train", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CarEntity> cars = new ArrayList<>();

    @OneToOne(mappedBy = "train", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private ScheduleEntity schedule;
}