package com.example.projetomercadinho;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projetomercadinho.adapters.PropostaAdapter;
import com.example.projetomercadinho.databinding.ActivityMainBinding;
import com.example.projetomercadinho.models.PropostaData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;

public class PropostasListActivity extends AppCompatActivity {

    DatabaseReference reference;
    private ActivityMainBinding binding;
    private PropostaAdapter adapter;
    private ArrayList<PropostaData> propostas = new ArrayList<PropostaData>();
    private PropostaData data;
    private String keyPost;

    private ListView listView;
    private ImageView goBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_propostas_list);

        reference = FirebaseDatabase.getInstance().getReference("PropostasData");

        listView = findViewById(R.id.propostasList);
        goBack = findViewById(R.id.goBack);

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PropostasListActivity.this, MainActivity.class));
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        keyPost = getIntent().getStringExtra("keyPost");
        FirebaseDatabase.getInstance().getReference("PostsData").child(keyPost).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String auth = FirebaseAuth.getInstance().getUid();
                if(!snapshot.child("userId").getValue().toString().equals(auth)){
                    Toast.makeText(PropostasListActivity.this, "Este post não pertence a você", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(PropostasListActivity.this, MainActivity.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        generateItems(keyPost);
        adapter = new PropostaAdapter(this, propostas);
        listView.setAdapter(adapter);
    }

    private void generateItems(String keyPost){
        reference.child(keyPost).orderByKey().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                propostas.clear();
                for(DataSnapshot snapshotItem : snapshot.getChildren()){
                    PropostaData proposta = snapshotItem.getValue(PropostaData.class);
                    proposta.setKey(snapshotItem.getKey());
                    addProposta(proposta);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addProposta(PropostaData proposta){
        propostas.add(0, proposta);
    }
}