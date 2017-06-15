package com.example.bharti.demo;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.example.bharti.demo.MessageDataSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListForums extends AppCompatActivity {
    private DatabaseReference userlistReference;
    private static final String TAG = "UserList" ;
    ArrayList<String> usernamelist = new ArrayList<>();
    private ValueEventListener mUserListListener;
    ArrayAdapter arrayAdapter;
    ListView userListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_users);
        getSupportActionBar().setTitle("Forums");
        getSupportActionBar().setHomeButtonEnabled(true);

        FloatingActionButton fab1 = (FloatingActionButton) findViewById(R.id.fab_button);
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG,"Clicked on fab button");
                Intent individualChatIntent = new Intent(ListForums.this, IndividualChatActivity.class);
                //individualChatIntent.putExtra("username", FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                startActivity(individualChatIntent);
            }
        });

        MessageDataSource groupsReference = new MessageDataSource();
        groupsReference.setsRef("https://smartcitychat.firebaseio.com/usernamelist/groups");
        userlistReference = FirebaseDatabase.getInstance().getReference().child("usernamelist/groups");
        onStart();
        Log.v(TAG, userlistReference.toString());
        userListView = (ListView) findViewById(R.id.userlistview);
        ListView lv = userListView;

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                Intent i = new Intent(ListForums.this, GroupChatActivity.class);
                i.putExtra("topicname", userListView.getItemAtPosition(position).toString());
                Log.v(TAG, userListView.getItemAtPosition(position).toString());
                startActivity(i);
            }
        });
    }


        @Override
    protected void onStart() {
        super.onStart();
        final ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> firebaseHashMap = new HashMap<>();
                for (DataSnapshot userNameSnapshot: dataSnapshot.getChildren()){
                    Object job = (Object) userNameSnapshot.getValue();
                    firebaseHashMap.put(userNameSnapshot.getKey(), job);
                }
                List<String> keys = new ArrayList<String>(firebaseHashMap.keySet());
                usernamelist = (ArrayList<String>) keys;
                Log.i(TAG, "onDataChange: "+ usernamelist.toString());
                arrayAdapter = new ArrayAdapter(ListForums.this,R.layout.activity_list_view,usernamelist);
                //usernamelist.remove(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                //Log.i(TAG, "onDataChange: "+ usernamelist.toString());
                userListView.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "onCancelled: ",databaseError.toException());
                Toast.makeText(ListForums.this, "Failed to load User list.",
                        Toast.LENGTH_SHORT).show();
            }
        };
        userlistReference.addValueEventListener(userListener);

        mUserListListener = userListener;
    }
}
