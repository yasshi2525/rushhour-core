package net.rushhourgame.core.database.entities;

import jakarta.persistence.*;
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
@Table(name = "trains")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(exclude = {"cars", "schedule"})
public class TrainEntity extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(name = "owner_id", nullable = false)
    private String ownerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "train_type", nullable = false)
    private TrainType trainType;

    @Column(name = "group_id")
    private String groupId;

    @Column(name = "total_capacity", nullable = false)
    private Integer totalCapacity;

    @Column(name = "door_count", nullable = false)
    private Integer doorCount;

    @Column(name = "is_player_controlled", nullable = false)
    private Boolean isPlayerControlled;

    @Column(name = "assigned_route_id")
    private String assignedRouteId;

    @OneToMany(mappedBy = "train", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CarEntity> cars = new ArrayList<>();

    @OneToOne(mappedBy = "train", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private ScheduleEntity schedule;
}