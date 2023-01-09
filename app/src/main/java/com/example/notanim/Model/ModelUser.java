package com.example.notanim.Model;

public class ModelUser {
    private String uid,name,email,online,usertype,timestamp;

    public ModelUser(String uid, String name, String email, String online, String usertype, String timestamp) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.online = online;
        this.usertype = usertype;
        this.timestamp = timestamp;
    }

    public ModelUser(){

    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOnline() {
        return online;
    }

    public void setOnline(String online) {
        this.online = online;
    }

    public String getUsertype() {
        return usertype;
    }

    public void setUsertype(String usertype) {
        this.usertype = usertype;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
