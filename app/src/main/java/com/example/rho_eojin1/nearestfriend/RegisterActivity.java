package com.example.rho_eojin1.nearestfriend;

import android.content.Context;
import android.os.AsyncTask;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by shinjaemin on 2016. 1. 2..
 */
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";
    OkHttpClient client;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        final TextInputLayout nameWrapper = (TextInputLayout) findViewById(R.id.input_name);
        final TextInputLayout usernameWrapper = (TextInputLayout) findViewById(R.id.input_username);
        final TextInputLayout passwordWrapper = (TextInputLayout) findViewById(R.id.input_password);
        final Button btn = (Button) findViewById(R.id.btn_signup);
        final TextView loginLink = (TextView) findViewById(R.id.link_login);

        try{
            client = OHclient.getClient();
        }catch(IOException e){
            e.printStackTrace();
        }

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                String name = nameWrapper.getEditText().getText().toString();
                String username = usernameWrapper.getEditText().getText().toString();
                String password = passwordWrapper.getEditText().getText().toString();
                if(name.length()<1){
                    nameWrapper.setError("Please input your FULL NAME to sign up!");
                }
                else if (username.length() < 1) {
                    usernameWrapper.setError("Please input username to sign up!");
                } else if (password.length() < 1) {
                    passwordWrapper.setError("Please input password to sign up!");
                } else {
                    nameWrapper.setErrorEnabled(false);
                    usernameWrapper.setErrorEnabled(false);
                    passwordWrapper.setErrorEnabled(false);
                    registerAsyncTask RAT = new registerAsyncTask();
                    RAT.execute(username, password, name);
                }
            }
        });

        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                finish();
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

    private JSONObject ifRegisterValidated(String name, String username, String password){
        RequestBody formBody = new FormEncodingBuilder()
                .add("username", username)
                .add("password", password)
                .add("realname", name)
                .build();
        Request request = new Request.Builder()
                .url("http://143.248.139.70:8000/signup")
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
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    private class registerAsyncTask extends AsyncTask<String, Void, JSONObject> {
        JSONObject res = null;
        String username = null;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            try{
                res = ifRegisterValidated(params[2],params[0],params[1]);
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
            try{
                if(jsonobject.get("status").equals("200")){
                    Context context = getApplicationContext();
                    CharSequence text = "Signup Success!";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context,text,duration);
                    toast.show();
                    UserInformation.setusername(username);
                    UserInformation.setrealname("Jaemin Shin");
                    setResult(RESULT_OK,null);
                    finish();
                }
                else if(jsonobject.get("status").equals("404")){
                    Context context = getApplicationContext();
                    CharSequence text = "Signup Failed! This username is currently being used.";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context,text,duration);
                    toast.show();
                }
            }catch(JSONException e){
                e.printStackTrace();
            }
        }
        /*
        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
        */
    }
}