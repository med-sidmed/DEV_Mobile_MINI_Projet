package com.example.shop.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.shop.Databases.DBHelper;
import com.example.shop.R;
 import com.example.shop.Models.Users;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {

    private LinearLayout home_page, cart_page, wishlist_page;
    private TextView userName, userEmail, userAddress, userPhone, userCity, userPostalCode, userCountry;
    private ImageView userProfileImage, go_back;
    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "UserPrefs";
    private static final String KEY_USER_ID = "userId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialisation des vues
        home_page = findViewById(R.id.home_page);
        cart_page = findViewById(R.id.cart_page);
        wishlist_page = findViewById(R.id.wishlist_page);
        userName = findViewById(R.id.userName);
        userEmail = findViewById(R.id.userEmail);
        userAddress = findViewById(R.id.userAddress);
        userPhone = findViewById(R.id.userPhone);
        userCity = findViewById(R.id.userCity);
        userPostalCode = findViewById(R.id.userPostalCode);
        userCountry = findViewById(R.id.userCountry);
        userProfileImage = findViewById(R.id.userProfileImage);
        go_back = findViewById(R.id.go_back);

        // Initialisation de SharedPreferences
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        // Récupérer l'ID de l'utilisateur depuis SharedPreferences
        int userId = sharedPreferences.getInt(KEY_USER_ID, -1);

        // Mettre à jour les informations de l'utilisateur
        if (userId != -1) {
            DBHelper dbHelper = new DBHelper(this);
            Users user = dbHelper.getUserById(userId);

            if (user != null) {
                // Mettre à jour les TextView avec les informations de l'utilisateur
                userName.setText(user.getNom() + " " + user.getPrenom());
                userEmail.setText(user.getEmail());
                userAddress.setText("Adresse: " + (user.getAdresse() != null ? user.getAdresse() : "Non définie"));
                userPhone.setText("Téléphone: " + (user.getTelephone() != null ? user.getTelephone() : "Non défini"));
                userCity.setText("Ville: " + (user.getVille() != null ? user.getVille() : "Non définie"));
                userPostalCode.setText("Code postal: " + (user.getCodePostal() != null ? user.getCodePostal() : "Non défini"));
                userCountry.setText("Pays: " + (user.getPays() != null ? user.getPays() : "Non défini"));

                // Charger l'image de profil si elle existe
                if (user.getImage() != null && !user.getImage().isEmpty()) {
                    Picasso.get().load(user.getImage()).into(userProfileImage);
                } else {
                    userProfileImage.setImageResource(R.drawable.profile_icon);
                }
            } else {
                // Gérer le cas où l'utilisateur n'est pas trouvé
                userName.setText("Utilisateur non trouvé");
                userEmail.setText("N/A");
                userAddress.setText("Adresse: Non définie");
                userPhone.setText("Téléphone: Non défini");
                userCity.setText("Ville: Non définie");
                userPostalCode.setText("Code postal: Non défini");
                userCountry.setText("Pays: Non défini");
                userProfileImage.setImageResource(R.drawable.profile_icon);
            }
        } else {
            // Gérer le cas où aucun utilisateur n'est connecté
            userName.setText("Non connecté");
            userEmail.setText("Veuillez vous connecter");
            userAddress.setText("Adresse: Non définie");
            userPhone.setText("Téléphone: Non défini");
            userCity.setText("Ville: Non définie");
            userPostalCode.setText("Code postal: Non défini");
            userCountry.setText("Pays: Non défini");
            userProfileImage.setImageResource(R.drawable.profile_icon);
        }

        // Listeners pour la navigation
        wishlist_page.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, WishlistActivity.class);
            startActivity(intent);
            finish(); // Terminer l'activité actuelle pour éviter l'empilement
        });

        cart_page.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, CardActivity.class);
            startActivity(intent);
            finish();
        });

        home_page.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        go_back.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }
}