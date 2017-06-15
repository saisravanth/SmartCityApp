package com.example.bharti.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class HomeActivity extends Activity implements View.OnClickListener{

    Button bSignin, bSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_home);
//        getSupportActionBar().setHomeButtonEnabled(true);

        bSignin = (Button) findViewById(R.id.bSignin);
        bSignup = (Button) findViewById(R.id.bSignup);

        bSignin.setOnClickListener(this);
        bSignup.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.bSignin:
                startActivity(new Intent(this, LoginActivity.class));
                break;
            case R.id.bSignup:
                startActivity(new Intent(this, SignupActivity.class));
                break;
        }
    }
}
