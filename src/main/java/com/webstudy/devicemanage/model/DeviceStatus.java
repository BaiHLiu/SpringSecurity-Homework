package com.webstudy.devicemanage.model;

public enum DeviceStatus {
    NORMAL, NEED_REPAIR, NEED_MAINT;

    public String getStatus() {
        return name();
    }
}
