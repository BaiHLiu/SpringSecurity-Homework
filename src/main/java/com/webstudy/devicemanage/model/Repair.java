package com.webstudy.devicemanage.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.Instant;

@Entity
public class Repair {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(targetEntity = Device.class)
    @JsonIgnoreProperties(value = { "repairs" })
    @JoinColumn(name = "device", referencedColumnName = "id")
    private Device device;

    @Column
    private Timestamp happenTime;

    @Column
    private String cause;

    @Column
    private boolean finished;

    @Column
    private Timestamp scheduledTime;

    @Column
    private Timestamp finishedTime;

    @Column
    private String executor;

    public Repair() {
    }

    public Repair(Device device, Long happenTime, String cause, Long scheduledTime) {
        this.device = device;
        this.happenTime = Timestamp.from(Instant.ofEpochMilli(happenTime));
        this.cause = cause;
        this.scheduledTime = Timestamp.from(Instant.ofEpochMilli(scheduledTime));
        this.finished = false;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public Timestamp getHappenTime() {
        return happenTime;
    }

    public void setHappenTime(Timestamp happenTime) {
        this.happenTime = happenTime;
    }

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public Timestamp getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(Timestamp scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    public Timestamp getFinishedTime() {
        return finishedTime;
    }

    public void setFinishedTime(Timestamp finishedTime) {
        this.finishedTime = finishedTime;
    }

    public String getExecutor() {
        return executor;
    }

    public void setExecutor(String executor) {
        this.executor = executor;
    }
}
