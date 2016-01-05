package com.example.rho_eojin1.nearestfriend;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import net.ser1.stomp.Client;
import net.ser1.stomp.Listener;
import net.ser1.stomp.Stomp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by shinjaemin on 2016. 1. 5..
 */
public class StompClient {
    public static StompClient stClient;
    public Client mClient;
    public List<String> queue;
    public Boolean busy = true;
    public static Context stompcontext;
    public static Activity stompactivity;
    private OkHttpClient ohclient;

    public StompClient(Context ct, Activity at) {
        queue = new ArrayList<String>();
        stompcontext = ct;
        stompactivity = at;
        try{
            ohclient = OHclient.getClient();
        }catch(IOException e){
            e.printStackTrace();
        }
        new ConnectAsyncTask().execute(this.mClient);
    }

    public static StompClient getClient(Context ct, Activity at) throws Exception{
        if(stClient==null){
            stClient = new StompClient(ct, at);
            return stClient;
        }
        stompcontext = ct;
        stompactivity = at;
        return stClient;
    }

    public class ConnectAsyncTask extends AsyncTask<Client, Client, Client> {

        @Override
        protected Client doInBackground(Client... clients) {
            try {
                mClient = new Client("143.248.139.70", 61613, "stomp", "stomp");
                Log.e("Stomp", "Login");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return clients[0];
        }

        @Override
        protected void onPostExecute(Client client) {

            for(int i=0; i < queue.size(); i++) {
                Log.e("Destination", queue.get(i));
                mClient.subscribe(queue.get(i), new Listener() {
                    @Override
                    public void message(Map map, String s) {
                        Log.e("Stomp", map.toString());
                        Log.e("Stomp", s);
                        if(map.get("action").toString().equals("requestFriendRequest")){
                            CustomRunnable r = new CustomRunnable(stompactivity, map);
                            stompactivity.runOnUiThread(r);
                            Log.e("Stomp", s);
                        }
                        else if(map.get("action").toString().equals("inviteChatroom")){

                        }
                    }
                });
            }
            queue.clear();
            busy = false;

        }
    }

    public void addQueue(String s) {
        if(this.busy) {
            queue.add(s);
        }
        else {
            this.mClient.subscribe(s, new Listener() {
                @Override
                public void message(Map map, String s) {
                    Log.e("Stomp", map.toString());
                    Log.e("Stomp", s);
                    if(map.get("action").toString().equals("requestFriendRequest")){
                        Log.e("requestFriendRequest", stompactivity.toString());
                        CustomRunnable r = new CustomRunnable(stompactivity, map);
                        stompactivity.runOnUiThread(r);
                        Log.e("Stomp", s);
                    }
                    else if(map.get("action").toString().equals("inviteChatroom")){
                        Log.e("inviteChatroom", stompactivity.toString());
                        chatCustomRunnable r = new chatCustomRunnable(stompactivity, map);
                        stompactivity.runOnUiThread(r);
                        Log.e("Stomp",s);
                    }
                }
            });
        }
    }
    public class chatCustomRunnable implements Runnable {
        public Activity act;
        public Map m;
        public chatCustomRunnable(Activity act, Map m) {
            this.act = act;
            this.m = m;
        }
        @Override
        public void run() {
            String test = m.get("realname") + " invited you to a chatroom.\nWould you accept?";
            final RequestAns ra = new RequestAns();
            new MaterialDialog.Builder(stompcontext)
                    .title("Chat Request")
                    .content(test)
                    .positiveText("Aceept")
                    .negativeText("Discard")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            String[] input = new String[3];
                            input[0] = "CHAT_ACCEPT";
                            input[1] = UserInformation.getrealname();
                            input[2] = m.get("realname").toString();
                            ra.execute(input);
                        }
                    })
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which){
                            String[] input = new String[3];
                            input[0] = "CHAT_DISCARD";
                            ra.execute(input);
                        }
                    })
                    .show();
        }
    }

    public class CustomRunnable implements Runnable {
        public Activity act;
        public Map m;
        public CustomRunnable(Activity act, Map m) {
            this.act = act;
            this.m = m;
        }
        @Override
        public void run() {
            String test = m.get("realname") + " added you as a friend.\nWould you accept?";
            final RequestAns ra = new RequestAns();
            new MaterialDialog.Builder(stompcontext)
                    .title("Friend Request")
                    .content(test)
                    .positiveText("Aceept")
                    .negativeText("Deny")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            String[] input = new String[3];
                            input[0] = "FR_ACCEPT";
                            input[1] = UserInformation.getusername();
                            input[2] = m.get("username").toString();
                            ra.execute(input);
                        }
                    })
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which){
                            String[] input = new String[3];
                            input[0] = "FR_DENY";
                            input[1] = UserInformation.getusername();
                            input[2] = m.get("username").toString();
                            ra.execute(input);
                        }
                    })
                    .show();
        }
    }

    private class RequestAns extends AsyncTask<String, Void, Void>{
        String toasttext= "";
        @Override
        protected Void doInBackground(String... params){
            switch(params[0]){
                case "FR_ACCEPT": // param[1] = username, param[2] = request_username
                    RequestBody formBody = new FormEncodingBuilder()
                            .add("username", params[1])
                            .add("request_username", params[2])
                            .build();
                    Request request = new Request.Builder()
                            .url("http://143.248.139.70:8000/api/friendAccept")
                            .post(formBody)
                            .build();

                    JSONObject jsonObject = null;
                    String jsonData = "";
                    try {
                        Response response = ohclient.newCall(request).execute();
                        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                        jsonData = response.body().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        jsonObject = new JSONObject(jsonData);
                        Log.e("FriendRequest", jsonObject.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if(jsonObject.optString("status").equals("200")){
                        toasttext = "New Friend Accepted";
                    }
                    else if(jsonObject.optString("status").equals("404")){
                        toasttext = "The user does not exist";
                    }
                    else if(jsonObject.optString("status").equals("500")){
                        toasttext = "Server has problem";
                    }
                    else if(jsonObject.optString("status").equals("304")){
                        toasttext = "Already friend!";
                    }
                    return null;
                case "FR_DENY":
                    RequestBody formBody1 = new FormEncodingBuilder()
                            .add("username", params[1])
                            .add("request_username", params[2])
                            .build();
                    Request request1 = new Request.Builder()
                            .url("http://143.248.139.70:8000/friendDeny")
                            .post(formBody1)
                            .build();

                    JSONObject jsonObject1 = null;
                    String jsonData1 = "";
                    try {
                        Response response = ohclient.newCall(request1).execute();
                        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                        jsonData = response.body().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        jsonObject = new JSONObject(jsonData1);
                        Log.e("FriendRequest", jsonObject.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if(jsonObject1.optString("status").equals("200")){
                        toasttext = "New Friend Denied";
                    }
                    else if(jsonObject1.optString("status").equals("404")){
                        toasttext = "The user does not exist";
                    }
                    else if(jsonObject1.optString("status").equals("500")){
                        toasttext = "Server has problem";
                    }
                    else if(jsonObject1.optString("status").equals("304")){
                        toasttext = "Already friend!";
                    }
                    return null;
                case "CHAT_ACCEPT":
                    Intent intent = new Intent(stompcontext, ChatActivity.class);
                    intent.putExtra("MyName",params[1]);
                    intent.putExtra("FriendName",params[2]);
                    stompcontext.startActivity(intent);
                    return null;
                case "CHAT_DISCARD":
                    toasttext = "Successfully Denied!";
                    return null;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(stompcontext, toasttext, Toast.LENGTH_SHORT).show();
        }
    }
}
