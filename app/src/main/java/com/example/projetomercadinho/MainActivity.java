package com.example.projetomercadinho;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.projetomercadinho.models.PostItem;
import com.example.projetomercadinho.databinding.ActivityMainBinding;
import com.example.projetomercadinho.fragments.ChatFragment;
import com.example.projetomercadinho.fragments.CombinadosFragment;
import com.example.projetomercadinho.fragments.HomeFragment;
import com.example.projetomercadinho.fragments.ProfileFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private StorageReference storage;
    private BottomNavigationView bottomNavigation;
    public static Dialog dialog;
    private Toolbar toolbar;
    private FloatingActionButton fab;

    private static Activity mainActivity;
    private HomeFragment home = new HomeFragment();
    private CombinadosFragment combinados = new CombinadosFragment();
    private ChatFragment chat = new ChatFragment();
    private ProfileFragment profile = new ProfileFragment();
    private ActivityMainBinding binding;

    private ImageView postPicture;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mainActivity = MainActivity.this;
        dialog = new Dialog(MainActivity.this);

        auth = FirebaseAuth.getInstance();
        initView();
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Mercadin");
        setUpBottomNavigation();
        showCreatePostForm();
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = auth.getCurrentUser();
        if(currentUser == null){
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        } else{
            Toast.makeText(MainActivity.this, "Bem-vindo!", Toast.LENGTH_SHORT).show();
        }
    }

    private void initView(){
        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.bringToFront();
        toolbar = findViewById(R.id.superiorToolbar);
        fab = findViewById(R.id.addPost);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("PostsData/");
        storage = FirebaseStorage.getInstance().getReference();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.logoutItem){
            auth.signOut();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        } else if (item.getItemId() == R.id.profileItem) {
            startActivity(new Intent(MainActivity.this, SetUpProfileActivity.class));
            finish();
        }
        return true;
    }

    private void setUpBottomNavigation(){
        replaceFragment(home);
        binding.bottomNavigation.setBackground(null);
        bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                if(item.getItemId() == R.id.home){
                    replaceFragment(home);
                }
                else if(item.getItemId() == R.id.combinados){
                    replaceFragment(combinados);
                }
                else if(item.getItemId() == R.id.chat){
                    replaceFragment(chat);
                }
                else if(item.getItemId() == R.id.profile){
                    replaceFragment(profile);
                }
                return true;
            }
        });
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.frame_layout, fragment);
        transaction.commit();
    }

    private void showCreatePostForm(){
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomDialog("");
            }
        });
    }

    public static void showBottomDialog(String key){

        //dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.create_post_form_layout);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("PostsData");
        StorageReference storage = FirebaseStorage.getInstance().getReference("post_pictures");

        ImageView postPicture = dialog.findViewById(R.id.addPostPic);

        EditText oferta = dialog.findViewById(R.id.editTextTextOferta);
        EditText procura = dialog.findViewById(R.id.editTextTextProcura);
        Button savePost = dialog.findViewById(R.id.savePost);
        Button cancel = dialog.findViewById(R.id.cancel_button);
        String userId = auth.getCurrentUser().getUid();

        postPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postPicture.setImageDrawable(null);
                Crop.pickImage(mainActivity);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        savePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnCompleteListener completeListener = new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            reference.orderByKey().limitToLast(1).addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                                    storage.child(snapshot.getKey())
                                            .putFile(Uri.parse(postPicture.getTag().toString())).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                                    if(task.isSuccessful()){
                                                        String downloadUri = postPicture.getTag().toString();
                                                        FirebaseDatabase.getInstance().getReference("PostsData")
                                                                .child(snapshot.getKey()).child("postPicture").setValue(downloadUri);
                                                        Toast.makeText(mainActivity, "Post disponível no seu feed!", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                }

                                @Override
                                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                                }
                                @Override
                                public void onChildRemoved(@NonNull DataSnapshot snapshot) {}
                                @Override
                                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(mainActivity, error.toString(), Toast.LENGTH_SHORT).show();
                                }

                            });
                        }else{
                            Log.w(TAG, "savePost:failure", task.getException());
                        }
                    }
                };
                String ofertaText = oferta.getText().toString();
                String procuraText = procura.getText().toString();
                PostItem post = new PostItem(userId, new Date(), "", ofertaText, procuraText, 0);
                reference.push().setValue(post).addOnCompleteListener(completeListener);
                dialog.dismiss();
                }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
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
        Crop.of(source, destination).asSquare().start(MainActivity.this);
    }

    private void handleCrop(int resultCode, Intent result){
        if(resultCode == RESULT_OK){
            ImageView postPicture = dialog.findViewById(R.id.addPostPic);
            postPicture.setImageURI(Crop.getOutput(result));
            postPicture.setTag(Crop.getOutput(result).toString());
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(MainActivity.this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


}