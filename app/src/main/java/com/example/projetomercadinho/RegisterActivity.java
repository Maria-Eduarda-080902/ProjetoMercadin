package com.example.projetomercadinho;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.projetomercadinho.models.UserData;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kotlin.text.Regex;

public class RegisterActivity extends AppCompatActivity {

    private EditText nameField, emailField, cpfField, passField, birthField, cepField;
    private static final int REQUEST_CODE = 100;
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
        passField = findViewById(R.id.editTextTextPasswordRegister);
        birthField = findViewById(R.id.editTextBirthdate);
        cepField = findViewById(R.id.editTextTextCEP);
        getLastLocation();
        registerBtn = findViewById(R.id.registerBtn);
        loginTxt = findViewById(R.id.textViewSignIn);
    }

    private void signUp(){

        birthField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateDialog();
            }
        });

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
                                                   startActivity(new Intent(RegisterActivity.this, SetUpProfileActivity.class));
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

    public void showDateDialog(){
        DatePickerDialog dialog = new DatePickerDialog(this, R.style.DialogTheme, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                birthField.setText(String.format("%02d", dayOfMonth)+"/"+String.format("%02d", month+1)+"/"+year);
                Log.d(TAG, String.format("%02d", dayOfMonth)+"/"+String.format("%02d", month+1)+"/"+year);
            }
        }, 2023, 0, 1);

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onShow(DialogInterface dialogI) {
                Button pButton = dialog.getButton (DialogInterface.BUTTON_POSITIVE);
                pButton.setText ("Set date");
                pButton.setBackgroundColor(R.color.beige);
            }
        });
        dialog.show();
    }


    private void getLastLocation(){
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if(location != null){
                        Geocoder geocoder = new Geocoder(RegisterActivity.this, Locale.getDefault());
                        try {
                            String currentPlaceCEP = "";
                            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            currentPlaceCEP = addresses.get(0).getPostalCode();
                            cepField.setText(currentPlaceCEP);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                    }
                }
            });
        } else{
            askPermission();
        }
    }
    private void askPermission(){
        ActivityCompat.requestPermissions(RegisterActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
        getLastLocation();
    }
}