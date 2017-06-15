package com.example.bharti.demo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.StringBuilderPrinter;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "SignupActivity";
    ImageView ivBackSignUp;
    EditText etFullName, etEmail, etPassword;
    Button bCreateUser;
    Long start, end;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_signup);

        etFullName = (EditText) findViewById(R.id.etFullName);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);
        bCreateUser = (Button) findViewById(R.id.bCreateUser);



        bCreateUser.setOnClickListener(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bCreateUser:
                if(etFullName.getText().toString().length() == 0 || etEmail.getText().toString().length() == 0 || etPassword.getText().toString().length() == 0){
                    Toast.makeText(this, "All fields mandatory", Toast.LENGTH_SHORT).show();
                }else{
                    // new AsyncTask().doInBackground();
                    Log.v(TAG, "User clicked on create");
                    AsyncT asyncT = new AsyncT(this);
                    asyncT.execute();
                }
                break;
        }
    }

    class AsyncT extends AsyncTask<Void,Void,String> {
        Context context;
        private AsyncT(Context context) {
            this.context = context.getApplicationContext();
        }

        @Override
        protected String doInBackground(Void... params) {

            try {
                User user = new User();
                user.setFullName(etFullName.getText().toString());
                user.setEmailId(etEmail.getText().toString());
                user.setPassword(etPassword.getText().toString());
                start = System.currentTimeMillis();

                URL url = new URL("http://ec2-54-153-112-71.us-west-1.compute.amazonaws.com:3000/api/addUser"); //Enter URL here

                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setRequestMethod("POST"); // here you are telling that it is a POST request, which can be changed into "PUT", "GET", "DELETE" etc.
                httpURLConnection.setRequestProperty("Content-Type", "application/json"); // here you are setting the `Content-Type` for the data you are sending which is `application/json`
                httpURLConnection.connect();

                Log.v(TAG, "Connected to URL json object");

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("fullName", user.getFullName());
                jsonObject.put("emailId",user.getEmailId());
                jsonObject.put("password",user.getPassword());
                Log.v(TAG, "Output Stream established");

                DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
                wr.writeBytes(jsonObject.toString());
                wr.flush();
                wr.close();

                Log.v(TAG,"closing connection");
                Log.v(TAG,"response code : " + httpURLConnection.getResponseCode());

                BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                String line="";
                StringBuilder response = new StringBuilder();

                while((line = br.readLine()) != null){
                    response.append(line);
                }

                String output = response.toString();
                return output;

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String str) {
            end = System.currentTimeMillis();
//            Log.v(TAG,Long.toString((end-start)/1000));
            if(str.contains("error")){
                JSONObject jsonResponse = null;
                try {
                    jsonResponse = new JSONObject(str);
                    String error = jsonResponse.getString("error");
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else{
                Intent intent = new Intent(context, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        }
    }
}
