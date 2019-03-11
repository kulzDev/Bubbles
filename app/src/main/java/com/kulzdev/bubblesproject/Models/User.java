package com.kulzdev.bubblesproject.Models;

public class User {

    private String fullName;
    private String email;
    private String clientType;
    private String id;


    public User(){

    }

    public User(String fullName, String email, String clientType) {
        this.fullName = fullName;
        this.email = email;
        this.clientType = clientType;


    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getClientType() {
        return clientType;
    }

    public void setClientType(String clientType) {
        this.clientType = clientType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
