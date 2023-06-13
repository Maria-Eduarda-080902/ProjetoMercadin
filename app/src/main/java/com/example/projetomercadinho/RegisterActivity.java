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

import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kotlin.text.Regex;

public class RegisterActivity extends AppCompatActivity {

    private EditText nameField, emailField, cpfField, passField, birthField, cepField;
    private Button registerBtn;
    private TextView loginTxt;
    private FirebaseAuth auth;

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    public static final Pattern VALID_BRAZILIAN_DATE_REGEX =
            Pattern.compile("^(0[1-9]|[12][0-9]|3[01])\\/(0[1-9]|1[012])\\/((19|2[0-9])[0-9]{2})$", Pattern.CASE_INSENSITIVE);



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
        String mask = new
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

                String message = checkInformation();

                if (message.equals("ok")) {
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
                               Toast.makeText(RegisterActivity.this, "Falha ao realizar cadastro", Toast.LENGTH_SHORT).show();
                           }
                       }
                   });
                } else{
                    Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            }});
    }

    private String checkInformation(){
        String email = emailField.getText().toString();
        String pass = passField.getText().toString();
        String name = nameField.getText().toString();
        String cpf = cpfField.getText().toString();
        String cep = cepField.getText().toString();
        String birthdate = birthField.getText().toString();


        if(email.isEmpty() || pass.isEmpty() || name.isEmpty() || cpf.isEmpty() || cep.isEmpty() || birthdate.isEmpty()){
            return "Por favor, preencha todos os campos";
        } else if(!validateEmail(email)){
            return "por favor, insira um email válido";
        } else if (pass.length()<8) {
            return "Sua senha deve possuir pelo menos 8 caracteres";
        } else if (!UserData.isCPF(cpf)) {
            return "Por favor, insira um CPF válido";
        } else if (!validateDate(birthdate)){
            return "Por favor, insira uma data de nascimento válida";
        }

        return "ok";
    }

    public static boolean validateEmail(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.matches();
    }
    public static boolean validateDate(String dateStr) {
        Matcher matcher = VALID_BRAZILIAN_DATE_REGEX.matcher(dateStr);
        return matcher.matches();
    }
}