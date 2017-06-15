package com.example.bharti.demo;


import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DisplayIssue extends AppCompatActivity {
    private static final String TAG = "DisplayIssueActivity";
    TextView etIssueid, etCategory, etDescription, etLocation, etStatus, etDate;
    String issueId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_issue);
        getSupportActionBar().setTitle("ISSUE");
        getSupportActionBar().setHomeButtonEnabled(true);

        etIssueid = (TextView) findViewById(R.id.etIssueid);
        etCategory = (TextView) findViewById(R.id.etCategory);
        etDescription = (TextView) findViewById(R.id.etDescription);
        etLocation = (TextView) findViewById(R.id.etLocation);
        etStatus = (TextView) findViewById(R.id.etStatus);
        etDate = (TextView) findViewById(R.id.etDate);
        issueId = getIntent().getStringExtra("id").trim();
        Log.v(TAG, issueId);

        DisplayIssue.AsyncT asyncT = new DisplayIssue.AsyncT(this);
        asyncT.execute();
    }

    class AsyncT extends AsyncTask<Void, Void, String> {
        Context context;

        private AsyncT(Context context) {
            this.context = context.getApplicationContext();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                URL url = new URL("http://ec2-54-153-112-71.us-west-1.compute.amazonaws.com:3000/api/users/issue/" + issueId);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
//                httpURLConnection.setDoOutput(true);
                httpURLConnection.setRequestMethod("GET"); // here you are telling that it is a POST request, which can be changed into "PUT", "GET", "DELETE" etc.
//                httpURLConnection.setRequestProperty("Content-Type", "application/json"); // here you are setting the `Content-Type` for the data you are sending which is `application/json`
                httpURLConnection.connect();

                Log.v(TAG, "Connected to URL json object");

                InputStream is = httpURLConnection.getInputStream();

                // Read the stream
                byte[] b = new byte[1024];
                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                while (is.read(b) != -1)
                    baos.write(b);

                String JSONResp = new String(baos.toByteArray());
                Log.v(TAG, JSONResp);

                return JSONResp;

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String str) {
            if (str.contains("\"error\"")) {
                JSONObject jsonResponse = null;
                try {
                    jsonResponse = new JSONObject(str);
                    String error = jsonResponse.getString("error");
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                JSONObject jsonResponse = null;
                try {
                    jsonResponse = new JSONObject(str);
                    String id = jsonResponse.getString("_id");
                    etIssueid.setText(id);
                    Log.v(TAG, id);
                    etCategory.setText(jsonResponse.getString("category"));
                    etDescription.setText(jsonResponse.getString("description"));
                    etLocation.setText(jsonResponse.getString("location"));
                    etDate.setText(jsonResponse.getString("date"));
                    etStatus.setText(jsonResponse.getString("status"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
