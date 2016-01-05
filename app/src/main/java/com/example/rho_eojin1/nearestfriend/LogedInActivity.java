package com.example.rho_eojin1.nearestfriend;

import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by shinjaemin on 2016. 1. 2..
 */
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.v4.app.Fragment;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import net.ser1.stomp.Client;
import net.ser1.stomp.Stomp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LogedInActivity extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener {

    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;
    private OkHttpClient client;
    private StompClient sClient;

    ArrayList<Friend> friendlist;
    Map<String, String> map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loged_in);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        try{
            sClient = StompClient.getClient(this, LogedInActivity.this);
        }catch(Exception e){
            e.printStackTrace();
        }

        try{
            client = OHclient.getClient();
        }catch(IOException e){
            e.printStackTrace();
        }
        map = new HashMap<String, String>();
        map.put("0", "Busy");
        map.put("1", "Game");
        map.put("2", "Shopping");
        map.put("3", "Sports");
        map.put("4", "Drinking");
        friendlist = new ArrayList<Friend>();

        getFriendsTask task = new getFriendsTask();
        task.username = UserInformation.getusername();
        task.execute();

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        drawerFragment = (FragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this, this);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Friends"));
        tabLayout.addTab(tabLayout.newTab().setText("Location"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount(), this);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
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
                    friendlist.add(newFriend);
                }
                //listview.deferNotifyDataSetChanged();
                //Toast.makeText(getApplicationContext(), friendlist.get(1).getName() + "  " +friendlist.get(1).getId()+ "  " +friendlist.get(1).getStatus()+ "  " + friendlist.size(),Toast.LENGTH_LONG).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            UserInformation.setFriendlist(friendlist);
            Log.e("Friendlist", friendlist.size() + "");
            Log.e("getFriendlist", UserInformation.getFriendlist().size()+"");
            super.onPostExecute(result);
        }
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

    @Override
    public void onDrawerItemSelected(View view, int position) {
        displayView(position);
    }

    private void displayView(int position) {
        Fragment fragment = null;
        String title = getString(R.string.app_name);
        switch (position) {
            case 0:
                Intent intent1 = new Intent(getApplicationContext(), StatusActivity.class);
                startActivity(intent1);
                break;
            case 1:
                Intent intent2 = new Intent(getApplicationContext(), InviteActivity.class);
                startActivity(intent2);
                break;
            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.commit();

            // set the toolbar title
            getSupportActionBar().setTitle(title);
        }
    }
}
