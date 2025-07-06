
package net.rushhourgame.models.common;

import lombok.Data;

/**
 * 地理的な位置情報を表すクラス。
 */
@Data
public class Location {
    private double x;
    private double y;
    private double z;

    public Location(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
