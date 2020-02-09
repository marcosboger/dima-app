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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class CreateAccountActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private TextView email_text, password_text, password_confirmation_text, wrong_register, name_text;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        email_text = findViewById(R.id.email_text);
        email_text.setText(getIntent().getStringExtra("email"));
        password_text = findViewById(R.id.password_text);
        password_confirmation_text = findViewById(R.id.password_text_confirmation);
        wrong_register = findViewById(R.id.wrong_register);
        name_text = findViewById(R.id.name_text);
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public void onRegisterClicked(View view){
        if(name_text.getText().toString().matches("")){
            wrong_register.setText(R.string.name_required);
            return;
        }
        if(email_text.getText().toString().matches("")){
            wrong_register.setText(R.string.email_required);
            return;
        }
        if(password_text.getText().toString().matches("")){
            wrong_register.setText(R.string.password_required);
            return;
        }
        if(password_confirmation_text.getText().toString().matches("") ||
                !password_confirmation_text.getText().toString().matches(password_text.getText().toString())){
            wrong_register.setText(R.string.password_confirmation_error);
            return;
        }
        mAuth.createUserWithEmailAndPassword(email_text.getText().toString(), password_text.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(CreateAccountActivity.this, "Authentication success.",
                                    Toast.LENGTH_SHORT).show();
                            Map<String, Object> map = new HashMap<>();
                            map.put(mAuth.getUid(), name_text.getText().toString());
                            mDatabase.child("users").updateChildren(map);
                            Intent intent = new Intent(CreateAccountActivity.this, MainMenuActivity.class);
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            wrong_register.setText(R.string.error_creating_account);
                        }

                        // ...
                    }
                });
    }
}
