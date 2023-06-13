package com.example.projetomercadinho.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.projetomercadinho.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CombinadosFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CombinadosFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_combinados, container, false);
    }
}