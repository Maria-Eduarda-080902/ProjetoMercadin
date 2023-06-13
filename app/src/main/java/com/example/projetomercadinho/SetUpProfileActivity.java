package com.example.projetomercadinho;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.soundcloud.android.crop.Crop;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetUpProfileActivity extends AppCompatActivity {

    private CircleImageView circleImageView;
    private EditText bio;
    private Button saveProfile;
    private Toolbar setupToolBar;
    private FirebaseAuth auth;
    private StorageReference storage;
    private String uid;
    private String downloadUri = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up_profile);

        initView();
        setPicture();
        saveProfile();
    }

    private void initView(){
        setupToolBar = findViewById(R.id.superiorSetupToolbar);
        setSupportActionBar(setupToolBar);
        getSupportActionBar().setTitle("Mercadin");
        circleImageView = findViewById(R.id.addProfilePic);
        bio = findViewById(R.id.editTextTextBio);
        saveProfile = findViewById(R.id.saveProfile);

        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance().getReference();
        uid = auth.getCurrentUser().getUid();
    }

    private void setPicture(){
        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                circleImageView.setImageDrawable(null);
                Crop.pickImage(SetUpProfileActivity.this);
            }
        });
    }

    private void saveProfile(){
        saveProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String bioString = bio.getText().toString();
                if(circleImageView.toString().contains("@drawable/user_setup_profile")){
                    Toast.makeText(SetUpProfileActivity.this, "Por favor, adcione uma foto de perfil!", Toast.LENGTH_SHORT).show();
                } else{
                    StorageReference profilePicRef = storage.child("Profile_pics").child(uid + ".jpg");
                     profilePicRef.putFile(Uri.parse(circleImageView.getTag().toString())).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                         @Override
                         public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                             if(task.isSuccessful()){
                                 updateUserProfile(task, bioString, profilePicRef);
                                 startActivity(new Intent(SetUpProfileActivity.this, MainActivity.class));
                                 finish();
                             }else{
                                 Log.w(TAG, "setProfile:failure", task.getException());
                                 Toast.makeText(SetUpProfileActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                             }
                         }
                     });
                }
            }
        });
    }

    private void updateUserProfile(Task<UploadTask.TaskSnapshot> task, String bioString, StorageReference profilePicRef) {
        profilePicRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                downloadUri = circleImageView.getTag().toString();
                FirebaseDatabase.getInstance().getReference("UserData")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("profile_pic").setValue(downloadUri);
                FirebaseDatabase.getInstance().getReference("UserData")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("bio").setValue(bioString);
                Toast.makeText(SetUpProfileActivity.this, "Sucesso!", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent result) {
        super.onActivityResult(requestCode, resultCode, result);
        if(requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK){
            beginCrop(result.getData());
        } else if (requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode,result);
        }
    }

    private void beginCrop(Uri source){
        Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
        Crop.of(source, destination).asSquare().start(SetUpProfileActivity.this);
    }

    private void handleCrop(int resultCode, Intent result){
        if(resultCode == RESULT_OK){
            circleImageView.setImageURI(Crop.getOutput(result));
            circleImageView.setTag(Crop.getOutput(result).toString());
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(SetUpProfileActivity.this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}