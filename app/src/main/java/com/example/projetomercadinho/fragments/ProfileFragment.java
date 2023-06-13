package com.example.projetomercadinho.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.projetomercadinho.R;
import com.example.projetomercadinho.models.UserData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {
    private TextView profileName, profileBio;
    private CircleImageView profilePicture;
    private Toolbar toolbar;
    private String uid;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private StorageReference storage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);

    }

    @Override
    public void onStart() {
        super.onStart();
        initView();
        UserData user = getUserInformation();
    }

    private void initView(){
        toolbar = getView().findViewById(R.id.superiorProfileToolbar);
        profileName = getView().findViewById(R.id.textViewProfileName);
        profileBio = getView().findViewById(R.id.textViewProfileBio);
        profilePicture = getView().findViewById(R.id.profilePic);

        auth = FirebaseAuth.getInstance();
        uid = auth.getCurrentUser().getUid();

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("UserData/"+uid);
        storage = FirebaseStorage.getInstance().getReference();
    }

    private UserData getUserInformation(){
        final UserData user = new UserData();

//        String name = reference.child("name").toString();
//        String bio = reference.child("name").toString();
//
//        user.setBio(bio);
//        user.setName(name);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                profileName.setText(dataSnapshot.child("name").getValue().toString());
                profileBio.setText(dataSnapshot.child("bio").getValue().toString());

                StorageReference profilePicRef = storage.child("Profile_pics").child(uid + ".jpg");

                File localFile = null;
                try {
                    localFile = File.createTempFile("images", "jpg");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                File finalLocalFile = localFile;
                profilePicRef.getFile(localFile).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){
                            Uri uri = Uri.parse(finalLocalFile.getPath());
                            profilePicture.setImageURI(uri);
                        }
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle any errors
                System.out.println("Error: " + databaseError.getMessage());
            }
        });


        return user;
    }



}