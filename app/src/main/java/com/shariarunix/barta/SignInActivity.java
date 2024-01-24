package com.shariarunix.barta;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignInActivity extends AppCompatActivity {

    AppCompatButton btnSignIn;
    EditText edtSignInEmail, edtSignInPass;
    TextView txtSignInCreateAc, txtSignInShowError;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mAuth = FirebaseAuth.getInstance();

        btnSignIn = findViewById(R.id.btn_sign_in);

        edtSignInEmail = findViewById(R.id.edt_sign_in_email);
        edtSignInPass = findViewById(R.id.edt_sign_in_pass);

        txtSignInCreateAc = findViewById(R.id.txt_sign_in_create_ac);
        txtSignInShowError = findViewById(R.id.txt_sign_in_show_error);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userEmail = edtSignInEmail.getText().toString().trim();
                String userPass = edtSignInPass.getText().toString().trim();

                if (isValidate(userEmail, userPass)) {
                    mAuth
                            .signInWithEmailAndPassword(userEmail, userPass)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        startActivity(new Intent(SignInActivity.this, MainActivity.class));
                                        finish();
                                    } else {
                                        txtSignInShowError.setVisibility(View.VISIBLE);
                                        txtSignInShowError.setText("Enter valid email and password");
                                    }
                                }
                            });
                }
            }
        });

        txtSignInCreateAc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
            }
        });
    }

    private boolean isValidate(String userEmail, String userPass) {

        if (userEmail.isEmpty()) {
            txtSignInShowError.setVisibility(View.VISIBLE);
            txtSignInShowError.setText("Email can't be empty");
            edtSignInEmail.requestFocus();
            return false;
        }
        if (userPass.isEmpty()) {
            txtSignInShowError.setVisibility(View.VISIBLE);
            txtSignInShowError.setText("Password can't be empty");
            edtSignInPass.requestFocus();
            return false;
        }
        if (!userEmail.matches("^[a-z0-9](\\.?[a-z0-9]){5,}@gmail\\.com$")) {
            txtSignInShowError.setVisibility(View.VISIBLE);
            txtSignInShowError.setText("Enter valid email");
            edtSignInEmail.requestFocus();
            return false;
        }
        if (userPass.length() < 9) {
            txtSignInShowError.setVisibility(View.VISIBLE);
            txtSignInShowError.setText("Password must be contains 8 characters");
            edtSignInPass.requestFocus();
            return false;
        }

        return true;
    }
}