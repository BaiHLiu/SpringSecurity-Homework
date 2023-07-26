package com.webstudy.devicemanage.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import javax.print.Doc;


@Entity
@JsonIgnoreProperties(value = {"user","uuid"})
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(targetEntity = Device.class)
    @JsonIgnoreProperties(value = {"repairs", "maintains"})
    @JoinColumn(name = "device", referencedColumnName = "id")
    private Device device;


    @ManyToOne(targetEntity = User.class)
    @JoinColumn(name = "user", referencedColumnName = "id")
    private User user;

    @Transient
    private String username;


    @Column
    private String uuid;

    @Column
    private String fileName;

    public Document(){

    }
    public Document(Device device, String uuid, String fileName, User user) {
        this.device = device;
        this.uuid = uuid;
        this.fileName = fileName;
        this.user = user;
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

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getUsername() {
        if (user != null) {
            return user.getUsername();
        }
        return username;
    }

    public User getUser() {
        return user;
    }
}
