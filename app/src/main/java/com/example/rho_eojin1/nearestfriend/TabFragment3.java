package com.example.rho_eojin1.nearestfriend;

/**
 * Created by shinjaemin on 2015. 12. 23..
 */

import android.graphics.Bitmap;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by shinjaemin on 2015. 12. 23..
 */

public class TabFragment3 extends Fragment {
    MapView mMapView;
    private GoogleMap googleMap;
    Location myLocation;
    ArrayList<Friend> friendlist;
    Map<String, String> map;
    OkHttpClient client;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // inflate and return the layout
        View v = inflater.inflate(R.layout.tab_fragment_3, container,
                false);
        mMapView = (MapView) v.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume();// needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        //MapsInitializer.initialize(getActivity());

        googleMap = mMapView.getMap();
        // latitude and longitude
        GPSTracker gps = new GPSTracker(this.getActivity());
        if(gps.canGetLocation){
            myLocation = gps.getLocation();
        }
        LatLng mylocation = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mylocation, 17));
        googleMap.addMarker(new MarkerOptions()
                .title("MyLocation")
                .position(mylocation));
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
        Log.e("getFriendlistatTab3", friendlist.size() + "");
        /*
        for (int i=0; i<friendlist.size(); ++i){
            LatLng newlocation = new LatLng(friendlist.get(i).getLocation().getLatitude(),friendlist.get(i).getLocation().getLongitude());
            try{
                googleMap.addMarker(new MarkerOptions()
                        .title(friendlist.get(i).getName())
                        .position(newlocation)
                        .icon(BitmapDescriptorFactory.fromBitmap(Picasso.with(getActivity()).load(friendlist.get(i).getProfile()).get())));
            }catch(IOException e){
                e.printStackTrace();
            }
        }
        */
        //FaceMarker fm = new FaceMarker();
        //fm.execute(friendlist.size());

        return v;
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
        private BitmapDescriptor[] bds;

        protected void onCancelled() {
            super.onCancelled();
        }

        protected Void doInBackground(Void... params) {
            try {
                json = getFriendsData(username);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
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
            //Toast.makeText(getContext(),"Happy",Toast.LENGTH_SHORT).show();

            bds = new BitmapDescriptor[friendlist.size()];

            for (int i=0; i<friendlist.size(); ++i){
                try{
                    bds[i] = BitmapDescriptorFactory.fromBitmap(Picasso.with(getActivity()).load(friendlist.get(i).getProfile()).get());
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
            return null;
        }

        protected void onPostExecute(Void result) {
            Log.e("onPost", bds.length+"");
            for (int i=0; i<friendlist.size(); ++i){
                LatLng newlocation = new LatLng(friendlist.get(i).getLocation().getLatitude(),friendlist.get(i).getLocation().getLongitude());
                googleMap.addMarker(new MarkerOptions()
                        .title(friendlist.get(i).getName())
                        .position(newlocation)
                        .icon(bds[i]));
            }
            super.onPostExecute(result);
        }
    }

    /*
    private class FaceMarker extends AsyncTask<Integer,Void,Void>{
        private BitmapDescriptor[] bds = new BitmapDescriptor[99999];
        @Override
        protected Void doInBackground(Integer... params){
            bds = new BitmapDescriptor[params[0]];
            for (int i=0; i<friendlist.size(); ++i){
                try{
                    bds[i] = BitmapDescriptorFactory.fromBitmap(Picasso.with(getActivity()).load(friendlist.get(i).getProfile()).get());
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
            return null;
        }

        protected void onPostExecute(Void result){
            Log.e("onPost", bds.length+"");
            for (int i=0; i<friendlist.size(); ++i){
                LatLng newlocation = new LatLng(friendlist.get(i).getLocation().getLatitude(),friendlist.get(i).getLocation().getLongitude());
                googleMap.addMarker(new MarkerOptions()
                        .title(friendlist.get(i).getName())
                        .position(newlocation)
                        .icon(bds[i]));
                Log.e("Urls", friendlist.get(i).getProfile().toString());
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }*/
}