package com.example.instagramclone.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagramclone.Adapter.PhotographAdapter;
import com.example.instagramclone.FollowActivity;
import com.example.instagramclone.Model.Post;
import com.example.instagramclone.Model.User;
import com.example.instagramclone.OptionsActivity;
import com.example.instagramclone.OptionsProfileActivity;
import com.example.instagramclone.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


public class ProfileFragment extends Fragment {

    ImageView imageOptions , imageProfile;
    TextView txt_username , txt_posts , txt_folllowers , txt_folllowing , txt_name , txt_bio;
    Button btn_optionsProfile;
    ImageButton imgbtn_photos , imagbtn_saves;

    private List <String> saved;

    private RecyclerView saveRecyclerView;
    private List <Post> savedPhotoList;
    private PhotographAdapter savedPhotoAdapter;


    private RecyclerView recyclerView;
    private PhotographAdapter photographAdapter;
    private List <Post> mPost;


    FirebaseUser currentUser;
    String profileId;


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        SharedPreferences preferences = getContext().getSharedPreferences("PREFS" , Context.MODE_PRIVATE);

        profileId = preferences.getString("profileid" , "none");

        imageOptions = view.findViewById(R.id.options_image_profile);
        imageProfile = view.findViewById(R.id.profile_image_profile);

        txt_username = view.findViewById(R.id.txt_username_profile);
        txt_posts = view.findViewById(R.id.txt_posts_profile);
        txt_folllowers = view.findViewById(R.id.txt_followers_profile);
        txt_folllowing = view.findViewById(R.id.txt_following_profile);
        txt_name = view.findViewById(R.id.txt_name_profile);
        txt_bio = view.findViewById(R.id.txt_bio_profile);

        btn_optionsProfile = view.findViewById(R.id.btn_profile);

        imgbtn_photos = view.findViewById(R.id.imagebtn_photos_profile);
        imagbtn_saves = view.findViewById(R.id.imagebtn_savephoto_profile);

        recyclerView = view.findViewById(R.id.recyle_view_profile);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),3));
        mPost = new ArrayList<>();
        photographAdapter = new PhotographAdapter(getContext(),mPost);
        recyclerView.setAdapter(photographAdapter);

        saveRecyclerView = view.findViewById(R.id.recyle_view_save_profile);
        saveRecyclerView.setHasFixedSize(true);
        saveRecyclerView.setLayoutManager(new GridLayoutManager(getContext() , 3));
        savedPhotoList = new ArrayList<>();
        savedPhotoAdapter = new PhotographAdapter(getContext() , savedPhotoList);
        saveRecyclerView.setAdapter(savedPhotoAdapter);

        recyclerView.setVisibility(View.VISIBLE);
        saveRecyclerView.setVisibility(View.GONE);

        UserInfo();
        GetFollowers();
        GetPostNumber();
        GetPost();
        GetSavedPost();

        if (profileId.equals(currentUser.getUid()))
        {
            btn_optionsProfile.setText("Profili Duzenle");
        }
        else
        {
            ControlFollow();
            imagbtn_saves.setVisibility(View.GONE);
        }

        btn_optionsProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String btn = btn_optionsProfile.getText().toString();

                if (btn.equals("Profili Duzenle"))
                {
                    //profil duzenlemeye sayfasina gidilecek
                    Intent intent = new Intent(getContext() ,  OptionsProfileActivity.class);
                    startActivity(intent);

                }
                else if (btn.equals("Following"))
                {
                    FirebaseDatabase.getInstance().getReference().child("Follows").child(currentUser.getUid())
                            .child("following").child(profileId).setValue(true);

                    FirebaseDatabase.getInstance().getReference().child("Follows").child(profileId).child("followers")
                            .child(currentUser.getUid()).setValue(true);

                    AddNotifications();
                }
                else if (btn.equals("Follow"))
                {
                    FirebaseDatabase.getInstance().getReference().child("Follows").child(currentUser.getUid())
                            .child("following").child(profileId).removeValue();

                    FirebaseDatabase.getInstance().getReference().child("Follows").child(profileId).child("followers")
                            .child(currentUser.getUid()).removeValue();


                }
            }
        });

        imgbtn_photos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.setVisibility(View.VISIBLE);
                saveRecyclerView.setVisibility(View.GONE);
            }
        });

        imagbtn_saves.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.setVisibility(View.GONE);
                saveRecyclerView.setVisibility(View.VISIBLE);
            }
        });

        txt_folllowers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext() , FollowActivity.class);
                intent.putExtra("id",profileId);
                intent.putExtra("title" , "followers");
                startActivity(intent);
            }
        });

        txt_folllowing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext() , FollowActivity.class);
                intent.putExtra("id",profileId);
                intent.putExtra("title" , "following");
                startActivity(intent);
            }
        });

        imageOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext() , OptionsActivity.class));
            }
        });

        return view;
    }

    private void UserInfo()
    {
        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("Users").child(profileId);

        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot1) {
                if (getContext() ==  null )
                {
                    return;
                }

                User user = snapshot1.getValue(User.class);


                Glide.with(getContext()).load(user.getImageurl()).into(imageProfile);

                txt_username.setText(user.getUserName());
                txt_name.setText(user.getFullname());
                txt_bio.setText(user.getBio());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void ControlFollow()
    {
        DatabaseReference followReference = FirebaseDatabase.getInstance().getReference().child("Follows").child(currentUser.getUid())
                .child("following");

        followReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.child(profileId).exists())
                {
                    btn_optionsProfile.setText("Following");
                }
                else
                {
                    btn_optionsProfile.setText("Follow");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }

    private void GetFollowers()
    {
        //takipci sayisini alma
        DatabaseReference followersReference = FirebaseDatabase.getInstance().getReference("Follows").child(profileId)
                .child("followers");

        followersReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                txt_folllowers.setText("" + snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //takip edilenleri sayisini alma
        DatabaseReference followingReference = FirebaseDatabase.getInstance().getReference("Follows").child(profileId)
                .child("following");

        followingReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                txt_folllowing.setText("" + snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }

    private void GetPostNumber()
    {
        DatabaseReference postReference = FirebaseDatabase.getInstance().getReference("Posts");

        postReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                int i = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren() )
                {
                    Post post = dataSnapshot.getValue(Post.class);
                    if (post.getPublisher().equals(profileId))
                    {
                        i++;
                    }
                    txt_posts.setText("" +i);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }

    private void GetPost()
    {
        DatabaseReference photoReference = FirebaseDatabase.getInstance().getReference("Posts");

        photoReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mPost.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren())
                {
                    Post post = snapshot1.getValue(Post.class);
                    if (post.getPublisher().equals(profileId))
                    {
                        mPost.add(post);
                    }
                }
                Collections.reverse(mPost);
                photographAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void GetSavedPost()
    {
        saved = new ArrayList<>();

        DatabaseReference savedReference = FirebaseDatabase.getInstance().getReference("Saves").child(currentUser.getUid());

        savedReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren())
                {
                    saved.add(snapshot1.getKey());
                }
                GetSaved();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void GetSaved() {
        DatabaseReference postReference = FirebaseDatabase.getInstance().getReference("Posts");

        postReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                savedPhotoList.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren())
                {
                    Post post = snapshot1.getValue(Post.class);

                    for (String id : saved)
                    {
                        if (post.getPostId().equals(id))
                        {
                            savedPhotoList.add(post);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void AddNotifications()
    {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Notifications").child(profileId);

        HashMap<String , Object> hashMap = new HashMap<>();
        hashMap.put("userId" ,currentUser.getUid());
        hashMap.put("text" ,"seni takip etmeye basladi: ");
        hashMap.put("postId" ,"");
        hashMap.put("isPost" ,false);

        databaseReference.push().setValue(hashMap);
    }

}