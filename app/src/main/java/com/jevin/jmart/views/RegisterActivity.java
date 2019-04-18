package com.jevin.jmart.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.jevin.jmart.R;
import com.jevin.jmart.fragments.CartFragment;
import com.jevin.jmart.helpers.APIClient;
import com.jevin.jmart.models.User;
import com.jevin.jmart.services.AuthService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = RegisterActivity.class.getSimpleName();

    private EditText name, email, username, password;
    private TextInputLayout inputLayoutName, inputLayoutEmail, inputLayoutUsername, inputLayoutPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        init();
    }

    private void init() {

        inputLayoutName = findViewById(R.id.input_layout_name);
        inputLayoutEmail = findViewById(R.id.input_layout_email);
        inputLayoutUsername = findViewById(R.id.input_layout_username);
        inputLayoutPassword = findViewById(R.id.input_layout_password);

        name = findViewById(R.id.input_name);
        email = findViewById(R.id.input_email);
        username = findViewById(R.id.input_username);
        password = findViewById(R.id.input_password);
    }

    public void txtSigninClicked(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void btnRegisterClicked(View view) {

        if (validate()) {

            User user = new User();
            user.setName(name.getText().toString().trim());
            user.setEmail(email.getText().toString().trim());
            user.setUsername(username.getText().toString().trim());
            user.setPassword(password.getText().toString().trim());

            AuthService authService = APIClient.getClientForAuth().create(AuthService.class);
            Call<JSONObject> call = authService.signUp(user);

            call.enqueue(new Callback<JSONObject>() {
                @Override
                public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {

                    if (response.isSuccessful()) {
                        try {
                            Toast.makeText(getApplicationContext(), response.body().getString("message"), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            Log.e(TAG, e.toString());
                        }

                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                    } else {
                        try {
                            Toast.makeText(getApplicationContext(), response.errorBody().string(), Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            Log.e(TAG, e.toString());
                        }
                    }
                }

                @Override
                public void onFailure(Call<JSONObject> call, Throwable t) {
                    Log.e(TAG, t.toString());
                }
            });
        }
    }

    private boolean validate() {
        return validateInput(name, inputLayoutName) &&
                validateEmail() &&
                validateInput(username, inputLayoutUsername) &&
                validateInput(password, inputLayoutPassword);
    }

    private boolean validateEmail() {
        String strEmail = email.getText().toString().trim();

        if (strEmail.isEmpty() || !isValidEmail(strEmail)) {
            inputLayoutEmail.setError(getString(R.string.err_msg_invalid));
            requestFocus(email);
            return false;
        } else {
            inputLayoutEmail.setErrorEnabled(false);
        }

        return true;
    }

    private boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
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
