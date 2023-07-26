package com.webstudy.devicemanage.dto;

import java.sql.Timestamp;

public class RepairDTO {
    private Long happenTime;
    private String cause;
    private Long scheduledTime;

    public Long getHappenTime() {
        return happenTime;
    }

    public void setHappenTime(Long happenTime) {
        this.happenTime = happenTime;
    }

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }

    public Long getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(Long scheduledTime) {
        this.scheduledTime = scheduledTime;
    }
}
