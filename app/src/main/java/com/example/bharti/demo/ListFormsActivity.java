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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListFormsActivity extends AppCompatActivity {
    private DatabaseReference formListReference;
    private static final String TAG = "FormsList" ;
    ArrayList<String> formsList = new ArrayList<>();
    private ValueEventListener mFormListListener;
    Map<String,String> linkMap = new HashMap<>();
    ArrayAdapter arrayAdapter;

    ListView formListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_forms);
        String username = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        MessageDataSource formsReference = new MessageDataSource();
        username = username.replaceAll("\\s","");
        formsReference.setsRef("https://smartcitychat.firebaseio.com/usernamelist/forms/" + username);

        getSupportActionBar().setTitle("Forms");
        getSupportActionBar().setHomeButtonEnabled(true);

        formListReference = FirebaseDatabase.getInstance().getReference().child(String.format(String.format("usernamelist/forms/%s", username)));
        onStart();
        Log.v(TAG, formListReference.toString());
        formListView = (ListView) findViewById(R.id.formlistview);
        ListView lv = formListView;

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {

                Intent i = new Intent(ListFormsActivity.this, FormActivity.class);
                //i.putExtra("formUrl", linkMap.get
                Log.v(TAG, formListView.getItemAtPosition(position).toString());
                startActivity(i);
            }
        });
    }

    protected void onStart() {
        super.onStart();
        final ValueEventListener formListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Map<String, Object> firebaseHashMap = new HashMap<>();
                for (DataSnapshot formSnapshot: dataSnapshot.getChildren()) {
                    Object job = (Object) formSnapshot.getValue();
                    firebaseHashMap.put(formSnapshot.getKey(), job);
                    linkMap.put(formSnapshot.getKey(), (String) formSnapshot.getValue());
                }

                List<String> keys = new ArrayList<String>(firebaseHashMap.keySet());
                formsList = (ArrayList<String>) keys;
                Log.i(TAG, "onDataChange: "+ formsList.toString());
                arrayAdapter = new ArrayAdapter(ListFormsActivity.this,android.R.layout.simple_list_item_1,formsList);
                formListView.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "onCancelled: ",databaseError.toException());
                Toast.makeText(ListFormsActivity.this, "Failed to load User list.",
                        Toast.LENGTH_SHORT).show();
            }
        };
        formListReference.addValueEventListener(formListener);

        mFormListListener = formListener;
    }
}
