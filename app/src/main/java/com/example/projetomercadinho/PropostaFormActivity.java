package com.example.projetomercadinho;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.soundcloud.android.crop.Crop;

import java.io.File;

public class PropostaFormActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private DatabaseReference reference;

    private String keyPost;
    private ImageView ofertado;
    private TextView descricao;
    private ImageView propostaPic;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_combinado_form);

        initView();
    }

    public void initView(){
        keyPost = getIntent().getStringExtra("key");
        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference("PostsData");
        ofertado = findViewById(R.id.produtoOfertado);
        descricao = findViewById(R.id.textViewProduto);
        propostaPic = findViewById(R.id.imageProposta);
        reference.child(keyPost).addValueEventListener(new ValueEventListener() {
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