package com.example.rho_eojin1.nearestfriend;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    Location myLocation;
    String myName;
    String myUserName;
    GPSTracker gps;
    ArrayList<Friend> friendlist;
    ArrayList<String> spinnerlist;
    ListView listview;
    ArrayList<Friend> temp;
    FriendAdapter m_adapter;
    EditText edittext;
    OkHttpClient client;
    Map<String, String> map;
    Map<String, Integer> rangeMap;
    Thread t;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        map = new HashMap<String, String>();
        map.put("0", "Busy");
        map.put("1", "Game");
        map.put("2", "Shopping");
        map.put("3", "Sports");
        map.put("4", "Drinking");

        //edit
        rangeMap = new HashMap<String, Integer>();
        rangeMap.put("1000m", 1000);
        rangeMap.put("2000m", 2000);
        rangeMap.put("3000m", 3000);
        rangeMap.put("4000m", 4000);
        rangeMap.put("5000m", 5000);

        client = new OkHttpClient();

        myName = "Rho";
        myUserName = "test0";
        gps = new GPSTracker(this);
        if(gps.canGetLocation){
            myLocation = gps.getLocation();
        }
        UserInfo.setName(myName);
        UserInfo.setId(myUserName);
        UserInfo.setMyLocation(myLocation);

        friendlist = new ArrayList<Friend>();

        listview = (ListView)findViewById(R.id.listView);

        getFriendsTask task = new getFriendsTask();
        task.username = myUserName;
        task.execute();

        spinnerlist = new ArrayList<String>();
        spinnerlist.add("All");
        spinnerlist.add("Avaliable");
        spinnerlist.add("Game");
        spinnerlist.add("Shopping");
        spinnerlist.add("Sports");
        spinnerlist.add("Drinking");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, spinnerlist);
        Spinner spinnerview = (Spinner) findViewById(R.id.spinner);
        spinnerview.setPrompt("Search");
        spinnerview.setAdapter(adapter);
        spinnerview.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //edit
                if (spinnerlist.get(position).equals("All")) {
                    m_adapter = new FriendAdapter(getApplicationContext(), R.layout.row, friendlist);
                    listview.setAdapter(m_adapter);
                } else if (spinnerlist.get(position).equals("Avaliable")) {
                    temp = exclude("Busy");
                    m_adapter = new FriendAdapter(getApplicationContext(), R.layout.row, temp);
                    listview.setAdapter(m_adapter);
                } else {
                    temp = search(spinnerlist.get(position));
                    m_adapter = new FriendAdapter(getApplicationContext(), R.layout.row, temp);
                    listview.setAdapter(m_adapter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinnerlist = new ArrayList<String>();
        spinnerlist.add("Infinite");
        spinnerlist.add("1000m");
        spinnerlist.add("2000m");
        spinnerlist.add("3000m");
        spinnerlist.add("4000m");
        spinnerlist.add("5000m");
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, spinnerlist);
        Spinner spinnerview2 = (Spinner) findViewById(R.id.spinner2);
        spinnerview2.setPrompt("Range");
        spinnerview2.setAdapter(adapter2);
        spinnerview2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //edit
                if (spinnerlist.get(position).equals("Infinite")) {
                    m_adapter = new FriendAdapter(getApplicationContext(), R.layout.row, friendlist);
                    listview.setAdapter(m_adapter);
                } else {
                    temp = rangedown(rangeMap.get(spinnerlist.get(position)));
                    m_adapter = new FriendAdapter(getApplicationContext(), R.layout.row, temp);
                    listview.setAdapter(m_adapter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        t = new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(60000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                System.out.println("Tick-Tock");
                                updataLocation task1 = new updataLocation();
                                task1.username = myUserName;
                                task1.location = myLocation;
                                task1.execute();
                                getFriendsTask task2 = new getFriendsTask();
                                task2.username = myUserName;
                                task2.execute();
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };
        t.start();

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /*Friend selected = (Friend) parent.getAdapter().getItem(position);
                Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                intent.putExtra("MyName",myName);
                intent.putExtra("FriendName",selected.getName());
                startActivity(intent);*/
                Intent intent = new Intent(getApplicationContext(), InviteActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class FriendAdapter extends ArrayAdapter<Friend> {
        ArrayList<Friend> items;
        public FriendAdapter(Context context, int textViewResourceId, ArrayList<Friend> items){
            super(context, textViewResourceId, items);
            this.items = items;
        }

        public View getView(int position, View convertView, ViewGroup parent){

            final Context context = parent.getContext();
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.row, null);
            }
            Friend p = items.get(position);
            if (p != null) {
                ImageView im = (ImageView) v.findViewById(R.id.imageView2);
                TextView tt = (TextView) v.findViewById(R.id.toptext);
                TextView mt = (TextView) v.findViewById(R.id.middletext);
                TextView bt = (TextView) v.findViewById(R.id.bottomtext);
                if (im != null){
                    Picasso.with(getApplicationContext()).load(p.getProfile()).fit().centerCrop().into(im);
                }
                if (tt != null){
                    tt.setText(p.getName() + "  id : " + p.getId());
                }
                if (mt != null){
                    mt.setText("Wants : " + p.getStatus().toLowerCase());
                }
                if(bt != null){
                    bt.setText("Distance : " + String.valueOf((int)p.getDistance()));
                }
            }

            if(position%2 == 0){
                v.setBackgroundColor(0x20111155);
            }
            else{
                v.setBackgroundColor(0x00FFFFFF);
            }

            return v;
        }
    }

    public void sort(){
        Collections.sort(friendlist, new Comparator<Friend>() {
            @Override
            public int compare(Friend lhs, Friend rhs) {
                return (lhs.getDistance() < rhs.getDistance()) ? -1: (lhs.getDistance() > rhs.getDistance()) ? 1:0 ;
            }
        });
    }

    public ArrayList<Friend> search(String status){
        ArrayList<Friend> new_list = new ArrayList<Friend>();
        for(int i=0; i<friendlist.size();++i){
            if(friendlist.get(i).getStatus().equals(status)){
                new_list.add(friendlist.get(i));
            }
        }
        return new_list;
    }

    public ArrayList<Friend> rangedown(int range){
        ArrayList<Friend> new_list = new ArrayList<Friend>();
        for(int i=0; i<friendlist.size();++i){
            if(friendlist.get(i).getDistance() < range){
                new_list.add(friendlist.get(i));
            }
        }
        return new_list;
    }

    public ArrayList<Friend> exclude(String status){
        ArrayList<Friend> new_list = new ArrayList<Friend>();
        for(int i = 0; i < friendlist.size();++i){
            if(!friendlist.get(i).getStatus().equals(status)){
                new_list.add(friendlist.get(i));
            }
        }
        return new_list;
    }


    private JSONObject getFriendsData(String username) throws Exception{
        RequestBody formBody = new FormEncodingBuilder()
                .add("username", username)
                .build();
        Request request = new Request.Builder()
                .url("http://143.248.139.70:8000/api/getFriends")
                .post(formBody)
                .build();

        JSONObject jsonObject = null;
        String jsonData = "";
        try {
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            jsonData = response.body().string();
            System.out.println(jsonData);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            jsonObject = new JSONObject(jsonData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    private class getFriendsTask extends AsyncTask<Void, Void, Void> {
        JSONObject json = null;
        String username = "";
        JSONObject friend;
        Friend newFriend;

        protected void onCancelled() {
            super.onCancelled();
        }

        protected Void doInBackground(Void... params) {
            try {
                json = getFriendsData(username);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Void result) {
            if (json==null) {
                Toast.makeText(getApplicationContext(), "it's null", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                friendlist.clear();
                JSONArray array = json.getJSONArray("getFriends");
                for (int i=0; i<array.length(); ++i){
                    friend = array.getJSONObject(i);
                    String name = friend.optString("realname").toString();
                    String id = friend.optString("username").toString();
                    //String profile = friend.optString("");
                    Location newLocation = new Location("newLocation");
                    newLocation.setLatitude(((Number) friend.get("latitude")).doubleValue());
                    newLocation.setLongitude(((Number) friend.get("longitude")).doubleValue());
                    String status = map.get(friend.optString("status").toString());
                    String profile = friend.optString("picture").toString();
                    newFriend = new Friend(name,id,profile,newLocation,status);
                    newFriend.updateDistance(myLocation);
                    friendlist.add(newFriend);
                }
                //listview.deferNotifyDataSetChanged();
                //Toast.makeText(getApplicationContext(), friendlist.get(1).getName() + "  " +friendlist.get(1).getId()+ "  " +friendlist.get(1).getStatus()+ "  " + friendlist.size(),Toast.LENGTH_LONG).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            sort();
            UserInfo.setFriendlist(friendlist);
            //edit
            //ArrayList<Friend> temp = exclude("Busy");
            m_adapter = new FriendAdapter(getApplicationContext(), R.layout.row, friendlist);
            listview.setAdapter(m_adapter);
            super.onPostExecute(result);
        }
    }

    private JSONObject updateLocationData(String username, Location location) throws Exception{
        RequestBody formBody = new FormEncodingBuilder()
                .add("username", username)
                .add("latitude", String.valueOf(location.getLatitude()))
                .add("longitude", String.valueOf(location.getLongitude()))
                .build();
        Request request = new Request.Builder()
                .url("http://143.248.139.70:8000/api/updateLocation")
                .post(formBody)
                .build();

        JSONObject jsonObject = null;
        String jsonData = "";
        try {
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            jsonData = response.body().string();
            System.out.println(jsonData);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            jsonObject = new JSONObject(jsonData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    private class updataLocation extends AsyncTask<Void, Void, Void> {
        JSONObject json = null;
        String username = "";
        Location location;

        protected void onCancelled() {
            super.onCancelled();
        }

        protected Void doInBackground(Void... params) {
            try {
                json = updateLocationData(username, location);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Void result) {
            try {
                if(json.optString("status").toString().equals("200")) {
                    System.out.println("Status : 200");
                }
                else if (json.get("status").equals("404")) {
                    System.out.println("Status : 404");
                }
                else {
                    System.out.println("Fuck you start over");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            super.onPostExecute(result);
        }
    }
}
