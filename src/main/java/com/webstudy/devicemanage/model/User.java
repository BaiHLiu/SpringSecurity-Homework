package com.webstudy.devicemanage.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;

@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column
    private String password;

    @Column
    private String role;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String name) {
        this.username = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public ArrayList<String> getRoleList(){
        ArrayList<String> roles = new ArrayList<String>();
        roles.add("ROLE_ADMIN");
        roles.add("ROLE_OPERATOR");
        roles.add("ROLE_WORKER");

        return roles;
    }


}
