package com.example.projetomercadinho.fragments;

import static android.app.Activity.RESULT_OK;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projetomercadinho.MainActivity;
import com.example.projetomercadinho.R;
import com.example.projetomercadinho.adapters.PostAdapter;
import com.example.projetomercadinho.dao.PostsDao;
import com.example.projetomercadinho.models.PostItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class HomeFragment extends Fragment {

    private List<PostItem> postList;
    private RecyclerView homeFeed;
    private static PostAdapter postAdapter;

    private FirebaseAuth auth;

    private static Activity activity;


    private StorageReference storage;
    private SearchView search;


    public HomeFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        auth = FirebaseAuth.getInstance();
        View rootView =  inflater.inflate(R.layout.fragment_home, container, false);
        activity = getActivity();


        homeFeed = rootView.findViewById(R.id.homeRecyclerView);
        homeFeed.setLayoutManager(new GridLayoutManager(getActivity(), 1));

        search = rootView.findViewById(R.id.search);
        search.clearFocus();

        postList = generatePostItems();
        postAdapter = new PostAdapter(getActivity(), postList);
        homeFeed.setAdapter(postAdapter);

        prepareSearch();


        return rootView;
    }

    private List<PostItem> generatePostItems(){
        List<PostItem> postItems = new ArrayList<>();

        FirebaseDatabase.getInstance().getReference("PostsData").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    postList.clear();
                    postItems.addAll((Collection<? extends PostItem>) PostsDao.updateList(snapshot, postAdapter));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }


        });

        return postItems;

    }


    private void prepareSearch(){
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchList(newText);
                return true;
            }
        });
    }

    public void searchList(String query){
        ArrayList<PostItem> searchList = new ArrayList<PostItem>();
        for(PostItem post : postList){
            if(post.getOferta().toLowerCase().contains(query.toLowerCase())){
                searchList.add(post);
            }
        }
        postAdapter.searchDataList(searchList);
    }

    public static void showBottomDialog(String key) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("PostsData");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("PostsData");
                StorageReference storage = FirebaseStorage.getInstance().getReference("post_pictures");

                Dialog dialog = MainActivity.dialog;
                dialog.setContentView(R.layout.create_post_form_layout);

                EditText postOferta = dialog.findViewById(R.id.editTextTextOferta);
                EditText postProcura = dialog.findViewById(R.id.editTextTextProcura);
                ImageView postPicture = dialog.findViewById(R.id.addPostPic);
                postOferta.setText(dataSnapshot.child(key).child("oferta").getValue().toString());
                postProcura.setText(dataSnapshot.child(key).child("procura").getValue().toString());

                StorageReference postPicRef = storage.child(key);

                File localFile = null;
                try {
                    localFile = File.createTempFile("images", "jpg");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                File finalLocalFile = localFile;
                postPicRef.getFile(localFile).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            Uri uri = Uri.parse(finalLocalFile.getPath());
                            postPicture.setImageURI(uri);
                        }else {
                            Log.w(TAG, key);
                        }
                    }
                });


                //dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                EditText oferta = dialog.findViewById(R.id.editTextTextOferta);
                EditText procura = dialog.findViewById(R.id.editTextTextProcura);
                Button savePost = dialog.findViewById(R.id.savePost);
                Button cancel = dialog.findViewById(R.id.cancel_button);
                String userId = auth.getCurrentUser().getUid();

                postPicture.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        postPicture.setImageDrawable(null);
                        Crop.pickImage(activity);
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

                                            }
                                            @Override
                                            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                                                storage.child(snapshot.getKey())
                                                        .putFile(Uri.parse(postPicture.getTag().toString())).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                                                if(task.isSuccessful()){
                                                                    String downloadUri = postPicture.getTag().toString();
                                                                    FirebaseDatabase.getInstance().getReference("PostsData")
                                                                            .child(snapshot.getKey()).child("postPicture").setValue(downloadUri);
                                                                    Toast.makeText(activity, "Post disponível no seu feed!", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                            }
                                            @Override
                                            public void onChildRemoved(@NonNull DataSnapshot snapshot) {}
                                            @Override
                                            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}
                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                Toast.makeText(activity, error.toString(), Toast.LENGTH_SHORT).show();
                                            }

                                        });
                                    }else{
                                        Log.w(TAG, "savePost:failure", task.getException());
                                    }
                                }
                            };
                            String ofertaText = oferta.getText().toString();
                            String procuraText = procura.getText().toString();
                            reference.child(key).child("oferta").setValue(ofertaText).addOnCompleteListener(completeListener);
                            reference.child(key).child("procura").setValue(procuraText).addOnCompleteListener(completeListener);
                        dialog.dismiss();

                    }
                });

                dialog.show();
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.getWindow().getAttributes().windowAnimations = R.style.dialogAnimation;
                dialog.getWindow().setGravity(Gravity.BOTTOM);
            }
        });
    }

    public static ImageView handleLike(String key, ImageView likes){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("PostsData");
        reference.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String tag = likes.getTag().toString();
                Log.w(TAG, tag);
                if(tag.equals("unliked")){

                    reference.child(key).child("likes").setValue(Integer.parseInt(snapshot.child("likes").getValue().toString())+1);
                    likes.setTag("liked");
                    likes.setImageResource(R.drawable.baseline_thumb_up_alt_24);
                } else if (tag.equals("liked")) {
                    reference.child(key).child("likes").setValue(Integer.parseInt(snapshot.child("likes").getValue().toString())-1);
                    likes.setTag("unliked");
                    likes.setImageResource(R.drawable.baseline_thumb_up_off_alt_24);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        postAdapter.notifyDataSetChanged();
        return likes;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent result) {
        super.onActivityResult(requestCode, resultCode, result);
            if(requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK){
                beginCrop(result.getData());
            } else if (requestCode == Crop.REQUEST_CROP) {
                handleCrop(resultCode,result);
            }
    }

    private void beginCrop(Uri source){
        Uri destination = Uri.fromFile(new File(activity.getCacheDir(), "cropped"));
        Crop.of(source, destination).asSquare().start(activity);
    }

    private void handleCrop(int resultCode, Intent result){
        if(resultCode == RESULT_OK){
            ImageView postPicture = MainActivity.dialog.findViewById(R.id.addPostPic);
            postPicture.setImageURI(Crop.getOutput(result));
            postPicture.setTag(Crop.getOutput(result).toString());
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(activity, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }





}