package com.example.calorease;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ✅ Oturum kontrolü (isRemembered true ise HomeActivity'ye yönlendir)
        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        boolean isRemembered = sharedPreferences.getBoolean("isRemembered", false);

        if (isRemembered) {
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // geri dönülmesin
            startActivity(intent);
            finish();
            return;
        }

        // Layout sadece kullanıcı hatırlanmadıysa yüklensin
        setContentView(R.layout.activity_main);

        // Edge-to-Edge (tam ekran) özellikleri
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Start Button: SignIn ekranına yönlendirir
        Button startButton = findViewById(R.id.startButton);
        startButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SignIn.class);
            startActivity(intent);
        });

        // Login Button: LogIn ekranına yönlendirir
        Button loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LogIn.class);
            startActivity(intent);
        });
    }
}
