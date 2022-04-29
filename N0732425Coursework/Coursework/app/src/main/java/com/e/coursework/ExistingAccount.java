package com.e.coursework;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ExistingAccount extends AppCompatActivity implements View.OnClickListener {
    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    public Button LoginB;
    public TextView main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_existing_account);
        mAuth = FirebaseAuth.getInstance();
        main = (TextView) findViewById(R.id.Main);
        main.setOnClickListener(this);

        if(mAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(),HomeActivity.class));
            finish();
        }

        main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ExistingAccount.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    public void existingAccount(View view) {

        progressDialog = new ProgressDialog(this);

        EditText emailExistView = findViewById(R.id.email);
        EditText passwordExistView = findViewById(R.id.password);

        String password = passwordExistView.getText().toString();
        String email = emailExistView.getText().toString();

        progressDialog.setMessage("Logging In, Please Wait...");
        progressDialog.show();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            Log.d("TAG", "signInWithEmail:success");
                            LoginB = (Button) findViewById(R.id.Login);
                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));

                        } else {
                            Log.w("TAG", "signInWithEmail:failure", task.getException());
                            Toast.makeText(ExistingAccount.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.Main:
                startActivity(new Intent(this, MainActivity.class));
                break;

        }
    }
}
