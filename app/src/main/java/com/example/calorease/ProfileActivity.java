package com.example.calorease;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity {

    private ImageView imageProfile;
    private TextView textName, textEmail;
    private Button buttonEditProfile, buttonLogout;
    private FirebaseAuth auth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        imageProfile = findViewById(R.id.image_profile);
        textName = findViewById(R.id.text_name);
        textEmail = findViewById(R.id.text_email);
        buttonEditProfile = findViewById(R.id.button_edit_profile);
        buttonLogout = findViewById(R.id.button_logout);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        if (user != null) {
            textName.setText(user.getDisplayName() != null ? user.getDisplayName() : "Ad Yok");
            textEmail.setText(user.getEmail());
        } else {
            textName.setText("Kullanıcı Yok");
            textEmail.setText("Email Yok");
        }

        buttonEditProfile.setOnClickListener(view -> {
            // Buraya profil düzenleme ekranına geçiş kodu yazabiliriz
            // Şu anlık Toast mesajı vereyim
            // İstersen detaylı profil düzenleme yapabiliriz
          //  startActivity(new Intent(ProfileActivity.this, EditProfileActivity.class)); // ileride yapılacak
        });

        buttonLogout.setOnClickListener(view -> {
            auth.signOut();

            SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isRemembered", false); // Beni hatırla bilgisini sıfırla
            editor.apply();

            Intent intent = new Intent(ProfileActivity.this, LogIn.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

    }
}
