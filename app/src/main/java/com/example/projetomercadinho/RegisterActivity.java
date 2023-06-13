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

import com.example.projetomercadinho.models.UserData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private EditText nameField, emailField, cpfField, passField, birthField, cepField;
    private Button registerBtn;
    private TextView loginTxt;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setVariables();
        goToLogInOnClick();
        signUp();
    }
    private void goToLogInOnClick(){
        loginTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });
    }

    private void setVariables(){
        auth = FirebaseAuth.getInstance();
        nameField = findViewById(R.id.editTextTextName);
        emailField = findViewById(R.id.editTextTextEmailRegister);
        cpfField = findViewById(R.id.editTextTextCPF);
        passField = findViewById(R.id.editTextTextPasswordRegister);
        birthField = findViewById(R.id.editTextTextBirthdate);
        cepField = findViewById(R.id.editTextTextCEP);
        registerBtn = findViewById(R.id.registerBtn);
        loginTxt = findViewById(R.id.textViewSignIn);
    }

    private void signUp(){
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailField.getText().toString();
                String pass = passField.getText().toString();
                String name = nameField.getText().toString();
                String cpf = cpfField.getText().toString();
                String cep = cepField.getText().toString();
                String birthdate = birthField.getText().toString();


                if (email.isEmpty() || pass.isEmpty() || cpf.isEmpty() || cep.isEmpty() || name.isEmpty() || birthdate.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show();
                } else {
                   auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                       @Override
                       public void onComplete(@NonNull Task<AuthResult> task) {
                           if(task.isSuccessful()) {
                               UserData data = new UserData(name , email , pass, cpf, birthdate, cep);
                               Log.w(TAG, "TEST LOG", task.getException());
                               FirebaseDatabase.getInstance().getReference("UserData")
                                       .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(data).
                                       addOnCompleteListener(new OnCompleteListener<Void>() {
                                           @Override
                                           public void onComplete(@NonNull Task<Void> task) {
                                               if(task.isSuccessful()){
                                                   Toast.makeText(RegisterActivity.this, "Cadastro Realizado com Sucesso!",
                                                           Toast.LENGTH_SHORT).show();
                                                   startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                               }
                                               else{
                                                   Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                                   Toast.makeText(RegisterActivity.this, "Falha ao realizar cadastro",
                                                           Toast.LENGTH_SHORT).show();
                                               }
                                           }
                                       });
                           }else{
                               Toast.makeText(RegisterActivity.this, "Falha ao realizar cadastro",
                                       Toast.LENGTH_SHORT).show();
                           }
                       }
                   });
                }
            }});}
}