package com.example.shop.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.shop.Databases.DBHelper;
import com.example.shop.Models.Users;
import com.example.shop.R;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private EditText emailEditText, passwordEditText;
    private Button loginButton, btnBack;
    private TextView signupTextView;
    private DBHelper dbHelper;
    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "UserPrefs";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_EMAIL = "email";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        Log.d(TAG, "Layout set to R.layout.activity_login");

        // Initialisation de SharedPreferences
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        // Vérifier si l'utilisateur est déjà connecté
        if (isUserLoggedIn()) {
            Log.d(TAG, "User already logged in, redirecting to MainActivity");
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // Initialisation des vues
        try {
            emailEditText = findViewById(R.id.emailEditText);
            passwordEditText = findViewById(R.id.passwordEditText);
            loginButton = findViewById(R.id.loginButton);
            btnBack = findViewById(R.id.btnback);
            signupTextView = findViewById(R.id.signupTextView);
            dbHelper = new DBHelper(this);

            if (signupTextView == null) {
                Log.e(TAG, "signupTextView is null, check activity_login.xml for R.id.signupTextView");
                Toast.makeText(this, "Erreur de configuration de l'interface", Toast.LENGTH_LONG).show();
                return;
            }

            // Bouton de connexion
            loginButton.setOnClickListener(v -> {
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Vérification des identifiants
                Users user = dbHelper.getUserByEmail(email);
                if (user != null && user.getPassword().equals(password)) {
                    // Stocker les informations de l'utilisateur dans SharedPreferences
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt(KEY_USER_ID, user.getId());
                    editor.putString(KEY_EMAIL, user.getEmail());
                    editor.apply();
                    Log.d(TAG, "User logged in: ID=" + user.getId() + ", Email=" + user.getEmail());

                    Toast.makeText(LoginActivity.this, "Connexion réussie", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("userId", user.getId());
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Email ou mot de passe incorrect", Toast.LENGTH_SHORT).show();
                }
            });

            // Bouton de retour
            btnBack.setOnClickListener(v -> {
                Log.d(TAG, "Back button clicked");
                finish();
            });

            // Lien vers l'inscription
            signupTextView.setOnClickListener(v -> {
                Log.d(TAG, "Signup TextView clicked");
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            });
        } catch (Exception e) {
            Log.e(TAG, "Error initializing views: " + e.getMessage(), e);
            Toast.makeText(this, "Erreur lors de l'initialisation: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    // Vérifier si un utilisateur est déjà connecté
    private boolean isUserLoggedIn() {
        int userId = sharedPreferences.getInt(KEY_USER_ID, -1);
        Log.d(TAG, "Checking if user is logged in, userId=" + userId);
        return userId != -1;
    }

    // Méthode pour déconnexion (à appeler depuis une autre activité si nécessaire)
    public void logout() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        Log.d(TAG, "User logged out");
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}