package com.example.instagramclone.Adapter;

import android.content.Context;
import android.content.Intent;
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
import com.bumptech.glide.request.RequestOptions;
import com.example.instagramclone.CommentsActivity;
import com.example.instagramclone.FollowActivity;
import com.example.instagramclone.Fragments.ProfileFragment;
import com.example.instagramclone.Model.Post;
import com.example.instagramclone.Model.User;
import com.example.instagramclone.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private Context mContext;
    private List <Post> mPost;

    private FirebaseUser currentFirebaseUser;

    public PostAdapter(Context mContext, List<Post> mPost) {
        this.mContext = mContext;
        this.mPost = mPost;
    }

    @NonNull
    @Override
    public PostAdapter.PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.post_object , parent ,false);

        return new PostAdapter.PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final PostAdapter.PostViewHolder holder, final int position) {
        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        final Post post = mPost.get(position);

        Glide.with(mContext).load(post.getPostImage())
                .apply(new RequestOptions().placeholder(R.drawable.placeholder))
                .into(holder.postImage);

        if (post.getPostInfo().equals(""))
        {
            holder.txt_SendInfo.setVisibility(View.GONE);
        }
        else
        {
            holder.txt_SendInfo.setVisibility(View.VISIBLE);
            holder.txt_SendInfo.setText(post.getPostInfo());
        }

        InfoPost(holder.profileImage , holder.txt_Username , holder.txt_Sended , post.getPublisher());

        Liked(post.getPostId() , holder.likeImage);
        SavedPost(post.getPostId() , holder.bookmarkImage);
        NumberOfLike(holder.txt_Like , post.getPostId());
        GetComments(post.getPostId() , holder.txt_Comment);


        //username tiklandiginda
        holder.txt_Username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = mContext.getSharedPreferences( "PREFS" , MODE_PRIVATE).edit();
                editor.putString("profileid" , post.getPublisher());
                editor.apply();

                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.all_fragment , new ProfileFragment()).commit();
            }
        });

        //gonderen ismi
        holder.txt_Sended.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = mContext.getSharedPreferences( "PREFS" , MODE_PRIVATE).edit();
                editor.putString("profileid" , post.getPublisher());
                editor.apply();

                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.all_fragment , new ProfileFragment()).commit();
            }
        });

        //gonderen profil resmi
        holder.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = mContext.getSharedPreferences( "PREFS" , MODE_PRIVATE).edit();
                editor.putString("profileid" , post.getPublisher());
                editor.apply();

                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.all_fragment , new ProfileFragment()).commit();
            }
        });

        //bookmark
        holder.bookmarkImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.bookmarkImage.getTag().equals("save"))
                {
                    FirebaseDatabase.getInstance().getReference().child("Saves").child(currentFirebaseUser.getUid())
                            .child(post.getPostId()).setValue(true);
                }
                else
                {
                    FirebaseDatabase.getInstance().getReference().child("Saves").child(currentFirebaseUser.getUid())
                            .child(post.getPostId()).removeValue();
                }

            }
        });

        //like
        holder.likeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.likeImage.getTag().equals("like"))
                {
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostId())
                            .child(currentFirebaseUser.getUid()).setValue(true);
                    AddNotifications(post.getPublisher() , post.getPostId());
                }
                else
                {
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostId())
                            .child(currentFirebaseUser.getUid()).removeValue();

                    /*FirebaseDatabase.getInstance().getReference().child("Notifications").child(post.getPublisher()).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("Notifications").child(currentFirebaseUser.getUid()).removeValue();*/
                }
            }
        });

        //comments show
        holder.commentImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext , CommentsActivity.class);
                intent.putExtra("postid",post.getPostId());
                intent.putExtra("publisherid",post.getPublisher());
                mContext.startActivity(intent);
            }
        });

        //comments
        holder.txt_Comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext , CommentsActivity.class);
                intent.putExtra("postid",post.getPostId());
                intent.putExtra("publisherid",post.getPublisher());
                mContext.startActivity(intent);
            }
        });

        holder.txt_Like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext , FollowActivity.class);
                intent.putExtra("id", post.getPostId());
                intent.putExtra("title", "likes");
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPost.size();
    }

    public class  PostViewHolder  extends RecyclerView.ViewHolder
    {
        public ImageView profileImage,postImage,likeImage,commentImage,bookmarkImage;
        public TextView txt_Username,txt_Like,txt_Sended,txt_SendInfo,txt_Comment;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.profile_image_post_object);
            postImage = itemView.findViewById(R.id.send_image_post_object);
            likeImage = itemView.findViewById(R.id.like_post_object);
            commentImage = itemView.findViewById(R.id.comment_post_object);
            bookmarkImage = itemView.findViewById(R.id.bookmark_post_object);

            txt_Username = itemView.findViewById(R.id.txt_username_post_object);
            txt_Like = itemView.findViewById(R.id.txt_like_post_object);
            txt_Sended = itemView.findViewById(R.id.txt_sended_post_object);
            txt_SendInfo = itemView.findViewById(R.id.txt_sendInfo_post_object);
            txt_Comment = itemView.findViewById(R.id.txt_comment_post_object);
        }
    }

    private void AddNotifications(String userId , String postId)
    {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Notifications").child(userId);

        HashMap <String , Object> hashMap = new HashMap<>();
        hashMap.put("userId" ,currentFirebaseUser.getUid());
        hashMap.put("text" ,"gonderini begendi");
        hashMap.put("postId" ,postId);
        hashMap.put("isPost" ,true);

        databaseReference.push().setValue(hashMap);
    }
    private void GetComments(String postId , final TextView comments)
    {
        DatabaseReference commentsDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Comments").child(postId);

        commentsDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                comments.setText(snapshot.getChildrenCount() + " yorumun tumunu gor");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void Liked(final String postId , final ImageView imageView)
    {
        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Likes").child(postId);

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if (snapshot.child(currentUser.getUid()).exists())
                    {
                        imageView.setImageResource(R.drawable.ic_fav1);
                        imageView.setTag("liked");
                    }
                    else
                    {
                        imageView.setImageResource(R.drawable.ic_fav);
                        imageView.setTag("like");
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

    }

    private void NumberOfLike(final TextView likes , String postId)
    {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("Likes").child(postId);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                likes.setText(snapshot.getChildrenCount() + " likes");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void InfoPost(final ImageView profileImage , final TextView userName , final TextView publisher , final String userId)
    {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                    User user = snapshot.getValue(User.class);

                    Glide.with(mContext).load(user.getImageurl()).into(profileImage);

                    userName.setText(user.getUserName());
                    publisher.setText(user.getUserName());


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void SavedPost(final String postId , final ImageView imageView)
    {
        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference saveReference = FirebaseDatabase.getInstance().getReference().child("Saves").child(currentUser.getUid());

        saveReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(postId).exists())
                {
                    imageView.setImageResource(R.drawable.ic_saved);
                    imageView.setTag("saved");
                }
                else
                {
                    imageView.setImageResource(R.drawable.ic_save);
                    imageView.setTag("save");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
