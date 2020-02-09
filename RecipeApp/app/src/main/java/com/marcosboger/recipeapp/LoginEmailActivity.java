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

public class LoginEmailActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private TextView email_text, password_text, wrong_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_email);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        //Get email and password reference
        email_text = findViewById(R.id.email_text);
        password_text = findViewById(R.id.password_text);
        wrong_login = findViewById(R.id.wrong_login);
    }

    public void onSignInClicked(View view){
        if(email_text.getText().toString().matches("")){
            wrong_login.setText(R.string.email_required);
            return;
        }
        if(password_text.getText().toString().matches("")){
            wrong_login.setText(R.string.password_required);
            return;
        }
        mAuth.signInWithEmailAndPassword(email_text.getText().toString(), password_text.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(LoginEmailActivity.this, "Log in successful",
                                    Toast.LENGTH_SHORT).show();
                            LogIn();
                        } else {
                            wrong_login.setText(R.string.wrong_e_mail_or_password);
                        }
                    }
                });
    }

    public void onCreateAccountClicked(View view){
        Intent intent = new Intent(this, CreateAccountActivity.class);
        intent.putExtra("email", email_text.getText().toString());
        startActivity(intent);
    }

    private void LogIn(){
        Intent intent = new Intent(LoginEmailActivity.this, MainMenuActivity.class);
        startActivity(intent);
        finish();
    }
}
