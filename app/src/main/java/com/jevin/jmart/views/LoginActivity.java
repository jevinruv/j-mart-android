package com.jevin.jmart.views;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.jevin.jmart.R;
import com.jevin.jmart.forms.LoginForm;
import com.jevin.jmart.helpers.APIClient;
import com.jevin.jmart.helpers.SharedPreferencesManager;
import com.jevin.jmart.models.JwtResponse;
import com.jevin.jmart.services.AuthService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();

    private EditText username;
    private EditText password;
    private TextInputLayout inputLayoutUsername, inputLayoutPassword;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();
    }

    private void init() {

        inputLayoutUsername = findViewById(R.id.input_layout_username);
        inputLayoutPassword = findViewById(R.id.input_layout_password);

        username = findViewById(R.id.input_username);
        password = findViewById(R.id.input_password);
    }

    public void txtSignupClicked(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    public void btnLoginClicked(View view) {

        if (validate()) {

            LoginForm loginForm = new LoginForm(username.getText().toString(), password.getText().toString());

            AuthService authService = APIClient.getClientForAuth().create(AuthService.class);
            Call<JwtResponse> call = authService.signIn(loginForm);

            progressDialog = new ProgressDialog(this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage(getString(R.string.msg_signing_in));
            progressDialog.setCancelable(false);
            progressDialog.show();

            call.enqueue(new Callback<JwtResponse>() {
                @Override
                public void onResponse(Call<JwtResponse> call, Response<JwtResponse> response) {

                    progressDialog.dismiss();

                    if (response.isSuccessful()) {
                        setValues(response.body());
                    } else {
                        Toast.makeText(getApplicationContext(), getString(R.string.err_msg_invalid_credentials), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<JwtResponse> call, Throwable t) {
                    Log.e(TAG, t.toString());
                }
            });
        }
    }

    private void setValues(JwtResponse jwtResponse) {

        SharedPreferencesManager.setAuthToken(this, jwtResponse.getAccessToken());
        SharedPreferencesManager.setUserId(this, jwtResponse.getUserId());
        SharedPreferencesManager.setUsername(this, jwtResponse.getUsername());
//        new CartService().initGetCart(this);

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private boolean validate() {
        return validateInput(username, inputLayoutUsername) && validateInput(password, inputLayoutPassword);
    }

    private boolean validateInput(EditText editText, TextInputLayout textInputLayout) {
        if (editText.getText().toString().trim().isEmpty()) {

            textInputLayout.setError(getString(R.string.err_msg_empty));
            requestFocus(editText);
            return false;
        } else {
            textInputLayout.setErrorEnabled(false);
        }

        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

}
