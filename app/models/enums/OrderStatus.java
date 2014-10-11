package models.enums;

import java.util.Arrays;
import java.util.List;

public enum OrderStatus {
    UNBUYED("未购入"), BUYED("已购入"), NOBUY("暂不购入");

    private final String status;

    OrderStatus(final String status) {
        this.status = status;
    }

    public String status() {
        return status;
    }

    public static List<OrderStatus> handledStatus() {
        OrderStatus[] bss = { OrderStatus.BUYED, OrderStatus.NOBUY };
        return Arrays.asList(bss);
    }

    public static List<OrderStatus> unhandledStatus() {
        OrderStatus[] bss = { OrderStatus.UNBUYED };
        return Arrays.asList(bss);
    }

}
