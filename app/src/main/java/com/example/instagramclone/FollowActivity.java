package com.example.instagramclone;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagramclone.Adapter.UserAdapter;
import com.example.instagramclone.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FollowActivity extends AppCompatActivity {
    String id ;
    String title;

    private List<String> mList;

    RecyclerView recyclerView;
    UserAdapter userAdapter;
    List <User> userList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followers);

        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        title = intent.getStringExtra("title");

        //toolbar ayarlari
        Toolbar toolbar = findViewById(R.id.toolbar_followers);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //recycler ayarlari
        recyclerView = findViewById(R.id.recyle_view_followers);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        userList = new ArrayList<>();
        userAdapter = new UserAdapter(this , userList ,  false);
        recyclerView.setAdapter(userAdapter);

        mList = new ArrayList<>();

        switch (title)
        {
            case "likes":
                GetLikes();
                break;

            case "following":
                GetFollowing();
                break;

            case "followers":
                GetFollowers();
                break;
        }
    }

    private void GetLikes()
    {
        DatabaseReference likesReference = FirebaseDatabase.getInstance().getReference("Likes").child(id);

        likesReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mList.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren())
                {
                    mList.add(snapshot1.getKey());
                }
                GetUserInfo();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void GetFollowing()
    {
        DatabaseReference followingReference = FirebaseDatabase.getInstance().getReference("Follows").child(id).child("following");

        followingReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mList.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren())
                {
                    mList.add(snapshot1.getKey());
                }
                GetUserInfo();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void GetFollowers()
    {
        DatabaseReference followerReference = FirebaseDatabase.getInstance().getReference("Follows").child(id).child("followers");

        followerReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mList.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren())
                {
                    mList.add(snapshot1.getKey());
                }
                GetUserInfo();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void GetUserInfo()
    {
        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("Users");

        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren())
                {
                    User user = snapshot1.getValue(User.class);
                    for (String id : mList)
                    {
                        if (user.getId().equals(id))
                        {
                            userList.add(user);
                        }
                    }
                }
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
