package com.shariarunix.barta;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.window.OnBackAnimationCallback;
import android.window.OnBackInvokedDispatcher;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shariarunix.barta.DataModel.UserModel;

public class SignUpActivity extends AppCompatActivity {

    EditText edtSignUpName, edtSignUpEmail, edtSignUpPass;
    AppCompatButton btnSignUp;
    TextView txtSignUpSignIn, txtSignUpShowError;

    FirebaseAuth mAuth;
    FirebaseUser mUser;
    DatabaseReference mReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();
        mReference = FirebaseDatabase.getInstance().getReference();

        edtSignUpName = findViewById(R.id.edt_sign_up_name);
        edtSignUpEmail = findViewById(R.id.edt_sign_up_email);
        edtSignUpPass = findViewById(R.id.edt_sign_up_pass);

        btnSignUp = findViewById(R.id.btn_sign_up);

        txtSignUpShowError = findViewById(R.id.txt_sign_up_show_error);
        txtSignUpSignIn = findViewById(R.id.txt_sign_up_sign_in);

        txtSignUpSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = edtSignUpName.getText().toString().trim(),
                    userEmail = edtSignUpEmail.getText().toString().trim(),
                    userPass = edtSignUpPass.getText().toString().trim();

                if (isDataValidate(userName, userEmail, userPass)) {
                    if (txtSignUpShowError.getVisibility() == View.VISIBLE) {
                        Animation errorAnimation = AnimationUtils.loadAnimation(SignUpActivity.this, android.R.anim.fade_out);
                        txtSignUpShowError.startAnimation(errorAnimation);
                        txtSignUpShowError.setVisibility(View.GONE);
                    }

                    mAuth
                            .createUserWithEmailAndPassword(userEmail, userPass)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                mUser = mAuth.getCurrentUser();

                                assert mUser != null;
                                String userId = mUser.getUid();

                                mReference
                                        .child(DatabaseTableName.USER_TABLE_NAME)
                                        .child(userId)
                                        .setValue(new UserModel(userId,
                                                userName,
                                                userEmail,
                                                userPass,
                                                System.currentTimeMillis(),
                                                true))
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                                                    finish();
                                                } else {
                                                    showError("Something went wrong, Please try again", edtSignUpEmail);
                                                }
                                            }
                                        });
                            } else {
                                showError("Enter a valid email", edtSignUpEmail);
                            }

                        }
                    });

                }
            }
        });

    }

    private boolean isDataValidate(String userName, String userEmail, String userPass) {

        if (userName.isEmpty()) {
            showError("Name can't be empty", edtSignUpName);
            return false;
        }

        if (userEmail.isEmpty()) {
            showError("Email can't be empty", edtSignUpEmail);
            return false;
        }

        if (userPass.isEmpty()) {
            showError("Password can't be empty", edtSignUpPass);
            return false;
        }

        if (!userName.matches("^[a-zA-Z .]+$")) {
            showError("Special characters are not allowed", edtSignUpName);
            return false;
        }

        return true;
    }

    private void showError(String errorMessage, EditText editText){
        txtSignUpShowError.setVisibility(View.VISIBLE);
        txtSignUpShowError.setText(errorMessage);
        editText.requestFocus();
    }

}