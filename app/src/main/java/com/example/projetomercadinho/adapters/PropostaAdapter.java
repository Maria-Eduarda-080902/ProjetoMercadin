package com.example.projetomercadinho.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;

import com.bumptech.glide.Glide;
import com.example.projetomercadinho.PropostaDetailActivity;
import com.example.projetomercadinho.R;
import com.example.projetomercadinho.models.PropostaData;
import com.google.android.play.core.integrity.IntegrityManager;

import java.util.ArrayList;
import java.util.List;


public class PropostaAdapter extends ArrayAdapter<PropostaData> {

    private ImageView propostaPic;
    private TextView propostaDesc;
    private ImageView verMais;
    private LinearLayout statusTag;

    private TextView statusTagText;

    private ArrayList<PropostaData> propostas;

    public PropostaAdapter(@NonNull Context context, @NonNull ArrayList<PropostaData> propostas) {
        super(context, R.layout.proposta_list_item, propostas);

        this.propostas = propostas;
    }

    @SuppressLint("ResourceAsColor")
    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        PropostaData proposta = getItem(position);

        if(view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.proposta_list_item, parent,false);

        }

        propostaPic = view.findViewById(R.id.listImage);
        propostaDesc = view.findViewById(R.id.nomeOferta);
        verMais = view.findViewById(R.id.verProposta);
        statusTag = view.findViewById(R.id.statusTag);
        statusTagText = view.findViewById(R.id.statusTagText);

        if (proposta.getStatus().equals("aceita")){
            statusTag.setBackgroundTintList(view.getContext().getColorStateList(R.color.sucess));
            statusTagText.setText("Aceita");
        }

        Glide.with(view).load(proposta.getProduct_image()).into(propostaPic);
        propostaDesc.setText(proposta.getProduct_description());

        Activity act = (Activity) view.getContext();

        verMais.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String keyPost = act.getIntent().getStringExtra("keyPost");
                Intent intent = new Intent(act, PropostaDetailActivity.class);
                intent.putExtra("keyPost", keyPost);
                intent.putExtra("keyProposta", proposta.getKey());
                act.startActivity(intent);
            }
        });

        return view;

    }
}
