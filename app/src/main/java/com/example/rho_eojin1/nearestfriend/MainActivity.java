package com.example.rho_eojin1.nearestfriend;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import net.ser1.stomp.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    OkHttpClient client;
    StompClient stompClient;
    private final int REQUEST_REGISTER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TextInputLayout usernameWrapper = (TextInputLayout) findViewById(R.id.usernameWrapper);
        final TextInputLayout passwordWrapper = (TextInputLayout) findViewById(R.id.passwordWrapper);
        final Button btn = (Button) findViewById(R.id.btn);
        final TextView registerLink = (TextView) findViewById(R.id.register_link);
        usernameWrapper.setHint("Username");
        passwordWrapper.setHint("Password");
        try{
            client = OHclient.getClient();
        }catch(IOException e){
            e.printStackTrace();
        }
        try{
            this.stompClient = StompClient.getClient(getApplicationContext(), this);
        }catch(Exception e){
            e.printStackTrace();
        }

        class CustomOnClickListener implements View.OnClickListener {
            StompClient mClient;
            public CustomOnClickListener(StompClient client) {
                this.mClient = client;
            }
            public void onClick(View v) {
                hideKeyboard();
                String username = usernameWrapper.getEditText().getText().toString();
                String password = passwordWrapper.getEditText().getText().toString();
                if (username.length() < 1) {
                    usernameWrapper.setError("Please input username to login!");
                } else if (password.length() < 1) {
                    passwordWrapper.setError("Please input password to login!");
                } else {
                    usernameWrapper.setErrorEnabled(false);
                    passwordWrapper.setErrorEnabled(false);
                    loginAsyncTask LAT = new loginAsyncTask(this.mClient);
                    LAT.execute(username, password);
                }
            }
        }

        CustomOnClickListener customOnClickListener = new CustomOnClickListener(this.stompClient);

        btn.setOnClickListener(customOnClickListener);
        registerLink.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private JSONObject ifLoginValidated(String username, String password) throws Exception{
        RequestBody formBody = new FormEncodingBuilder()
                .add("username", username)
                .add("password", password)
                .build();
        Request request = new Request.Builder()
                .url("http://143.248.139.70:8000/login")
                .post(formBody)
                .build();

        JSONObject jsonObject = null;
        String jsonData = "";
        try {
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            jsonData = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            jsonObject = new JSONObject(jsonData);
            Log.e("Login", jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
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

    private class loginAsyncTask extends AsyncTask<String, Void, JSONObject> {
        JSONObject res = null;
        String username = null;
        StompClient mClient;
        public loginAsyncTask(StompClient client) {
            this.mClient = client;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            try{
                res = ifLoginValidated(params[0],params[1]);
                username = params[0];
            }catch(Exception e){
                e.printStackTrace();
            }
            return res;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            super.onPostExecute(result);
            JSONObject jsonobject = result;
            String destination = "/queue/" + result.optString("username");
            mClient.addQueue(destination);
                if(jsonobject.optString("status").equals("200")){
                    Context context = getApplicationContext();
                    CharSequence text = "Login Success!";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context,text,duration);
                    toast.show();
                    UserInformation.setusername(jsonobject.optString("username").toString());
                    UserInformation.setrealname(jsonobject.optString("realname").toString());
                    UserInformation.setpictureurl(jsonobject.optString("picture").toString());
                    Intent logedin_intent = new Intent(getApplicationContext(), LogedInActivity.class);
                    startActivity(logedin_intent);
                    finish();
                }
                else if(jsonobject.optString("status").equals("404")){
                    Context context = getApplicationContext();
                    CharSequence text = "Login Failed! Check your username and password.";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context,text,duration);
                    toast.show();
                }

        }

        protected JSONObject getres(){
            return res;
        }
        /*
        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
        */
    }
}
