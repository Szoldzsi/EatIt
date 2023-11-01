package com.example.eatit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.dynamic.IFragmentWrapper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    TextView tw;
    Button regBt, logBt;
    EditText email, psw;

    DatabaseReference reference;

    FirebaseAuth mAuth;

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        tw = findViewById(R.id.textView);
        regBt = findViewById(R.id.register_button);
        logBt = findViewById(R.id.login_button);
        email = findViewById(R.id.login_email);
        psw = findViewById(R.id.login_password);

        regBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

        logBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String log_email, log_psw;
                log_email = String.valueOf(email.getText());
                log_psw = String.valueOf(psw.getText());
                if (TextUtils.isEmpty(log_email)){
                    Toast.makeText(LoginActivity.this, "Hiányzó email", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(log_psw)){
                    Toast.makeText(LoginActivity.this, "Hiányzó jelszó", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.signInWithEmailAndPassword(log_email, log_psw).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(LoginActivity.this, "Sikeres bejelentkezés", Toast.LENGTH_SHORT).show();
                            reference = FirebaseDatabase.getInstance().getReference("Users");
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        else{
                            Toast.makeText(LoginActivity.this, "Sikertelen azonosítás", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }
}