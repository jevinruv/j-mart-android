package com.jevin.jmart.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.jevin.jmart.R;
import com.jevin.jmart.forms.LoginForm;
import com.jevin.jmart.models.JwtResponse;
import com.jevin.jmart.services.APIClient;
import com.jevin.jmart.services.AuthService;
import com.jevin.jmart.services.SharedPreferencesManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText username;
    private EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();
    }

    private void init() {

        username = findViewById(R.id.input_username);
        password = findViewById(R.id.input_password);
    }

    public void btnLoginClicked(View view) {

        if (!username.getText().toString().isEmpty() && !password.getText().toString().isEmpty()) {

            LoginForm loginForm = new LoginForm(username.getText().toString(), password.getText().toString());

            AuthService authService = APIClient.getClientForAuth().create(AuthService.class);
            Call<JwtResponse> call = authService.signIn(loginForm);

            call.enqueue(new Callback<JwtResponse>() {
                @Override
                public void onResponse(Call<JwtResponse> call, Response<JwtResponse> response) {
                    setValues(response.body());
                }

                @Override
                public void onFailure(Call<JwtResponse> call, Throwable t) {

                }
            });
        }
    }

    private void setValues(JwtResponse jwtResponse){

        SharedPreferencesManager.setAuthToken(this, jwtResponse.getAccessToken());
        SharedPreferencesManager.setUserId(this, jwtResponse.getUserId());
        SharedPreferencesManager.setUsername(this, jwtResponse.getUsername());

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }
}
