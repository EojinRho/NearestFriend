package com.example.rho_eojin1.nearestfriend;

import android.location.Location;

import java.util.ArrayList;

/**
 * Created by shinjaemin on 2016. 1. 2..
 */
public class UserInformation {
    private static String user_name;
    private static String real_name;
    private static String picture_url;
    private static ArrayList<Friend> friendlist = new ArrayList<Friend>();
    private static Location myLocation = new Location("MyLocation");

    public static String getusername(){
        return user_name;
    }
    public static void setusername(String un){
        user_name = un;
    }
    public static String getrealname(){
        return real_name;
    }
    public static void setrealname(String rn){
        real_name = rn;
    }
    public static String getpictureurl(){
        return picture_url;
    }
    public static void setpictureurl(String pu){
        picture_url = pu;
    }
    public static ArrayList<Friend> getFriendlist() { return friendlist;}
    public static void setFriendlist(ArrayList<Friend> fr){ friendlist = fr;}
    public static Location getMyLocation(){ return myLocation;}
    public static void setMyLocation(Location lo) {myLocation = lo;}
}
