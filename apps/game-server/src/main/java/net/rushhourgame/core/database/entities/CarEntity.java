package net.rushhourgame.core.database.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 車両の永続化モデル（JPA Entity）
 */
@Entity
@Table(name = "cars")
@Data
@EqualsAndHashCode(of = "id")
@ToString(exclude = "train")
public class CarEntity {
    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "capacity", nullable = false)
    private Integer capacity;

    @Column(name = "door_count", nullable = false)
    private Integer doorCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "train_id", nullable = false)
    private TrainEntity train;
  
}
