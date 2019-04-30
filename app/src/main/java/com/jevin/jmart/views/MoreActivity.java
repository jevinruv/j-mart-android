package com.jevin.jmart.views;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jevin.jmart.R;
import com.jevin.jmart.helpers.SharedPreferencesManager;

public class MoreActivity extends AppCompatActivity {

    TextView name, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        init();
        setValues();
    }

    private void init() {

        name = findViewById(R.id.lbl_username);
        email = findViewById(R.id.lbl_email);

    }

    private void setValues() {

        String username = SharedPreferencesManager.getUsername(this);
        name.setText(username);
    }

    public void btnViewPurchaseHistoryClicked(View view){
        Intent intent = new Intent(this, PurchaseHistoryActivity.class);
        startActivity(intent);
    }

    public void btnLogoutClicked(View view){

        SharedPreferencesManager.deleteAll(this);

        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    
}
