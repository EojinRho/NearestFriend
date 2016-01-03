package com.example.rho_eojin1.nearestfriend;

import android.location.Location;

/**
 * Created by Rho-Eojin1 on 2016. 1. 2..
 */
public class Friend {
    private String name;
    private String id;
    private int password;
    private int profile;
    private Location location;
    private String currStatus;
    private String nextStatus;
    private float distance;

    public Friend(String name, String id, int password, int profile, Location location, String currStatus, String nextStatus){
        this.name = name;
        this.id = id;
        this.password = password;
        this.profile = profile;
        this.location = location;
        this.currStatus = currStatus;
        this.nextStatus = nextStatus;
    }
    public String getName(){return name;}
    public String getId(){return id;}
    public int getPassword(){return password;}
    public int getProfile(){return profile;}
    public Location getLocation(){return location;}
    public String getCurrStatus(){return currStatus;}
    public String getNextStatus(){return nextStatus;}
    public void updateDistance(Location myLocation){distance = myLocation.distanceTo(location);}
    public float getDistance(){return distance;}
}
