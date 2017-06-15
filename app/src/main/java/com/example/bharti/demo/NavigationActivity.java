package com.example.bharti.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import com.google.firebase.auth.FirebaseAuth;


import static android.content.ContentValues.TAG;

public class NavigationActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "NavigationActivity";

    ImageView ivDashboard, ivNewsFeed, ivIssue, ivMessenger;
    TextView tvDashboard, tvNewsFeed, tvIssueIcon, tvMessenger;
    Button bLogout;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);


        tvDashboard = (TextView) findViewById(R.id.tvDashboard);
        tvNewsFeed = (TextView) findViewById(R.id.tvNewsFeed);
        tvIssueIcon = (TextView) findViewById(R.id.tvIssueIcon);
        tvMessenger = (TextView) findViewById(R.id.tvMessenger);
        bLogout = (Button) findViewById(R.id.bLogOut);
        ivDashboard = (ImageView) findViewById(R.id.ivDashboard);
        ivNewsFeed = (ImageView) findViewById(R.id.ivNewsFeed);
        ivIssue = (ImageView) findViewById(R.id.ivIssue);
        ivMessenger = (ImageView) findViewById(R.id.ivMessenger);

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        } else {
            smartCityChat context = (smartCityChat)getApplicationContext();
            email  = context.getUser().getEmailId();
        }

        Log.v(TAG, "display name is" + email );
        tvDashboard.setOnClickListener(this);
        tvNewsFeed.setOnClickListener(this);
        tvIssueIcon.setOnClickListener(this);
        tvMessenger.setOnClickListener(this);
        bLogout.setOnClickListener(this);
        ivDashboard.setOnClickListener(this);
        ivNewsFeed.setOnClickListener(this);
        ivIssue.setOnClickListener(this);
        ivMessenger.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.tvDashboard:
                startActivity(new Intent(this, DashboardActivity.class));
                break;
            case R.id.tvNewsFeed:
                startActivity(new Intent(this, FormActivity.class));
                break;
            case R.id.tvIssueIcon:
                startActivity(new Intent(this, ListIssues.class));
                break;
            case R.id.tvMessenger:
                Log.v(TAG,"chat selected!");
                startActivity(new Intent(this, ListForums.class));
                break;
            case R.id.bLogOut:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(this, LoginActivity.class));
                break;
            case R.id.ivDashboard:
                startActivity(new Intent(this, DashboardActivity.class));
                break;
            case R.id.ivNewsFeed:
                startActivity(new Intent(this, FormActivity.class));
                break;
            case R.id.ivIssue:
                startActivity(new Intent(this, ListIssues.class));
                break;
            case R.id.ivMessenger:
                startActivity(new Intent(this, ListForums.class));
                break;
        }
    }
}