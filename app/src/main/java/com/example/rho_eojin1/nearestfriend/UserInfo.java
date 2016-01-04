package com.example.rho_eojin1.nearestfriend;

import android.location.Location;

import java.util.ArrayList;

/**
 * Created by Rho-Eojin1 on 2016. 1. 4..
 */
public class UserInfo {
    private static String name = "";
    private static String id = "";
    private static ArrayList<Friend> friendlist = new ArrayList<Friend>();
    private static Location myLocation = new Location("MyLocation");

    public static String getName(){ return name;}
    public static void setName(String na){
        name = na;
    }
    public static String getId(){
        return id;
    }
    public static void setId(String rn){ id = rn;}
    public static ArrayList<Friend> getFriendlist() { return friendlist;}
    public static void setFriendlist(ArrayList<Friend> fr){ friendlist = fr;}
    public static Location getMyLocation(){ return myLocation;}
    public static void setMyLocation(Location lo) {myLocation = lo;}
}
