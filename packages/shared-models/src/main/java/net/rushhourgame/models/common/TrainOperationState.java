package net.rushhourgame.models.common;

/**
 * 電車の運行状態を表すEnum
 * 仕様書：リアルタイム状態管理で使用される
 */
public enum TrainOperationState {
    /**
     * 停車中 - 駅や信号待ちで停車している状態
     */
    STOPPED("停車中"),
    
    /**
     * 走行中 - 線路上を移動している状態
     */
    MOVING("走行中"),
    
    /**
     * 乗降中 - 駅で乗客の乗り降りを行っている状態
     */
    BOARDING("乗降中"),
    
    /**
     * 緊急停止 - 緊急事態により停止している状態
     */
    EMERGENCY("緊急停止"),
    
    /**
     * 回送運転 - 利用客を載せずに運行している状態
     */
    DEADHEAD("回送運転"),
    
    /**
     * 退避中 - 上位種別の電車を待つため退避している状態
     */
    AWAITING("退避中");

    private final String displayName;

    TrainOperationState(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * この状態で電車が移動可能かを判定
     * @return 移動可能な場合true
     */
    public boolean canMove() {
        return this == MOVING || this == DEADHEAD;
    }

    /**
     * この状態で乗客の乗降が可能かを判定
     * @return 乗降可能な場合true
     */
    public boolean canBoard() {
        return this == BOARDING;
    }

    /**
     * この状態が緊急状態かを判定
     * @return 緊急状態の場合true
     */
    public boolean isEmergency() {
        return this == EMERGENCY;
    }
}