package com.ai.master.order.domain.model;

public enum OrderAction {
    CREATE("CREATE", "创建订单"),
    SUBMIT("SUBMIT", "提交订单"),
    PAY("PAY", "支付订单"),
    DELIVERY("DELIVERY", "发货"),
    RECEIVE("RECEIVE", "收货"),
    REFUND("REFUND", "退货"),
    CANCEL("CANCEL", "取消订单"),
    FINISH("FINISH", "完成订单");

    private final String name;
    private final String title;

    OrderAction(String name, String title) {
        this.name = name;
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public String toString() {
        return name;
    }
} 