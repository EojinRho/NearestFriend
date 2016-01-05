package com.example.rho_eojin1.nearestfriend;

import com.squareup.okhttp.OkHttpClient;

import java.io.IOException;

/**
 * Created by shinjaemin on 2016. 1. 2..
 */
public class OHclient {

    private static OkHttpClient client;

        public static OkHttpClient getClient() throws IOException{
            if(client == null){
                client = new OkHttpClient();
            }
            return client;
    }
}
