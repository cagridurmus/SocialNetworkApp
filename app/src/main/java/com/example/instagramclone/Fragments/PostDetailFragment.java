package com.example.instagramclone.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagramclone.Adapter.PostAdapter;
import com.example.instagramclone.Model.Post;
import com.example.instagramclone.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class PostDetailFragment extends Fragment {
    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List <Post> postList;

    String postId;

    public PostDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragments
        View view =inflater.inflate(R.layout.fragment_post_detail, container, false);

        SharedPreferences preferences = getContext().getSharedPreferences("PREFS" , Context.MODE_PRIVATE);
        postId = preferences.getString("postid", "none");

        recyclerView = view.findViewById(R.id.recycler__postDetail);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        postList = new ArrayList<>();
        postAdapter = new PostAdapter(getContext() , postList);
        recyclerView.setAdapter(postAdapter);

        ReadPost();

        return view;
    }

    private void ReadPost() {
        DatabaseReference postReference = FirebaseDatabase.getInstance().getReference("Posts").child(postId);

        postReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();

                Post post = snapshot.getValue(Post.class);
                postList.add(post);

                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}