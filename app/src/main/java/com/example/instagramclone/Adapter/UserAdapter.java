package com.example.instagramclone.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagramclone.Fragments.ProfileFragment;
import com.example.instagramclone.MainPageActivity;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private Context mContext;
    private List <User> mUsers;
    private boolean isFragment;

    private FirebaseUser firebaseUser;

    public UserAdapter(Context mContext, List<User> mUsers, boolean isFragment) {
        this.mContext = mContext;
        this.mUsers = mUsers;
        this.isFragment = isFragment;
    }

    @NonNull
    @Override
    public UserAdapter.UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.user_object , parent , false);
        return new UserAdapter.UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final UserAdapter.UserViewHolder holder, final int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        final User user = mUsers.get(position);

        holder.btn_follow.setVisibility(View.VISIBLE);
        Following(user.getId() , holder.btn_follow);

        holder.userName.setText(user.getUserName());
        holder.name.setText(user.getFullname());

        Glide.with(mContext).load(user.getImageurl()).into(holder.profileImage);

        //Kullanici kendi profilinin yaninda takip et butonunu gormemesi icin
        if (user.getId().equals(firebaseUser.getUid()))
        {
            holder.btn_follow.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFragment)
                {
                    SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS" , Context.MODE_PRIVATE).edit();
                    editor.putString("profileid", user.getId());
                    editor.apply();

                    ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.all_fragment , new ProfileFragment()).commit();
                }
                else
                {
                    Intent intent = new Intent(mContext , MainPageActivity.class);
                    intent.putExtra("publisherid" , user.getId());
                    mContext.startActivity(intent);
                }
            }
        });

        holder.btn_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.btn_follow.getText().toString().equals("Follow"))
                {
                    //eger karsidaki kisiyi takip ediyorsak onu takip edilenlere ekliyor
                    FirebaseDatabase.getInstance().getReference().child("Follows").child(firebaseUser.getUid())
                            .child("following").child(user.getId()).setValue(true);

                    FirebaseDatabase.getInstance().getReference().child("Follows").child(user.getId())
                            .child("followers").child(firebaseUser.getUid()).setValue(true);

                    AddNotifications(user.getId());
                }
                else
                {
                    FirebaseDatabase.getInstance().getReference().child("Follows").child(firebaseUser.getUid())
                            .child("following").child(user.getId()).removeValue();

                    FirebaseDatabase.getInstance().getReference().child("Follows").child(user.getId())
                            .child("followers").child(firebaseUser.getUid()).removeValue();


                }
            }
        });

        
    }

    private void AddNotifications(String userId )
    {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Notifications").child(userId);

        HashMap<String , Object> hashMap = new HashMap<>();
        hashMap.put("userId" ,firebaseUser.getUid());
        hashMap.put("text" ,"seni takip etmeye basladi");
        hashMap.put("postid" ,"");
        hashMap.put("isPost" ,false);

        databaseReference.push().setValue(hashMap);
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder
    {

        public TextView userName;
        public TextView name;
        public CircleImageView profileImage;
        public Button btn_follow;

        public UserViewHolder(@NonNull View itemView) {

            super(itemView);

            userName = itemView.findViewById(R.id.txt_username_object);
            name = itemView.findViewById(R.id.txt_name_object);

            profileImage = itemView.findViewById(R.id.profile_image_object);

            btn_follow = itemView.findViewById(R.id.btn_follow_object);
        }
    }

    private void Following(final String userId , final Button button)
    {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Follows")
                .child(firebaseUser.getUid()).child("following");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(userId).exists())
                {
                    button.setText("Following");
                }
                else
                {
                    button.setText("Follow");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}
