package com.example.instagramclone.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagramclone.MainPageActivity;
import com.example.instagramclone.Model.Comment;
import com.example.instagramclone.Model.User;
import com.example.instagramclone.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ImageViewHolder>{
    private Context mContext;
    private List<Comment> mComment;

    public CommentAdapter(Context mContext, List<Comment> mComment) {
        this.mContext = mContext;
        this.mComment = mComment;
    }
    @NonNull
    @Override
    public CommentAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.comment_object , parent ,false);

        return new CommentAdapter.ImageViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull final CommentAdapter.ImageViewHolder holder, int position) {
        final Comment comment = mComment.get(position);
        holder.comments.setText(comment.getComment());
        GetUserInfo(holder.profileimage , holder.userName , comment.getPublisher());
        holder.userName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext , MainPageActivity.class);
                intent.putExtra("publisherid",comment.getPublisher());
                mContext.startActivity(intent);
            }
        });

        holder.profileimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext , MainPageActivity.class);
                intent.putExtra("publisherid",comment.getPublisher());
                mContext.startActivity(intent);

            }
        });
    }
    @Override
    public int getItemCount() {
        return mComment.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder
    {
        public ImageView profileimage;
        public TextView userName , comments;

        public ImageViewHolder(View itemView) {
            super(itemView);

            profileimage = itemView.findViewById(R.id.profile_image_comment_object);
            userName = itemView.findViewById(R.id.txt_username_comments_object);
            comments = itemView.findViewById(R.id.txt_comment_comment_object);
        }
    }
    private void GetUserInfo(final ImageView profileImage , final TextView username , String publisherid)
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(publisherid);


        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);


                Glide.with(mContext).load(user.getImageurl()).into(profileImage);


                username.setText(user.getUserName());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}
