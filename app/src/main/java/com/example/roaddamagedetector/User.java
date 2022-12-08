package com.example.roaddamagedetector;


import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class User  extends RealmObject {

    @PrimaryKey
    private String uuid;

    private String name;
    private String password;
    private String path;

    public User() {}

    public User(String uuid, String name, String password, String path) {
        this.uuid = uuid;
        this.name = name;
        this.password = password;
        this.path = path;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "User{" +
                "uuid='" + uuid + '\'' +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", path='" + path + '\'' +
                '}';
    }
}