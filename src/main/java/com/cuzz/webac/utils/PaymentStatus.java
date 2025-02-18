package com.cuzz.webac.utils;

public enum PaymentStatus {
    PAID((byte) 0, "已付"),
    UNPAID((byte) 1, "未付"),
    REFUNDED((byte) 2, "已退款");

    // 成员变量
    private final byte code;
    private final String description;

    // 构造函数
    PaymentStatus(byte code, String description) {
        this.code = code;
        this.description = description;
    }

    // 获取对应的 byte 值
    public byte getCode() {
        return code;
    }

    // 获取描述
    public String getDescription() {
        return description;
    }

    // 根据 byte 值获取枚举实例
    public static PaymentStatus fromCode(byte code) {
        for (PaymentStatus status : PaymentStatus.values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid code: " + code);
    }
}