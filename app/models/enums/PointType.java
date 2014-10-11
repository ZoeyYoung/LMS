package models.enums;

import java.util.Arrays;
import java.util.List;

public enum PointType {
    BOOKBORROW("借阅积分"), REWARDS("奖惩积分"), DONATION("捐赠积分"), EXCHANGE("兑换礼品");

    private final String type;

    PointType(final String type) {
        this.type = type;
    }

    public String ptype() {
        return type;
    }

    public static List<PointType> exchangeStatus() {
        PointType[] arr = { PointType.DONATION, PointType.EXCHANGE };
        return Arrays.asList(arr);
    }

    public static List<PointType> unexchangeStatus() {
        PointType[] arr = { PointType.BOOKBORROW, PointType.REWARDS };
        return Arrays.asList(arr);
    }

    public static List<PointType> exchangedStatus() {
        PointType[] arr = { PointType.EXCHANGE };
        return Arrays.asList(arr);
    }
}
