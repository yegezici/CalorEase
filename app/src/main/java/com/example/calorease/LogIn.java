package com.example.calorease;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LogIn extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText emailEditText, passwordEditText;
    private MaterialButton loginButton;
    private CheckBox rememberMeCheckBox;
    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Tam ekran özelliklerini ViewCompat ile uygula
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Firebase başlat
        auth = FirebaseAuth.getInstance();

        // View'ları bağla
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        // Checkbox ve SharedPreferences'ı bağla
        rememberMeCheckBox = findViewById(R.id.checkbox_remember_me);
        sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);

// Uygulama açılır açılmaz kontrol et
        boolean isRemembered = sharedPreferences.getBoolean("isRemembered", false);

        if (isRemembered) {
            startActivity(new Intent(LogIn.this, HomeActivity.class));
            finish(); // Login ekranını kapat
        }

        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(LogIn.this, "Lütfen tüm alanları doldurun!", Toast.LENGTH_SHORT).show();
            } else {
                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {

                                // Beni Hatırla seçildiyse kaydet
                                if (rememberMeCheckBox.isChecked()) {
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putBoolean("isRemembered", true);
                                    editor.apply();
                                }

                                startActivity(new Intent(LogIn.this, HomeActivity.class));
                                finish();
                            } else {
                                Toast.makeText(LogIn.this, "Giriş başarısız: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

    }
}
