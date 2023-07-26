package com.webstudy.devicemanage.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;

@Entity
@Data
public class PasswordRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(targetEntity = User.class)
    @JoinColumn(name = "user", referencedColumnName = "id")
    private User user;

    @Column
    private String passwordEncrypt;


    public PasswordRecord() {
    }

    public PasswordRecord(User user, String passwordEncrypt) {
        this.user = user;
        this.passwordEncrypt = passwordEncrypt;
    }


    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

}

