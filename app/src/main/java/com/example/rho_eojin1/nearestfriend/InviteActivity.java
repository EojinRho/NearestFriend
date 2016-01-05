package com.example.rho_eojin1.nearestfriend;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class InviteActivity extends AppCompatActivity {
    EditText edittext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
        Toast.makeText(getApplicationContext(),inputText,Toast.LENGTH_SHORT).show();
        return null;
    }
}
