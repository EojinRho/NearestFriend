package com.example.rho_eojin1.nearestfriend;

import android.location.Location;

/**
 * Created by Rho-Eojin1 on 2016. 1. 2..
 */
public class Friend {
    private String name;
    private String id;
    private String profile;
    private Location location;
    private String status;
    private float distance;

    public Friend(String name, String id, String profile, Location location, String status){
        this.name = name;
        this.id = id;
        this.profile = profile;
        this.location = location;
        this.status = status;
    }
    public String getName(){return name;}
    public String getId(){return id;}
    public String getProfile(){return profile;}
    public Location getLocation(){return location;}
    public String getStatus(){return status;}
    public void updateDistance(Location myLocation){distance = myLocation.distanceTo(location);}
    public float getDistance(){return distance;}
}
