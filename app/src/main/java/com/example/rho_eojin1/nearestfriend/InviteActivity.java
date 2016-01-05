package com.example.rho_eojin1.nearestfriend;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
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

import java.io.IOException;


public class InviteActivity extends AppCompatActivity {
    EditText edittext;
    OkHttpClient ohclient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);
        try{
            ohclient = OHclient.getClient();
        }catch(IOException e){
            e.printStackTrace();
        }

        edittext = (EditText) findViewById(R.id.editText1);
        edittext.setImeOptions(EditorInfo.IME_ACTION_DONE);
        edittext.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    inviteFriends();
                }
                return false;
            }
        });

        findViewById(R.id.searchButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inviteFriends();
            }
        });
    }

    private Void inviteFriends(){
        String inputText = edittext.getText().toString();
        inviteAsync ia = new inviteAsync();
        String[] input = new String[2];
        input[0] = UserInformation.getusername();
        input[1] = inputText;
        ia.execute(input);
        return null;
    }

    private class inviteAsync extends AsyncTask<String, Void, Void> {
        String toasttext ="";
        protected Void doInBackground(String... param){
            RequestBody formBody = new FormEncodingBuilder()
                    .add("username", param[0])
                    .add("request_username", param[1])
                    .build();
            Request request = new Request.Builder()
                    .url("http://143.248.139.70:8000/api/friendRequest")
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
                toasttext = "Success!";
            }
            else if(jsonObject.optString("status").equals("404")){
                toasttext = "The user does not exist";
            }
            else if(jsonObject.optString("status").equals("500")){
                toasttext = "Server has problem";
            }
            else if(jsonObject.optString("status").equals("304")){
                toasttext = "Already sent request.";
            }
            return null;
        }
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(getApplicationContext(), toasttext, Toast.LENGTH_SHORT).show();
        }
    }
}
