package com.example.projetomercadinho;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

public class PropostaDetailActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private DatabaseReference reference;
    private StorageReference storage;
    private String keyPost;
    private String keyProposta;
    private ImageView ofertado;
    private TextView descricao;
    private ImageView propostaPic;
    private TextView propostaDesc;
    private TextView dateField;
    private TextView timeField;
    private TextView local;
    private TextView info;
    private TextView user;
    private AppCompatButton cancel;
    private AppCompatButton save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proposta_detail);

        initView();
        setValues();
    }

    private void initView(){
        ofertado = findViewById(R.id.produtoOfertado);
        descricao = findViewById(R.id.textViewProduto);
        propostaPic = findViewById(R.id.imageProposta);
        propostaDesc = findViewById(R.id.textProposta);
        user = findViewById(R.id.userText);
        local = findViewById(R.id.editTextLocalTroca);
        dateField = findViewById(R.id.editDate);
        timeField = findViewById(R.id.editTime);
        info = findViewById(R.id.editTextInfo);
        keyPost = getIntent().getStringExtra("keyPost");
        keyProposta = getIntent().getStringExtra("keyProposta");

        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference();

        save = findViewById(R.id.saveProposta);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPropostaStatus();
            }
        });
    }

    private void setValues(){
        reference.child("PostsData").child(keyPost).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Glide.with(PropostaDetailActivity.this).load(snapshot.child("postPicture").getValue()).into(ofertado);
                descricao.setText(snapshot.child("oferta").getValue().toString());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        reference.child("PropostasData").child(keyPost).child(keyProposta).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Glide.with(PropostaDetailActivity.this).load(snapshot.child("product_image").getValue()).into(propostaPic);
                propostaDesc.setText(snapshot.child("product_description").getValue().toString());
                local.setText(snapshot.child("local").getValue().toString());
                dateField.setText(snapshot.child("date").getValue().toString());
                timeField.setText(snapshot.child("time").getValue().toString());
                info.setText(snapshot.child("info").getValue().toString());

                String userKey = snapshot.child("authorKey").getValue().toString();

                reference.child("UserData").child(userKey).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        user.setText(user.getText() + snapshot.child("name").getValue().toString());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void setPropostaStatus() {
        reference.child("PropostasData").child(keyPost).child(keyProposta).child("status").setValue("aceita");
        Intent intent = new Intent(PropostaDetailActivity.this, PropostasListActivity.class);
        intent.putExtra("keyPost", keyPost);
        startActivity(intent);
    }
}