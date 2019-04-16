package com.jevin.jmart.views;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.jevin.jmart.R;
import com.jevin.jmart.helpers.SharedPreferencesManager;

public class MoreActivity extends AppCompatActivity {

    TextView name, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);

        init();
        setValues();
    }

    private void init() {

        name = findViewById(R.id.txt_username);
        email = findViewById(R.id.txt_email);
    }

    private void setValues() {

        String username = SharedPreferencesManager.getUsername(this);
        name.setText(username);
    }

    public void btnLogoutClicked(View view){

        SharedPreferencesManager.deleteAll(this);

        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
