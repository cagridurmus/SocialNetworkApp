package com.example.instagramclone.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagramclone.Fragments.PostDetailFragment;
import com.example.instagramclone.Model.Post;
import com.example.instagramclone.R;

import java.util.List;

public class PhotographAdapter extends RecyclerView.Adapter<PhotographAdapter.PhotographViewHolder> {

    private Context mContext;
    private List <Post> mPost;

    //private FirebaseUser currentUser;

    public PhotographAdapter(Context mContext, List <Post> mPost) {
        this.mContext = mContext;
        this.mPost = mPost;
    }

    @NonNull
    @Override
    public PhotographViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.photograph_object, parent , false);

        return new PhotographAdapter.PhotographViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotographViewHolder holder, int position) {
        final Post post = mPost.get(position);

        Glide.with(mContext).load(post.getPostImage()).into(holder.postImage);

        holder.postImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = mContext.getSharedPreferences( "PREFS" , Context.MODE_PRIVATE).edit();
                editor.putString("postid" , post.getPostId());
                editor.apply();

                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.all_fragment , new PostDetailFragment()).commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPost.size();
    }

    public class PhotographViewHolder extends RecyclerView.ViewHolder{

        public ImageView postImage;

        public PhotographViewHolder(@NonNull View itemView) {
            super(itemView);

            postImage = itemView.findViewById(R.id.post_image_Photo);
        }
    }
}
