package com.jevin.jmart.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.jevin.jmart.R;
import com.jevin.jmart.helpers.APIClient;
import com.jevin.jmart.helpers.SharedPreferencesManager;
import com.jevin.jmart.models.User;
import com.jevin.jmart.services.UserService;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = ProfileActivity.class.getSimpleName();

    private EditText name, email, username, password;
    private TextInputLayout inputLayoutName, inputLayoutPassword;

    private User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.activity_profile);

        init();
        fetchUserData();
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


    private void init() {
        inputLayoutName = findViewById(R.id.input_layout_name);
        inputLayoutPassword = findViewById(R.id.input_layout_password);

        name = findViewById(R.id.input_name);
        email = findViewById(R.id.input_email);
        username = findViewById(R.id.input_username);
        password = findViewById(R.id.input_password);
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

    private void setValues() {
        name.setText(user.getName());
        username.setText(user.getUsername());
        email.setText(user.getEmail());

        email.setEnabled(false);
        username.setEnabled(false);
    }

    public void btnSaveClicked(View view) {

        if (validate()) {

            user.setName(name.getText().toString().trim());
            user.setPassword(password.getText().toString().trim());

            UserService userService = APIClient.getClient().create(UserService.class);
            Call<User> call = userService.updateUser(user);

            call.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {

                    if (response.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), getString(R.string.success_msg_updated), Toast.LENGTH_SHORT).show();
                    } else {
                        try {
                            Toast.makeText(getApplicationContext(), response.errorBody().string(), Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            Log.e(TAG, e.toString());
                        }
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Log.e(TAG, t.toString());
                }
            });
        }
    }

    public void btnDeleteAccountClicked(View view) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Are you sure you want to delete the Account?");
        alertDialogBuilder.setPositiveButton("Yes",
                (arg0, arg1) -> {
                    deleteAccount();
                });

        alertDialogBuilder.setNegativeButton("No", (dialog, which) -> finish());

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void deleteAccount() {

        UserService userService = APIClient.getClient().create(UserService.class);
        Call<Boolean> call = userService.delete(user.getId());

        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {

                if (response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), getString(R.string.success_msg_deleted_user), Toast.LENGTH_SHORT).show();

                    SharedPreferencesManager.deleteAll(getApplicationContext());

                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();

                } else {
                    try {
                        Toast.makeText(getApplicationContext(), response.errorBody().string(), Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        Log.e(TAG, e.toString());
                    }
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Log.e(TAG, t.toString());
            }
        });
    }

    private boolean validate() {
        return validateInput(name, inputLayoutName) && validateInput(password, inputLayoutPassword);
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
