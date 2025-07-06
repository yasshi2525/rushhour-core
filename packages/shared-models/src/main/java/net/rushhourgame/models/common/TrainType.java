package net.rushhourgame.models.common;

/**
 * 電車種別を表すEnum
 * 仕様書：電車は種別（普通、快速、特急など）をもたせることができる。種別には優劣がある。
 */
public enum TrainType {
    /**
     * 普通電車 - 最も低い優先度
     */
    LOCAL("普通", 1),
    
    /**
     * 快速電車 - 中程度の優先度
     */
    RAPID("快速", 2),
    
    /**
     * 急行電車 - 高い優先度
     */
    EXPRESS("急行", 3),
    
    /**
     * 特急電車 - 最も高い優先度
     */
    LIMITED_EXPRESS("特急", 4);

    private final String displayName;
    private final int priority;

    TrainType(String displayName, int priority) {
        this.displayName = displayName;
        this.priority = priority;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getPriority() {
        return priority;
    }

    /**
     * 他の種別と比較して優先度が高いかを判定
     * @param other 比較対象の種別
     * @return この種別の方が優先度が高い場合true
     */
    public boolean hasHigherPriorityThan(TrainType other) {
        return this.priority > other.priority;
    }
}