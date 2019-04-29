package com.jevin.jmart.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.jevin.jmart.R;
import com.jevin.jmart.helpers.SharedPreferencesManager;
import com.jevin.jmart.services.CartService;

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
            new CartService().initGetCart(this);
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
        }
    }

}
