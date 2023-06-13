package com.example.projetomercadinho;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.projetomercadinho.dao.UsersDao;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private EditText userEdt, passEdt;
    private Button loginBtn;
    private TextView notRegistered;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FirebaseApp.initializeApp(this);
        initView();
        setVariableRegister();
        logIn();
    }

    private void initView(){
        userEdt = findViewById(R.id.editTextTextEmail);
        passEdt = findViewById(R.id.editTextTextPassword);
        loginBtn = findViewById(R.id.loginBtn);
        notRegistered = findViewById(R.id.textViewRegister);
        auth = FirebaseAuth.getInstance();
    }

    private void logIn(){
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = userEdt.getText().toString();
                String pass = passEdt.getText().toString();
                String message = checkLoginInformation(email, pass);
                if(message.equals("ok")){
                    auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            }else{
                                Toast.makeText(LoginActivity.this, "Login ou senha incorretos", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
               else{
                    Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void setVariableRegister(){
        notRegistered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
    }
    
    private String checkLoginInformation(String email, String pass){
        if(email.isEmpty() || pass.isEmpty()){
            return "Por favor, preencha todos os campos";
        } else if (!email.contains("@")) {
            return "Por favor, insira um email válido";
        }
        return "ok";
    }
}