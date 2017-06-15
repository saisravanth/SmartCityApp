package com.example.bharti.demo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static io.fabric.sdk.android.Fabric.TAG;


public class ListIssues extends AppCompatActivity {
    private static final String TAG = "ListIssuesActivity";
    ArrayAdapter adpt;
    ArrayList<String> result = new ArrayList<>();
    ListView lView;
    String email;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_issues);
        getSupportActionBar().setTitle("ISSUES");
        getSupportActionBar().setHomeButtonEnabled(true);
        new ArrayAdapter(ListIssues.this, R.layout.activity_list_view, 0);

        lView = (ListView) findViewById(R.id.lvissue);
        if ( FirebaseAuth.getInstance().getCurrentUser() != null ) {
            email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        } else {
            smartCityChat context = (smartCityChat)getApplicationContext();
            email  = context.getUser().getEmailId();
        }
        // Exec async load task
        (new AsyncListViewLoader()).execute("http://ec2-54-153-112-71.us-west-1.compute.amazonaws.com:3000/api/users/issues/"+email);

        lView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent i = new Intent(ListIssues.this, DisplayIssue.class);
                i.putExtra("id", lView.getItemAtPosition(position).toString());
                Log.v(TAG, lView.getItemAtPosition(position).toString());
                startActivity(i);
            }
        });

        FloatingActionButton fab1 = (FloatingActionButton) findViewById(R.id.fab_button);
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG,"Clicked on fab button");
                Intent issue = new Intent(ListIssues.this, IssueActivity.class);
                issue.putExtra("from", "ListIssues");
                startActivity(issue);
            }
        });
    }

    class AsyncListViewLoader extends AsyncTask<String, Void, List<String>> {
        private final ProgressDialog dialog = new ProgressDialog(ListIssues.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Retrieving issues...");
            dialog.show();
        }

        @Override
        protected List<String> doInBackground(String... params) {

            try {
                URL u = new URL(params[0]);

                HttpURLConnection conn = (HttpURLConnection) u.openConnection();
                conn.setRequestMethod("GET");
                conn.connect();
                InputStream is = conn.getInputStream();

                // Read the stream
                byte[] b = new byte[1024];
                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                while ( is.read(b) != -1)
                    baos.write(b);

                String JSONResp = new String(baos.toByteArray());
                Log.v(TAG, JSONResp);

                JSONArray arr = new JSONArray(JSONResp);
                for (int i=0; i < arr.length(); i++) {
                    result.add(convertIssue(arr.getJSONObject(i)));
                }

                return result;
            }
            catch(Throwable t) {
                t.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<String> result) {
            super.onPostExecute(result);
            dialog.dismiss();
            adpt  = new ArrayAdapter(ListIssues.this, R.layout.activity_list_view, result);
            lView.setAdapter(adpt);
        }

        private String convertIssue(JSONObject obj) throws JSONException {
            String id = obj.getString("_id");
            String title = obj.getString("category");
            return id;
        }

    }
}
