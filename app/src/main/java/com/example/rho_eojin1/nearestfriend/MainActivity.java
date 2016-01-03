package com.example.rho_eojin1.nearestfriend;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity {
    Location myLocation;
    Location newLocation;
    String myName;
    GPSTracker gps;
    ArrayList<Friend> friendlist;
    ArrayList<String> spinnerlist;
    ListView listview;
    ArrayList<Friend> temp;
    FriendAdapter m_adapter;
    EditText edittext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        myName = "Rho";
        gps = new GPSTracker(this);
        if(gps.canGetLocation){
            myLocation = gps.getLocation();
        }

        friendlist = new ArrayList<Friend>();

        edittext = (EditText) findViewById(R.id.editText1);
        edittext.setImeOptions(EditorInfo.IME_ACTION_DONE);
        edittext.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    myName = edittext.getText().toString();
                }
                return false;
            }
        });

        newLocation = new Location("Friend1");
        newLocation.setLatitude(36.3733869);
        newLocation.setLongitude(127.3649822);
        Friend friend1 = new Friend("Eojin","test1",0000, R.drawable.pianobackground, newLocation, "avaliable", "None");
        friend1.updateDistance(myLocation);
        friendlist.add(friend1);

        newLocation = new Location("Friend2");
        newLocation.setLatitude(36.3732869);
        newLocation.setLongitude(127.3647822);
        Friend friend2 = new Friend("haha","test2",0000, R.drawable.start, newLocation, "avaliable", "Game");
        friend2.updateDistance(myLocation);
        friendlist.add(friend2);

        newLocation = new Location("Friend3");
        newLocation.setLatitude(36.3732869);
        newLocation.setLongitude(127.3648822);
        Friend friend3 = new Friend("pinocio","test3",0000, R.drawable.startpiano, newLocation, "busy", "Game");
        friend3.updateDistance(myLocation);
        friendlist.add(friend3);
        sort();

        listview = (ListView)findViewById(R.id.listView);

        spinnerlist = new ArrayList<String>();
        spinnerlist.add("None");
        spinnerlist.add("Game");
        spinnerlist.add("Sports");
        spinnerlist.add("Shopping");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, spinnerlist);
        Spinner spinnerview = (Spinner) findViewById(R.id.spinner);
        spinnerview.setPrompt("Search");
        spinnerview.setAdapter(adapter);
        spinnerview.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (spinnerlist.get(position).equals("None")) {
                    m_adapter = new FriendAdapter(getApplicationContext(), R.layout.row, friendlist);
                    listview.setAdapter(m_adapter);
                }
                else {
                    temp = search(spinnerlist.get(position));
                    m_adapter = new FriendAdapter(getApplicationContext(), R.layout.row, temp);
                    listview.setAdapter(m_adapter);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Friend selected = (Friend) parent.getAdapter().getItem(position);
                Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                intent.putExtra("MyName",myName);
                intent.putExtra("FriendName",selected.getName());
                startActivity(intent);
            }
        });

        /*
        Button button1 = (Button) findViewById(R.id.button);
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), myLocation.getLatitude() + "   " + myLocation.getLongitude() + "   " + newLocation.getLatitude() + "   " + newLocation.getLongitude() + "   " + myLocation.distanceTo(newLocation), Toast.LENGTH_LONG).show();
            }
        });
        */
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
                TextView bt = (TextView) v.findViewById(R.id.bottomtext);
                if (im != null){
                    im.setImageResource(p.getProfile());
                }
                if (tt != null){
                    tt.setText(p.getName() + " wants : " + p.getNextStatus().toLowerCase());
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

    public void update(){

    }

    public void sort(){
        update();
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
            if(friendlist.get(i).getNextStatus().equals(status)){
                new_list.add(friendlist.get(i));
            }
        }
        return new_list;
    }
}
