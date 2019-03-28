package com.jevin.jmart.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.jevin.jmart.R;
import com.jevin.jmart.fragments.CartFragment;
import com.jevin.jmart.fragments.CategoryFragment;
import com.jevin.jmart.fragments.HomeFragment;
import com.jevin.jmart.services.SharedPreferencesManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init() {

        String authToken = SharedPreferencesManager.getAuthToken(this);

        if (authToken.isEmpty()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
        else{
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
        }
    }

}
