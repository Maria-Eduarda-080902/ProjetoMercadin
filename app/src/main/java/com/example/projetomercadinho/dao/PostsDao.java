package com.example.projetomercadinho.dao;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.example.projetomercadinho.adapters.PostAdapter;
import com.example.projetomercadinho.models.PostItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class PostsDao {

    public static StorageReference storage;

    public static ArrayList<PostItem> updateList(@NonNull DataSnapshot snapshot, PostAdapter postAdapter) throws IOException {
        ArrayList<PostItem> postItems = new ArrayList<PostItem>();
        for(DataSnapshot snapshotItem : snapshot.getChildren()){
            PostItem post = snapshotItem.getValue(PostItem.class);
            post.setKey(snapshotItem.getKey());
            File temp = File.createTempFile("preffix-", "suffix");
            temp.deleteOnExit();

            storage = FirebaseStorage.getInstance().getReference("post_pictures/" + snapshotItem.getKey());
            storage.getFile(temp).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()){
                        Uri uri = Uri.parse(temp.getPath());
                        post.setPostPicture(uri.toString());
                    }
                }
            });

            postItems.add(0, post);
        }
        postAdapter.notifyDataSetChanged();
        return postItems;
    }
    
}
