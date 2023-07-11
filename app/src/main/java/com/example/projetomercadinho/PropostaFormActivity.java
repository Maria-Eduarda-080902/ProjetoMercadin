package com.example.projetomercadinho;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.projetomercadinho.models.PropostaData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.soundcloud.android.crop.Crop;

import java.io.File;

public class PropostaFormActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private DatabaseReference reference;
    private StorageReference storage;
    private String keyPost;
    private ImageView ofertado;
    private TextView descricao;
    private ImageView propostaPic;
    private EditText propostaDesc;
    private LinearLayout datePicker;
    private EditText dateField;
    private EditText timeField;
    private EditText local;
    private EditText info;
    private AppCompatButton cancel;
    private AppCompatButton save;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_combinado_form);

        initView();
    }

    @SuppressLint("WrongViewCast")
    public void initView(){
        keyPost = getIntent().getStringExtra("key");
        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference("PropostasData");
        storage = FirebaseStorage.getInstance().getReference("propostas_pictures");

        ofertado = findViewById(R.id.produtoOfertado);
        descricao = findViewById(R.id.textViewProduto);
        propostaPic = findViewById(R.id.imageProposta);
        propostaDesc = findViewById(R.id.editTextTextOferta);
        local = findViewById(R.id.editTextLocalTroca);
        info = findViewById(R.id.editTextInfo);


        datePicker = findViewById(R.id.datePicker);
        dateField = findViewById(R.id.editDate);
        timeField = findViewById(R.id.editTime);
        info = findViewById(R.id.editTextInfo);

        cancel = findViewById(R.id.cancel_button);
        save = findViewById(R.id.saveProposta);

        FirebaseDatabase.getInstance().getReference("PostsData").child(keyPost).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                descricao.setText(snapshot.child("oferta").getValue().toString());
                Glide.with(PropostaFormActivity.this).load(snapshot.child("postPicture").getValue().toString()).into(ofertado);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        propostaPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                propostaPic.setImageDrawable(null);
                Crop.pickImage(PropostaFormActivity.this);
            }
        });

        dateField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateDialog();
            }
        });

        timeField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimeDialog();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PropostaFormActivity.this, MainActivity.class));
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProposta();
            }
        });


    }

    public void saveProposta(){
        PropostaData proposta = new PropostaData("", propostaDesc.getText().toString(), auth.getUid(), local.getText().toString(),
                dateField.getText().toString(), timeField.getText().toString(), info.getText().toString());

        reference.child(keyPost).push().setValue(proposta).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    reference.child(keyPost).orderByKey().limitToLast(1).addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                            storage.child(snapshot.getKey()).putFile(Uri.parse(propostaPic.getTag().toString())).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                    if(task.isSuccessful()){
                                        storage.child(snapshot.getKey()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                reference.child(keyPost).child(snapshot.getKey()).child("product_image").setValue(uri.toString());
                                                startActivity(new Intent(PropostaFormActivity.this, MainActivity.class));
                                            }
                                        });
                                    }
                                }
                            });
                        }
                        @Override
                        public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        }
                        @Override
                        public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                        }
                        @Override
                        public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });
    }

    public void showTimeDialog(){
        TimePickerDialog dialog = new TimePickerDialog(this, R.style.DialogTheme, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                timeField.setText(String.format("%02d", hourOfDay)+":"+String.format("%02d", minute));
            }
        }, 0,0, true);
        dialog.show();
    }

    public void showDateDialog(){
        DatePickerDialog dialog = new DatePickerDialog(this, R.style.DialogTheme, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                dateField.setText(dayOfMonth+"/"+String.format("%02d", month+1)+"/"+year);
            }
        }, 2023, 0, 1);
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent result) {
        super.onActivityResult(requestCode, resultCode, result);
        if(resultCode != RESULT_CANCELED){
            if(requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK){
                beginCrop(result.getData());
            } else if (requestCode == Crop.REQUEST_CROP) {
                handleCrop(resultCode,result);
            }
        }
    }
    private void beginCrop(Uri source){
        Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
        Crop.of(source, destination).asSquare().start(PropostaFormActivity.this);
    }
    private void handleCrop(int resultCode, Intent result){
        if(resultCode == RESULT_OK){
            ImageView postPicture = findViewById(R.id.imageProposta);
            postPicture.setImageURI(Crop.getOutput(result));
            postPicture.setTag(Crop.getOutput(result).toString());
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(PropostaFormActivity.this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }




}