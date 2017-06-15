package com.example.bharti.demo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.customtabs.CustomTabsIntent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "LoginActivity";
    private static final int SIGN_IN_REQUEST_CODE = 1;
    private FirebaseListAdapter<Message> adapter;
    ImageView ivBackLogin;
    EditText etUsername, etLoginPassword;
    Button bLogin;
    com.google.android.gms.common.SignInButton gLogin;
    String email=null;
    String userFullName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);

        etUsername = (EditText) findViewById(R.id.etEmailId);
        etLoginPassword = (EditText) findViewById(R.id.etLoginPassword);
        bLogin = (Button) findViewById(R.id.bLogin);
        gLogin = (com.google.android.gms.common.SignInButton) findViewById(R.id.login_with_google);
        bLogin.setOnClickListener(this);
        gLogin.setOnClickListener(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        FirebaseAuth.getInstance().getCurrentUser();

    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){

            case R.id.bLogin:
                if(etUsername.getText().toString().length() == 0 || etLoginPassword.getText().toString().length() == 0){
                    Toast.makeText(this, "All fields mandatory", Toast.LENGTH_SHORT).show();
                }
                else{
                    Log.v(TAG, "User clicked on Login");
                    LoginAsyncT asyncT = new LoginAsyncT(this);
                    asyncT.execute();
                }
                break;
            case R.id.login_with_google:
                if(FirebaseAuth.getInstance().getCurrentUser() == null) {
                    Log.d(TAG,"trying to get firebase instance");
                    // Start sign in/sign up activity
                    startActivityForResult(
                            AuthUI.getInstance().createSignInIntentBuilder().build(),
                            SIGN_IN_REQUEST_CODE
                    );

                } else {
                    // User is already signed in. Therefore, display
                    // a welcome Toast
                    Toast.makeText(this,
                            "Welcome " + FirebaseAuth.getInstance()
                                    .getCurrentUser()
                                    .getDisplayName(),
                            Toast.LENGTH_LONG)
                            .show();
                    Intent intent = new Intent(this, NavigationActivity.class);
                    startActivity(intent);
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SIGN_IN_REQUEST_CODE) {
            if(resultCode == RESULT_OK) {
                Toast.makeText(this,
                        "Successfully signed in. Welcome!",
                        Toast.LENGTH_LONG)
                        .show();

                Intent intent = new Intent(this,NavigationActivity.class);
                intent.putExtra("emailId", email);
                startActivity(intent);
            } else {
                Toast.makeText(this,
                        "We couldn't sign you in. Please try again later.",
                        Toast.LENGTH_LONG)
                        .show();
                finish();
            }
        }
    }

    public class LoginAsyncT extends AsyncTask<Void,Void,String> {

        Context context;
        private LoginAsyncT(Context context) {
            this.context = context.getApplicationContext();
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL("http://ec2-54-153-112-71.us-west-1.compute.amazonaws.com:3000/api/login");
                User user = new User();
                user.setEmailId(etUsername.getText().toString());
                user.setPassword(etLoginPassword.getText().toString());
                email = etUsername.getText().toString();

                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setRequestMethod("POST"); // here you are telling that it is a POST request, which can be changed into "PUT", "GET", "DELETE" etc.
                httpURLConnection.setRequestProperty("Content-Type", "application/json"); // here you are setting the `Content-Type` for the data you are sending which is `application/json`
                httpURLConnection.connect();

                Log.v(TAG, "Connected to URL json object");

                JSONObject jsonObject = new JSONObject();
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
                Log.v(TAG,output);
                if (!(output.contains("error"))) {
                    JSONArray mainObject = new JSONArray(output);
                    userFullName = mainObject.getJSONObject(0).getString("fullName");
                    Log.v(TAG, "display name = " + userFullName);
                }
                return output;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String str) {
            if(str.contains("\"error\"")){
                JSONObject jsonResponse = null;
                try {
                    jsonResponse = new JSONObject(str);
                    String error = jsonResponse.getString("error");
                    Log.v(TAG,error);
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else{
                Log.d(TAG,"Successfully logged in!");
                Intent intent = new Intent(LoginActivity.this, NavigationActivity.class);
                smartCityChat context = (smartCityChat)getApplicationContext();
                context.setUser(context.createUser(email, etLoginPassword.getText().toString(), userFullName));
                startActivity(intent);
            }
        }
    }
}