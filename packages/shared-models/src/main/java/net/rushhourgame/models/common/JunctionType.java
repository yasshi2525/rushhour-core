package net.rushhourgame.models.common;

/**
 * 接続点（分岐点）の種別を表すEnum
 * 仕様書：線路は分岐・交差・合流させることができる
 */
public enum JunctionType {
    /**
     * 合流点 - 複数の線路が一つに合流する
     */
    MERGE("合流"),
    
    /**
     * 分岐点 - 一つの線路が複数に分岐する
     */
    SPLIT("分岐"),
    
    /**
     * 交差点 - 線路同士が交差する
     */
    CROSS("交差"),
    
    /**
     * 終端点 - 線路の終点
     */
    TERMINAL("終端");

    private final String displayName;

    JunctionType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * この接続点が分岐機能を持つかを判定
     * @return 分岐機能を持つ場合true
     */
    public boolean canSplit() {
        return this == SPLIT || this == CROSS;
    }

    /**
     * この接続点が合流機能を持つかを判定
     * @return 合流機能を持つ場合true
     */
    public boolean canMerge() {
        return this == MERGE || this == CROSS;
    }
}