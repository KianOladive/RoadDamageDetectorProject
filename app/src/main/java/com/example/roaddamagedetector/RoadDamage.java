package com.example.roaddamagedetector;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RoadDamage extends RealmObject {

    @PrimaryKey
    private String uuid;

    private int entryId;
    private String damageType;
    private Double latitude;
    private Double longitude;
    private String userName;
    private String location;
    private String date;
    private String path;

    public RoadDamage() {}

    public RoadDamage(String uuid, int entryId, String damageType, double latitude, double longitude, String userName, String location, String date, String path) {
        this.uuid = uuid;
        this.entryId = entryId;
        this.damageType = damageType;
        this.latitude = latitude;
        this.longitude = longitude;
        this.userName = userName;
        this.location = location;
        this.date = date;
        this.path = path;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getEntryId() {
        return entryId;
    }

    public void setEntryId(int entryId) {
        this.entryId = entryId;
    }

    public String getDamageType() {
        return damageType;
    }

    public void setDamageType(String damageType) {
        this.damageType = damageType;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "RoadDamage{" +
                "uuid='" + uuid + '\'' +
                ", entryId=" + entryId +
                ", damageType='" + damageType + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", userName='" + userName + '\'' +
                ", location='" + location + '\'' +
                ", date='" + date + '\'' +
                ", path='" + path + '\'' +
                '}';
    }
}
