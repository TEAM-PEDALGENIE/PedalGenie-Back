package com.pedalgenie.pedalgenieback.domain.demo.entity;

public enum DemoStatus {
    SCHEDULED("시연예정"),
    COMPLETED("시연완료"),
    CANCELED("시연취소");

    private final String statusDescription;

    DemoStatus(String statusDescription) {
        this.statusDescription = statusDescription;
    }

    public String getStatusDescription() {
        return statusDescription;
    }
}
