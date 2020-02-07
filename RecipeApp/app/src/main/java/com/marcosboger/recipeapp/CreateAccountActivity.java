package com.marcosboger.recipeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class CreateAccountActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    TextView email_text, password_text, password_confirmation_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        email_text = findViewById(R.id.email_text);
        password_text = findViewById(R.id.password_text);
        password_confirmation_text = findViewById(R.id.password_text_confirmation);
    }

    public void onRegisterClicked(View view){
        //Check email
        //Check password
        mAuth.createUserWithEmailAndPassword(email_text.getText().toString(), password_text.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(CreateAccountActivity.this, "Authentication success.",
                                    Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(CreateAccountActivity.this, MainMenuActivity.class);
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(CreateAccountActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }
}
