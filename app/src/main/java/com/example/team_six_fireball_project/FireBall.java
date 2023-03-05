package com.example.team_six_fireball_project;

public class FireBall {
    String date;
    String energy;
    String impactE;
    String lat;
    String latDir;
    String lon;
    String lonDir;
    String alt;
    String vel;

    public FireBall() {
    }

    public FireBall(String date, String energy, String impactE, String lat, String latDir, String lon, String lonDir, String alt, String vel) {
        this.date = date;
        this.energy = energy;
        this.impactE = impactE;
        this.lat = lat;
        this.latDir = latDir;
        this.lon = lon;
        this.lonDir = lonDir;
        this.alt = alt;
        this.vel = vel;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getEnergy() {
        return energy;
    }

    public void setEnergy(String energy) {
        this.energy = energy;
    }

    public String getImpactE() {
        return impactE;
    }

    public void setImpactE(String impactE) {
        this.impactE = impactE;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLatDir() {
        return latDir;
    }

    public void setLatDir(String latDir) {
        this.latDir = latDir;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getLonDir() {
        return lonDir;
    }

    public void setLonDir(String lonDir) {
        this.lonDir = lonDir;
    }

    public String getAlt() {
        return alt;
    }

    public void setAlt(String alt) {
        this.alt = alt;
    }

    public String getVel() {
        return vel;
    }

    public void setVel(String vel) {
        this.vel = vel;
    }

    @Override
    public String toString() {
        return "FireBall{" +
                "date='" + date + '\'' +
                ", energy='" + energy + '\'' +
                ", impactE='" + impactE + '\'' +
                ", lat='" + lat + '\'' +
                ", latDir='" + latDir + '\'' +
                ", lon='" + lon + '\'' +
                ", lonDir='" + lonDir + '\'' +
                ", alt='" + alt + '\'' +
                ", vel='" + vel + '\'' +
                '}';
    }
}
