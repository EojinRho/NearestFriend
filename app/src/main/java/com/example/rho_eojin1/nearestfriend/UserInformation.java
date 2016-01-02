package com.example.rho_eojin1.nearestfriend;

/**
 * Created by shinjaemin on 2016. 1. 2..
 */
public class UserInformation {
    private static String user_name;
    private static String real_name;

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
}
