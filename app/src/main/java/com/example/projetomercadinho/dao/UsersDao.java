package com.example.projetomercadinho.dao;

import android.content.Intent;

import androidx.annotation.NonNull;

import com.example.projetomercadinho.LoginActivity;
import com.example.projetomercadinho.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class UsersDao {

    private static StorageReference storage = FirebaseStorage.getInstance().getReference("Profile_pictures");
    private static DatabaseReference reference = FirebaseDatabase.getInstance().getReference("UsersData");
    private static FirebaseAuth auth = FirebaseAuth.getInstance();
    private static ArrayList<String> result = new ArrayList<String>();

    public static boolean userExists(String email){
        return true;
    }

    private static void setMessage(String message){
        result.add(message);
    }
}
