package com.jevin.jmart.views;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.jevin.jmart.R;
import com.jevin.jmart.helpers.APIClient;
import com.jevin.jmart.helpers.SharedPreferencesManager;
import com.jevin.jmart.models.User;
import com.jevin.jmart.services.UserService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MoreActivity extends AppCompatActivity {

    private static final String TAG = MoreActivity.class.getSimpleName();

    private TextView name, email;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        init();
        fetchUserData();
    }

    private void fetchUserData() {

        int userId = SharedPreferencesManager.getUserId(this);
        UserService userService = APIClient.getClient().create(UserService.class);

        Call<User> call = userService.get(userId);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                user = response.body();
                Log.d(TAG, "Number of user received: " + user);
                setValues();
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e(TAG, t.toString());
            }
        });
    }

    private void init() {

        name = findViewById(R.id.lbl_name);
        email = findViewById(R.id.lbl_email);
    }


    private void setValues() {
        name.setText(user.getName());
        email.setText(user.getEmail());
    }

    public void btnViewPurchaseHistoryClicked(View view) {
        Intent intent = new Intent(this, PurchaseHistoryActivity.class);
        startActivity(intent);
    }

    public void btnLogoutClicked(View view) {

        SharedPreferencesManager.deleteAll(this);

        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    public void btnEditProfileClicked(View view) {

        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
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
