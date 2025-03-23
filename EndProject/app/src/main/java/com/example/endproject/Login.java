package com.example.endproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends AppCompatActivity {

    private TextInputEditText usernameEditText, passwordEditText;
    private MaterialButton loginBtn, registerBtn;

    private ApiService apiService;

    // SharedPreferences keys
    private static final String PREFS_NAME = "LoginPrefs";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if user is already logged in
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);

        if (isLoggedIn) {
            // Restore user data
            User user = User.getInstance();
            user.setUsername(sharedPreferences.getString(KEY_USERNAME, ""));
            user.setPassword(sharedPreferences.getString(KEY_PASSWORD, ""));

            // Redirect to HomeActivity
            Intent intent = new Intent(Login.this, HomeActivity.class);
            startActivity(intent);
            finish(); // Close Login activity
            return;
        }

        setContentView(R.layout.activity_login);
        apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);

        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        loginBtn = findViewById(R.id.loginBtn);
        registerBtn = findViewById(R.id.registerBtn);


        // Login button handler
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(Login.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                User user = User.getInstance();
                user.setUsername(username);
                user.setPassword(password);

                Call<ApiResponse> call = apiService.login(user);

                call.enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            // Get user details and token
                            String token = response.body().getToken();

                            // Save user data in SharedPreferences
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean(KEY_IS_LOGGED_IN, true);
                            editor.putString(KEY_USERNAME, username);
                            editor.putString(KEY_PASSWORD, password);
                            editor.apply();

                            // Set details in User instance
                            user.setUsername(username);
                            user.setPassword(password);

                            Toast.makeText(Login.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Login.this, HomeActivity.class);
                            startActivity(intent);
                            finish(); // Close Login activity
                        } else {
                            Toast.makeText(Login.this, "Login Failed: " + response.message(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {
                        Toast.makeText(Login.this, "Login Failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        // Register button handler
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, Register.class);
                startActivity(intent);
            }
        });
    }
}
