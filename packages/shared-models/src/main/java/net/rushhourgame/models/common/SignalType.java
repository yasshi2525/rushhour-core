package net.rushhourgame.models.common;

/**
 * 信号機の種別を表すEnum
 * 仕様書：電車の衝突回避実現のため、信号機を配置することができる
 */
public enum SignalType {
    /**
     * 閉塞信号機 - 単純な区間制御
     */
    BLOCK("閉塞信号機", "基本的な区間制御を行う信号機"),
    
    /**
     * 進路信号機 - 複雑な進路制御
     */
    PATH("進路信号機", "分岐点での進路制御を行う信号機"),
    
    /**
     * 絶対信号機 - 最高優先度の制御
     */
    ABSOLUTE("絶対信号機", "緊急時や重要区間での絶対制御を行う信号機"),
    
    /**
     * 入換信号機 - 車庫・駅構内での制御
     */
    SHUNTING("入換信号機", "車庫や駅構内での入換作業用信号機");

    private final String displayName;
    private final String description;

    SignalType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    /**
     * この信号機が主要線路での制御に使用されるかを判定
     * @return 主要線路用の場合true
     */
    public boolean isMainLineSignal() {
        return this == BLOCK || this == PATH || this == ABSOLUTE;
    }

    /**
     * この信号機が緊急制御機能を持つかを判定
     * @return 緊急制御機能を持つ場合true
     */
    public boolean hasEmergencyControl() {
        return this == ABSOLUTE;
    }
}