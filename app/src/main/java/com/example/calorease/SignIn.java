package com.example.calorease;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class SignIn extends AppCompatActivity {

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;

    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(firebaseAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        auth.removeAuthStateListener(firebaseAuthStateListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        firebaseAuthStateListener = firebaseAuth -> {
            FirebaseUser user = auth.getCurrentUser();
            if (user != null) {
                // Kullanıcı oturum açmış
            } else {
                // Kullanıcı oturumu kapatmış
            }
        };

        // Date Picker Dialog
        EditText birthDate = findViewById(R.id.birthDateEditText);
        birthDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(SignIn.this,
                    (view, year1, monthOfYear, dayOfMonth) -> {
                        String selectedDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year1;
                        birthDate.setText(selectedDate);
                    }, year, month, day);
            datePickerDialog.show();
        });

        // Register Button Click Listener
        Button register = findViewById(R.id.signupButton);
        register.setOnClickListener(v -> {
            EditText email = findViewById(R.id.emailEditText);
            EditText fullName = findViewById(R.id.fullNameEditText);
            EditText password = findViewById(R.id.passwordEditText);
            EditText birthDateEdit = findViewById(R.id.birthDateEditText);

            // Yeni eklenen alanlar
            EditText heightEditText = findViewById(R.id.heightEditText);
            EditText currentWeightEditText = findViewById(R.id.currentWeightEditText);
            EditText targetWeightEditText = findViewById(R.id.targetWeightEditText);
            EditText dailyCalorieGoalEditText = findViewById(R.id.dailyCalorieGoalEditText);

            // Değerleri al
            String emailText = email.getText().toString();
            String fullNameText = fullName.getText().toString();
            String passwordText = password.getText().toString();
            String birthDateText = birthDateEdit.getText().toString();
            String heightText = heightEditText.getText().toString();
            String currentWeightText = currentWeightEditText.getText().toString();
            String targetWeightText = targetWeightEditText.getText().toString();
            String dailyCalorieGoalText = dailyCalorieGoalEditText.getText().toString();

            // Validate fields
            if (emailText.isEmpty() || fullNameText.isEmpty() || passwordText.isEmpty()
                    || birthDateText.isEmpty() || heightText.isEmpty()
                    || currentWeightText.isEmpty() || targetWeightText.isEmpty()
                    || dailyCalorieGoalText.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Lütfen tüm alanları doldurun.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Firebase Authentication to create a new user
            auth.createUserWithEmailAndPassword(emailText, passwordText)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = auth.getCurrentUser();
                            if (user != null) {
                                String userId = user.getUid();

                                // Kullanıcı nesnesini oluştur
                                User newUser = new User(
                                        userId,
                                        emailText,
                                        fullNameText.split(" ")[0], // isim
                                        fullNameText.contains(" ") ? fullNameText.substring(fullNameText.indexOf(" ") + 1) : "", // soyisim
                                        Integer.parseInt(heightText),
                                        Integer.parseInt(currentWeightText),
                                        Integer.parseInt(targetWeightText),
                                        Integer.parseInt(dailyCalorieGoalText)
                                );

                                // Kullanıcıyı veritabanına kaydet
                                database.getReference("Users").child(userId).setValue(newUser);
                            }

                            Toast.makeText(getApplicationContext(), "Kullanıcı başarıyla oluşturuldu.", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SignIn.this, HomeActivity.class));
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), "Kayıt başarısız.", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}
