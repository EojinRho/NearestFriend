package com.example.rho_eojin1.nearestfriend;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.firebase.ui.FirebaseListAdapter;

import java.util.Arrays;


public class ChatActivity extends ListActivity {
    String user1name;
    String user2name;
    private Firebase mFirebaseRef;
    FirebaseListAdapter<ChatMessage> mListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        Firebase.setAndroidContext(this);
        Bundle bundle = getIntent().getExtras();
        user1name = bundle.getString("MyName");
        user2name = bundle.getString("FriendName");
        String[] namelist = {user1name, user2name};
        Arrays.sort(namelist);
        mFirebaseRef = new Firebase("https://vivid-torch-1926.firebaseio.com").child(namelist[0] + namelist[1]);

        final EditText textEdit = (EditText) this.findViewById(R.id.text_edit);
        Button sendButton = (Button) this.findViewById(R.id.send_button);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = textEdit.getText().toString();
                ChatMessage message = new ChatMessage(user1name, text);
                mFirebaseRef.push().setValue(message);
                textEdit.setText("");
            }
        });

        mListAdapter = new FirebaseListAdapter<ChatMessage>(this, ChatMessage.class,
                android.R.layout.two_line_list_item, mFirebaseRef) {
            @Override
            protected void populateView(View v, ChatMessage model) {
                ((TextView)v.findViewById(android.R.id.text1)).setText(model.getName());
                ((TextView)v.findViewById(android.R.id.text2)).setText(model.getText());
            }
        };
        setListAdapter(mListAdapter);
    }

    protected void onDestroy() {
        super.onDestroy();
        mListAdapter.cleanup();
    }

}
