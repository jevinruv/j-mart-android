package com.jevin.jmart.views;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.jevin.jmart.R;
import com.jevin.jmart.helpers.APIClient;
import com.jevin.jmart.helpers.SharedPreferencesManager;
import com.jevin.jmart.models.User;
import com.jevin.jmart.services.CartService;
import com.jevin.jmart.services.UserService;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Loading. Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void init() {

        String authToken = SharedPreferencesManager.getAuthToken(this);

        if (authToken.isEmpty()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        } else {
            validateUser();
        }
    }

    private void validateUser() {

        UserService userService = APIClient.getClient().create(UserService.class);

        Call<Void> call = userService.validateToken();
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

                progressDialog.dismiss();

                if (response.isSuccessful()) {
                    Log.d(TAG, "Token Valid");

                    new CartService().initGetCart(getApplicationContext());
                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                    startActivity(intent);
                } else {
                    Log.d(TAG, "Token Invalid");

                    Toast.makeText(getApplicationContext(), getString(R.string.err_msg_token_validation_fail), Toast.LENGTH_LONG).show();

                    SharedPreferencesManager.deleteAll(getApplicationContext());

                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, t.toString());
            }
        });
    }

}
