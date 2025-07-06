package net.rushhourgame.models.route;

import lombok.Data;

/**
 * 電車の運行経路を表すクラス。
 */
@Data
public class Route {
    private String id; // 経路ID
    private String name; // 経路名
    // 経路を構成する線路セグメントや駅のリストなど、詳細な経路情報をここに追加
}
