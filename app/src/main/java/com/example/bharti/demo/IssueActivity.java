package com.example.bharti.demo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.kosalgeek.android.photoutil.CameraPhoto;
import com.kosalgeek.android.photoutil.GalleryPhoto;
import com.kosalgeek.android.photoutil.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.jar.*;

import static android.Manifest.*;
import static android.R.attr.data;
import static android.widget.Toast.LENGTH_SHORT;

public class IssueActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "IssueActivity";
    ImageView ivBackIssue, ivDate, ivLocation, ivGallery, ivCamera, ivDisplay;
    EditText etDate, etDescription, etLocation;
    Button bCreateIssue;
    RadioGroup rgCategory;
    RadioButton rbUnemployement, rbSafety, rbTransport, rbOther, rbTraffic;
    Calendar calendar;
    SimpleDateFormat simpledateformat;
    String Date, category, image, email;
    final int CAMERA_REQUEST=1;
    final int GALLERY_REQUEST=2;
    private static final int MY_PERMISSION_REQUEST_LOCATION = 1, CAMERA_PERMISSION_REQUEST_CODE=89789;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_issue);
        getSupportActionBar().setTitle("Create Issue");
        getSupportActionBar().setHomeButtonEnabled(true);

        etDate = (EditText) findViewById(R.id.etDate);
        etDescription = (EditText) findViewById(R.id.etDescription);
        etLocation = (EditText) findViewById(R.id.etLocation);
        bCreateIssue = (Button) findViewById(R.id.bCreateIssue);
        ivDate = (ImageView) findViewById(R.id.ivDate);
        ivCamera = (ImageView) findViewById(R.id.ivCamera);
        ivDisplay = (ImageView) findViewById(R.id.ivDisplay);
        ivLocation = (ImageView) findViewById(R.id.ivLocation);
        rgCategory = (RadioGroup) findViewById(R.id.rgCategory);
        rbSafety = (RadioButton) findViewById(R.id.rbSafety);
        rbTraffic = (RadioButton) findViewById(R.id.rbTraffic);
        rbTransport = (RadioButton) findViewById(R.id.rbTransport);
        rbUnemployement = (RadioButton) findViewById(R.id.rbUnemployment);
        rbOther = (RadioButton) findViewById(R.id.rbOther);
        calendar = Calendar.getInstance();
        simpledateformat = new SimpleDateFormat("MM/dd/yyyy");
        Date = simpledateformat.format(calendar.getTime());
        if (null == FirebaseAuth.getInstance().getCurrentUser()) {
            smartCityChat context = (smartCityChat)getApplicationContext();
            email = context.getUser().getEmailId();
        } else {
            email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        }
        Log.v(TAG, email);

        bCreateIssue.setOnClickListener(this);
        etDate.setOnClickListener(this);
        ivDate.setOnClickListener(this);
        ivLocation.setOnClickListener(this);
        ivCamera.setOnClickListener(this);
        rbOther.setOnClickListener(this);
        rbUnemployement.setOnClickListener(this);
        rbTraffic.setOnClickListener(this);
        rbSafety.setOnClickListener(this);
        rbTransport.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bCreateIssue:
                switch(rgCategory.getCheckedRadioButtonId()){
                    case R.id.rbUnemployment:
                        category = "Unemployment";
                        break;
                    case R.id.rbSafety:
                        category = "Safety";
                        break;
                    case R.id.rbTraffic:
                        category = "Traffic";
                        break;
                    case R.id.rbTransport:
                        category = "Transport";
                        break;
                    case R.id.rbOther:
                        category = "Other";
                        break;
                    default:
                        break;
                }
                Log.v(TAG, category);
                IssueActivity.AsyncT asyncT = new IssueActivity.AsyncT(this);
                asyncT.execute();
//                if(getIntent().getStringExtra("from").equals("ListIssues")){
//                    Intent intent1 = new Intent(this, ListIssues.class);
//                    startActivity(intent1);
//                }
//                else{
//                    Intent intent1 = new Intent(this, IndividualChatActivity.class);
//                    startActivity(intent1);
//                }
                break;
            case R.id.etDate:
                etDate.setText(Date);
                break;
            case R.id.ivDate:
                etDate.setText(Date);
                break;
            case R.id.ivLocation:
                setLocation();
                break;
            case R.id.ivCamera:
                Log.v(TAG, "Inside start Issue activity");
                if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(intent, CAMERA_REQUEST);
                    }
                }else{
                    String[] permissionRequest = {permission.CAMERA};
                    ActivityCompat.requestPermissions(this, permissionRequest, CAMERA_PERMISSION_REQUEST_CODE);
                }
                break;
        }
    }

    public boolean isConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }

    class AsyncT extends AsyncTask<Void,Void,String> {
        Context context;
        private AsyncT(Context context) {
            this.context = context.getApplicationContext();
        }

        @Override
        protected String doInBackground(Void... params) {
            try{
                Issue issue = new Issue();
                issue.setUser_id(email);
                issue.setCategory(category);
                issue.setDescription(etDescription.getText().toString());
                issue.setDate(etDate.getText().toString());
                issue.setLocation(etLocation.getText().toString());
                issue.setImage(image);
                issue.setStatus("Pending");

                URL url = new URL("http://ec2-54-153-112-71.us-west-1.compute.amazonaws.com:3000/api/users/addissue");
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setRequestMethod("POST"); // here you are telling that it is a POST request, which can be changed into "PUT", "GET", "DELETE" etc.
                httpURLConnection.setRequestProperty("Content-Type", "application/json"); // here you are setting the `Content-Type` for the data you are sending which is `application/json`
                httpURLConnection.connect();

                Log.v(TAG, "Connected to URL json object");

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("user_id", issue.getUser_id());
                jsonObject.put("category",issue.getCategory());
                jsonObject.put("description",issue.getDescription());
                jsonObject.put("image", issue.getImage());
                jsonObject.put("location", issue.getLocation());
                jsonObject.put("date", issue.getDate());
                jsonObject.put("status", issue.getStatus());
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

            }catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String str) {
            if(str.contains("error")){
                JSONObject jsonResponse = null;
                try {
                    jsonResponse = new JSONObject(str);
                    String error = jsonResponse.getString("error");
                    Toast.makeText(context, error, LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else{
                Toast.makeText(context, "Issue created", LENGTH_SHORT).show();
                Intent intent = new Intent(context, IndividualChatActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //intent.putExtra("emailId", email);
                context.startActivity(intent);
            }
        }
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            if(requestCode == CAMERA_REQUEST){
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                ivDisplay.setImageBitmap(bitmap);
                ivDisplay.setVisibility(View.VISIBLE);
                image = getStringImage(bitmap);
                Log.v(TAG, image);
            }
        }
    }

    public void setLocation() {
        if (ContextCompat.checkSelfPermission(IssueActivity.this,
                permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if(ActivityCompat.shouldShowRequestPermissionRationale(IssueActivity.this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)){
                ActivityCompat.requestPermissions(IssueActivity.this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_REQUEST_LOCATION);
            }else{
                ActivityCompat.requestPermissions(IssueActivity.this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_REQUEST_LOCATION);
            }
        }else{
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            try{
                etLocation.setText(currentLocation(location.getLatitude(), location.getLongitude()));
            }catch(Exception e){
                e.printStackTrace();
                Toast.makeText(this, "Not Found!", LENGTH_SHORT).show();
            }
        }
    }

    public String currentLocation(double latitude, double longitude) {
        String city = "";
        Geocoder gc = new Geocoder(IssueActivity.this, Locale.getDefault());
        List<Address> addressList;
        try {
            addressList = gc.getFromLocation(latitude, longitude, 1);
            if (addressList.size() > 0) {
                city = addressList.get(0).getLocality();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return city;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(IssueActivity.this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        try {
                            etLocation.setText(currentLocation(location.getLatitude(), location.getLongitude()));
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Not Found!", LENGTH_SHORT).show();
                        }
                    }
                }
                else {
                    Toast.makeText(this, "No permission granted!", LENGTH_SHORT).show();
                }
                break;
            case CAMERA_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(intent, CAMERA_REQUEST);
                    }
                    else
                        Toast.makeText(this, "Cannot take photos without permission", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }
}
