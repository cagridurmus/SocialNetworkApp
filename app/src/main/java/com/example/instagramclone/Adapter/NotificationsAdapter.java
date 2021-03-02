package com.example.instagramclone.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagramclone.Fragments.PostDetailFragment;
import com.example.instagramclone.Fragments.ProfileFragment;
import com.example.instagramclone.Model.Notifications;
import com.example.instagramclone.Model.Post;
import com.example.instagramclone.Model.User;
import com.example.instagramclone.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;


public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.NotificationsViewHolder> {

    private Context mContext;
    private List<Notifications> mList;

    public NotificationsAdapter(Context mContext, List<Notifications> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    @NonNull
    @Override
    public NotificationsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.notifications_object , parent ,false);

        return new NotificationsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final NotificationsViewHolder holder, final int position) {

        final Notifications notifications = mList.get(position);

        holder.comments.setText(notifications.getText());

        GetUserInfo(holder.profileImage , holder.username , notifications.getUserId());

        if (notifications.isIsPost())
        {
            holder.postImage.setVisibility(View.VISIBLE);
            GetComments(holder.postImage , notifications.getPostId());
        }
        else
        {
            holder.postImage.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (notifications.isIsPost())
                {
                    SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS" , Context.MODE_PRIVATE).edit();
                    editor.putString("postid" ,notifications.getPostId());
                    editor.apply();

                    ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.all_fragment , new PostDetailFragment()).commit();
                }
                else
                {
                    SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS" , Context.MODE_PRIVATE).edit();
                    editor.putString("profileid" ,notifications.getUserId());
                    editor.apply();

                    ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.all_fragment , new ProfileFragment()).commit();
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    public class NotificationsViewHolder extends RecyclerView.ViewHolder{

        public ImageView profileImage , postImage;
        public TextView username , comments ;

        public NotificationsViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.profile_image_notifications);
            postImage = itemView.findViewById(R.id.post_image_notifications);

            username = itemView.findViewById(R.id.txt_username_notifications);
            comments = itemView.findViewById(R.id.txt_comment_notifications);

        }
    }

    private void GetUserInfo(final ImageView profileImage , final TextView username , String userId){
        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("Users").child(userId);

        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
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

    private void GetComments(final ImageView postImage , String postId)
    {
        DatabaseReference postReference = FirebaseDatabase.getInstance().getReference().child("Posts").child(postId);

        postReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Post post = snapshot.getValue(Post.class);

                Glide.with(mContext).load(post.getPostImage()).into(postImage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}
