package com.jevin.jmart.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.jevin.jmart.R;
import com.jevin.jmart.forms.LoginForm;
import com.jevin.jmart.models.JwtResponse;
import com.jevin.jmart.helpers.APIClient;
import com.jevin.jmart.services.AuthService;
import com.jevin.jmart.helpers.SharedPreferencesManager;
import com.jevin.jmart.services.CartService;

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

    public void txtSignupClicked(View view){
//        Intent intent = new Intent(this, MainActivity.class);
//        startActivity(intent);
    }

    public void btnLoginClicked(View view) {

        if (!username.getText().toString().isEmpty() && !password.getText().toString().isEmpty()) {

            LoginForm loginForm = new LoginForm(username.getText().toString(), password.getText().toString());

            AuthService authService = APIClient.getClientForAuth().create(AuthService.class);
            Call<JwtResponse> call = authService.signIn(loginForm);

            call.enqueue(new Callback<JwtResponse>() {
                @Override
                public void onResponse(Call<JwtResponse> call, Response<JwtResponse> response) {

                    if(response.isSuccessful()){
                        setValues(response.body());
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "Invalid Credentials!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<JwtResponse> call, Throwable t) {

                }
            });
        }
    }

    private void setValues(JwtResponse jwtResponse) {

        SharedPreferencesManager.setAuthToken(this, jwtResponse.getAccessToken());
        SharedPreferencesManager.setUserId(this, jwtResponse.getUserId());
        SharedPreferencesManager.setUsername(this, jwtResponse.getUsername());
        new CartService().initGetCart(this);

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
