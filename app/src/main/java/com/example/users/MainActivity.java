package com.example.users;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Иницијализација на полињата и копчето од XML дизајнот
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        Button btnLogin = findViewById(R.id.btnLogin);

        // Поврзување со Vercel серверот преку Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://classroom-presence-system-backend.vercel.app/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        // Настан при клик на копчето за најава
        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            // Проверка дали полињата се празни
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(MainActivity.this, "Пополнете ги сите полиња!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Креирање на објектот за барање со внесените податоци
            LoginRequest request = new LoginRequest(username, password);

            // Испраќање на податоците до серверот
            apiService.loginUser(request).enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        String token = response.body().getToken();
                        String userId = response.body().getUserId();

                        // Зачувување на сесијата во SharedPreferences
                        SharedPreferences sharedPref = getSharedPreferences("StudentPrefs", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("userId", userId);
                        editor.putString("token", token);
                        editor.putString("studentName", "Бојана Лазарова"); // Го зачувуваме името за следната страна
                        editor.apply();

                        Toast.makeText(MainActivity.this, "Успешна најава за Бојана!", Toast.LENGTH_LONG).show();

                        // ОТВОРАЊЕ НА СЛЕДНАТА СТРАНА: Префрлање во NfcActivity
                        Intent intent = new Intent(MainActivity.this, NfcActivity.class);
                        startActivity(intent);

                        // Уништување на MainActivity за студентот да не може да се врати назад на Login со 'Back'
                        finish();

                    } else {
                        Toast.makeText(MainActivity.this, "Неуспешна најава. Проверете ги податоците.", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                    Toast.makeText(MainActivity.this, "Проблем со мрежата: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}