package net.rushhourgame.core.database.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

/**
 * 位置情報の埋め込み可能クラス（JPA Embeddable）
 */
@Embeddable
@Data
public class LocationEmbeddable {
    @Column(name = "location_x", nullable = false)
    private Double x;

    @Column(name = "location_y", nullable = false)
    private Double y;

    @Column(name = "location_z", nullable = false)
    private Double z;
}