package com.example.newsvid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LogInCred extends AppCompatActivity {

    EditText email,passcode;

    TextView logInButton;

    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in_cred);


        email = findViewById(R.id.loged_email);
        passcode = findViewById(R.id.loged_passcode);

        logInButton = findViewById(R.id.log_in_button);

        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String em = email.getText().toString().trim();
                String ps = passcode.getText().toString().trim();
                ProgressDialog progressDialog = new ProgressDialog(LogInCred.this);
                progressDialog.setCancelable(false);
                progressDialog.setMessage("Log in..");
                progressDialog.show();

                auth = FirebaseAuth.getInstance();

                if(!em.isEmpty() && !ps.isEmpty()){
                    auth.signInWithEmailAndPassword(em, ps).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(getApplicationContext(),MainScreen.class);
                                startActivity(intent);
                                progressDialog.dismiss();
                                finish();
                            }else{
                                Toast.makeText(getApplicationContext(), "unSuccessful", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        }
                    });
                }
            }
        });


    }

    @Override
    public void onBackPressed() {
        Intent intent  = new Intent(getApplicationContext(),LogIn.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }
}