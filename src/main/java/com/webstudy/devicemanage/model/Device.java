package com.webstudy.devicemanage.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Device {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column
    private String position;

    @Column
    private DeviceStatus status;

    @Column
    private Integer maintenanceCycle;

    @OneToMany(mappedBy = "device", cascade = CascadeType.ALL)
    @JsonIgnoreProperties(value = {"device"})
    private List<Repair> repairs;

    @OneToMany(mappedBy = "device", cascade = CascadeType.ALL)
    @JsonIgnoreProperties(value = {"device"})
    private List<Maintain> maintains;

    @OneToMany(mappedBy = "device", cascade = CascadeType.ALL)
    @JsonIgnoreProperties(value = {"device"})
    private List<Document> documents;


    public Device() {
    }

    public Device(String name, String position, Integer maintenanceCycle) {
        this.name = name;
        this.position = position;
        this.maintenanceCycle = maintenanceCycle;
        this.setStatus(DeviceStatus.NORMAL);
        this.setRepairs(new ArrayList<>());
        this.setMaintains(new ArrayList<>());
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public DeviceStatus getStatus() {
        return status;
    }

    public void setStatus(DeviceStatus status) {
        this.status = status;
    }

    public Integer getMaintenanceCycle() {
        return maintenanceCycle;
    }

    public void setMaintenanceCycle(Integer maintenanceCycle) {
        this.maintenanceCycle = maintenanceCycle;
    }

    public List<Repair> getRepairs() {
        return repairs;
    }

    public void setRepairs(List<Repair> repairs) {
        this.repairs = repairs;
    }

    public List<Maintain> getMaintains() {
        return maintains;
    }

    public void setMaintains(List<Maintain> maintains) {
        this.maintains = maintains;
    }

    public List<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }
}
