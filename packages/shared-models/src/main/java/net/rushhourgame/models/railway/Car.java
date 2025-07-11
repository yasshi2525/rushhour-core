package net.rushhourgame.models.railway;

import lombok.Data;

/**
 * 電車を構成する個々の車両を表すクラス。
 */
@Data
public class Car {
    private String id; // 車両ID
    private String trainId; // 所属する電車のID
    private int capacity; // 定員
    private int doorCount; // ドア数
}