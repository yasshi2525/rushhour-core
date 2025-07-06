package net.rushhourgame.core.database.entities;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 改札の永続化モデル（JPA Entity）
 */
@Entity
@Table(name = "gates")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(exclude = "station")
public class GateEntity extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(name = "capacity", nullable = false)
    private Integer capacity;

    @Column(name = "processing_time", nullable = false)
    private Double processingTime;

    @Embedded
    private LocationEmbeddable position;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id", nullable = false)
    private StationEntity station;
    
}
