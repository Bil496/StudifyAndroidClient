package com.bil496.studifyapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bil496.studifyapp.model.User;
import com.bil496.studifyapp.rest.ApiClient;
import com.bil496.studifyapp.rest.ApiInterface;
import com.bil496.studifyapp.util.SharedPref;
import com.bil496.studifyapp.util.Status;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    @BindView(R.id.input_email) EditText _usernameText;
    @BindView(R.id.input_password) EditText _passwordText;
    @BindView(R.id.btn_login) Button _loginButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Integer userId = SharedPref.read(SharedPref.USER_ID, -1);
        if(userId != -1){
            Intent intent = new Intent(getBaseContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        String username = _usernameText.getText().toString().trim();
        String password = _passwordText.getText().toString().trim();

        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);
        Call<User> call = apiService.getUser(username);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User>call, Response<User> response) {
                progressDialog.cancel();
                if (response.body() != null){
                    onLoginSuccess(response.body());
                }else{
                    onLoginFailed();
                }
            }

            @Override
            public void onFailure(Call<User>call, Throwable t) {
                // Log error here since request failed
                Log.e(TAG, t.toString());
                progressDialog.cancel();
                onLoginFailed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public void onLoginSuccess(User user) {
        _loginButton.setEnabled(true);
        SharedPref.write(SharedPref.USER_ID, user.getId());
        SharedPref.write(SharedPref.USERNAME, user.getUsername());
        Status.whenEnterTopic(getBaseContext(), user.getCurrentTopic().getId());
        Status.whenEnterTeam(LoginActivity.this, user.getCurrentTeam().getId());
        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String username = _usernameText.getText().toString().trim();
        String password = _passwordText.getText().toString().trim();

        if (username.isEmpty()) {
            _usernameText.setError("enter a valid username");
            valid = false;
        } else {
            _usernameText.setError(null);
        }

        if (password.isEmpty()) {
            _passwordText.setError("enter your password");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }
}