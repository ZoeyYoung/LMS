package models.enums;

import java.util.Arrays;
import java.util.List;

public enum BorrowStatus {
    UNRETURNED("未归还"), RETURNED("已归还"), RENEW("续借"), OVERDUE("已逾期");

    private final String status;

    BorrowStatus(final String status) {
        this.status = status;
    }

    public String status() {
        return status;
    }

    public static List<BorrowStatus> unreturnedStatus() {
        BorrowStatus[] bss = { BorrowStatus.OVERDUE, BorrowStatus.RENEW, BorrowStatus.UNRETURNED };
        return Arrays.asList(bss);
    }

    public static List<BorrowStatus> overdueStatus() {
        BorrowStatus[] bss = { BorrowStatus.OVERDUE };
        return Arrays.asList(bss);
    }

    public static List<BorrowStatus> returnedStatus() {
        BorrowStatus[] bss = { BorrowStatus.RETURNED };
        return Arrays.asList(bss);
    }
}
