package com.example.rho_eojin1.nearestfriend;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class StatusActivity extends AppCompatActivity {
    OkHttpClient client;
    Map<Integer, String> map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        client = new OkHttpClient();

        map = new HashMap<Integer, String>();
        map.put(0, "Busy");
        map.put(1, "Game");
        map.put(2, "Shopping");
        map.put(3, "Sports");
        map.put(4, "Drinking");

        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        ImageAdapter adapter = new ImageAdapter(this);
        viewPager.setAdapter(adapter);
    }

    public class ImageAdapter extends PagerAdapter {
        Context context;
        private int[] GalImages = new int[] {
                R.drawable.busy,
                R.drawable.game,
                R.drawable.shopping,
                R.drawable.sports,
                R.drawable.drinking
        };
        ImageAdapter(Context context){
            this.context=context;
        }
        @Override
        public int getCount() {
            return GalImages.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((ImageView) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            ImageView imageView = new ImageView(context);
            //int padding = context.getResources().getDimensionPixelSize(R.dimen.activity_horizontal_margin);
            //imageView.setPadding(padding, padding, padding, padding);
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            imageView.setImageResource(GalImages[position]);
            ((ViewPager) container).addView(imageView, 0);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println(position);
                    updataStatus task = new updataStatus();
                    task.username = "test3";
                    task.status = position;
                    task.execute();
                }
            });
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager) container).removeView((ImageView) object);
        }
    }

    private JSONObject updateStatusData(String username, int status) throws Exception{
        RequestBody formBody = new FormEncodingBuilder()
                .add("username", username)
                .add("status", String.valueOf(status))
                .build();
        Request request = new Request.Builder()
                .url("http://143.248.139.70:8000/api/updateStatus")
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

    private class updataStatus extends AsyncTask<Void, Void, Void> {
        JSONObject json = null;
        String username = "";
        int status;

        protected void onCancelled() {
            super.onCancelled();
        }

        protected Void doInBackground(Void... params) {
            try {
                json = updateStatusData(username,status);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Void result) {
            try {
                if(json.optString("status").toString().equals("200")) {
                    System.out.println("Status : 200");
                    Toast.makeText(getApplicationContext(),"Status updated! : "+ map.get(status),Toast.LENGTH_SHORT).show();
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
