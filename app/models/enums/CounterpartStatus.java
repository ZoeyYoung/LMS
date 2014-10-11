package models.enums;

public enum CounterpartStatus {
    ONLOAD("借出"), OFFLOAD("在馆");

    private final String status;

    CounterpartStatus(final String status) {
        this.status = status;
    }

    public String status() {
        return status;
    }
}
