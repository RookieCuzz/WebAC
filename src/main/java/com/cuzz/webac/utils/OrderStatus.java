package com.cuzz.webac.utils;

public enum OrderStatus {
    PENDING_PAYMENT((byte) 0, "待支付"),
    PROCESSING((byte) 1, "处理中"),
    SHIPPED((byte) 2, "已发货"),
    DELIVERED((byte) 3, "已签收"),
    COMPLETED((byte) 4, "已完成"),
    CANCELED((byte) 5, "已取消"),
    REFUND_REQUESTED((byte) 6, "要退款");

    // 成员变量
    private final byte code;
    private final String description;

    // 构造函数
    OrderStatus(byte code, String description) {
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
    public static OrderStatus fromCode(byte code) {
        for (OrderStatus status : OrderStatus.values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid code: " + code);
    }
}