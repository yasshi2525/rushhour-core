package net.rushhourgame.core.database.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
 * 車両の永続化モデル（JPA Entity）
 */
@Entity
@Table(name = "cars", indexes = {
    @Index(name = "idx_car_train", columnList = "train_id")
})
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(exclude = "train")
public class CarEntity extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull(message = "Capacity is required")
    @Min(value = 1, message = "Capacity must be positive")
    @Column(name = "capacity", nullable = false)
    private Integer capacity;

    @NotNull(message = "Door count is required")
    @Min(value = 1, message = "Door count must be positive")
    @Column(name = "door_count", nullable = false)
    private Integer doorCount;

    @NotNull(message = "Train is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "train_id", nullable = false)
    private TrainEntity train;
  
}
