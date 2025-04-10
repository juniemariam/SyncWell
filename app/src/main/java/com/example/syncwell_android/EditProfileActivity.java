package com.example.syncwell_android;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class EditProfileActivity extends AppCompatActivity {

    private EditText nameInput, ageInput, weightInput, heightInput;
    private Button saveButton;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        nameInput = findViewById(R.id.editNameInput);
        ageInput = findViewById(R.id.editAgeInput);
        weightInput = findViewById(R.id.editWeightInput);
        heightInput = findViewById(R.id.editHeightInput);
        saveButton = findViewById(R.id.saveProfileButton);

        preferences = getSharedPreferences("user_profile", MODE_PRIVATE);

        // Pre-fill with existing data
        nameInput.setText(preferences.getString("name", ""));
        ageInput.setText(preferences.getString("age", ""));
        weightInput.setText(preferences.getString("weight", ""));
        heightInput.setText(preferences.getString("height", ""));

        saveButton.setOnClickListener(v -> {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("name", nameInput.getText().toString());
            editor.putString("age", ageInput.getText().toString());
            editor.putString("weight", weightInput.getText().toString());
            editor.putString("height", heightInput.getText().toString());
            editor.apply();

            Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show();
            finish();  // Go back to ProfileFragment
        });
    }
}
