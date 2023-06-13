package com.example.projetomercadinho.adapters;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projetomercadinho.R;
import com.example.projetomercadinho.models.PostItem;
import com.example.projetomercadinho.fragments.HomeFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private Context context;
    private List<PostItem> postLists;
    DatabaseReference reference;
    StorageReference storage;
    FirebaseAuth auth;

    public PostAdapter(Context context, List<PostItem> postLists) {
        this.context = context;
        this.postLists = postLists;
        reference = FirebaseDatabase.getInstance().getReference("PostsData");
        storage = FirebaseStorage.getInstance().getReference("post_pictures");
        auth = FirebaseAuth.getInstance();

    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        PostItem post = postLists.get(position);
        holder.pictureSpace.setImageURI(Uri.parse(post.getPostPicture()));
        holder.oferto.setText(post.getOferta());
        holder.procuro.setText(post.getProcura());
        holder.menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.deletarPostItem){
                    deletePost(position);
                } else if (item.getItemId() == R.id.editarPostItem) {
                    HomeFragment.showBottomDialog(postLists.get(position).getKey());
                }
                return true;
            }
        });

        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.like = HomeFragment.handleLike(postLists.get(position).getKey(), holder.like);
                notifyDataSetChanged();
                Log.w(TAG, holder.like.getTag().toString());
            }
        });

    }

    @Override
    public int getItemCount() {
        return postLists.size();
    }

    public void searchDataList(ArrayList<PostItem> searchList){
        postLists = searchList;
        notifyDataSetChanged();
    }

    public void deletePost(int position){
        reference.child(postLists.get(position).getKey()).removeValue();
        Toast.makeText(context, "Post deletado com sucesso!", Toast.LENGTH_SHORT).show();
    }

    public void handleLike(){

    }

    public static class PostViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        CardView postView;
        private ImageView postProfilePic;
        private TextView username;
        private ImageView pictureSpace;
        private TextView oferto;
        private TextView procuro;
        private ImageView postMenu;
        private ImageView proposta;
        private ImageView like;
        private ImageView comment;
        private PopupMenu menu;

        public PostViewHolder(@NotNull View itemView){
            super(itemView);
            postView = itemView.findViewById(R.id.postViewItem);
            postMenu = itemView.findViewById(R.id.postMenu);
            postMenu.setOnClickListener(this);
            menu = new PopupMenu(itemView.getContext(), itemView);
            postProfilePic = itemView.findViewById(R.id.postProfilePic);
            username = itemView.findViewById(R.id.postUsername);
            pictureSpace = itemView.findViewById(R.id.pictureSpace);
            oferto = itemView.findViewById(R.id.textViewOferto);
            procuro = itemView.findViewById(R.id.textViewProcuro);
            like = itemView.findViewById(R.id.likeBtn);

        }

        @Override
        public void onClick(View v) {
            showPopUpMenu(v);
        }

        private void showPopUpMenu(View view){
            menu.inflate(R.menu.post_options_menu);
            menu.show();
        }
    }

}
