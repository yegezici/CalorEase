package com.example.calorease;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {

    private ImageView imageProfile;
    private TextView textName, textEmail;
    private Button buttonEditProfile, buttonLogout;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private LottieAnimationView lottieAnim;

    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // View'ları bağla
        imageProfile = findViewById(R.id.image_profile);
        textName = findViewById(R.id.text_name);
        textEmail = findViewById(R.id.text_email);
        buttonEditProfile = findViewById(R.id.button_edit_profile);
        buttonLogout = findViewById(R.id.button_logout);
        lottieAnim = findViewById(R.id.lottie_profile_anim);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        firestore = FirebaseFirestore.getInstance();

        if (user != null) {
            // Email göster
            textEmail.setText(user.getEmail());

            // Firestore'dan ad soyad çek
            firestore.collection("Users").document(user.getUid())
                    .get()
                    .addOnSuccessListener(doc -> {
                        if (doc.exists()) {
                            String firstName = doc.contains("firstName") ? doc.getString("firstName") : "";
                            String lastName = doc.contains("lastName") ? doc.getString("lastName") : "";
                            textName.setText(firstName + " " + lastName);
                        } else {
                            textName.setText("Ad Soyad Yok");
                        }
                    })
                    .addOnFailureListener(e -> {
                        textName.setText("Yüklenemedi");
                    });
        } else {
            textName.setText("Kullanıcı Yok");
            textEmail.setText("Email Yok");
        }

        // Tıklama animasyonu fonksiyonu
        View.OnClickListener animatedClick = view -> {
            view.animate().scaleX(0.95f).scaleY(0.95f).setDuration(80).withEndAction(() -> {
                view.animate().scaleX(1f).scaleY(1f).setDuration(80).start();
            }).start();
        };

        // Profili düzenle tıklaması
        buttonEditProfile.setOnClickListener(view -> {
            animatedClick.onClick(view);
            startActivity(new Intent(ProfileActivity.this, EditProfileActivity.class));
        });

        // Çıkış yap işlemi
        buttonLogout.setOnClickListener(view -> {
            animatedClick.onClick(view);

            auth.signOut();

            SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isRemembered", false);
            editor.apply();

            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}
