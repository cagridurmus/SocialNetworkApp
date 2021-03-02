package com.example.instagramclone.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagramclone.Adapter.PostAdapter;
import com.example.instagramclone.Adapter.StoryAdapter;
import com.example.instagramclone.Model.Post;
import com.example.instagramclone.Model.Story;
import com.example.instagramclone.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {
    //gonderi
    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post> postList;

    //hikaye
    private RecyclerView storyRecyclerView;
    private StoryAdapter storyAdapter;
    private List<Story> storyList;

    private List<String> followList;

    ProgressBar progressBar;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        View view = inflater.inflate(R.layout.fragment_home, container, false);
        //story
        storyRecyclerView = view.findViewById(R.id.recyler_view_story_Home);
        storyRecyclerView.setHasFixedSize(true);
        LinearLayoutManager storyLinearLayoutManager = new LinearLayoutManager(getContext() , LinearLayoutManager.HORIZONTAL , false);
        storyLinearLayoutManager.setReverseLayout(true);
        storyLinearLayoutManager.setStackFromEnd(true);
        storyRecyclerView.setLayoutManager(storyLinearLayoutManager);
        storyList = new ArrayList<>();
        storyAdapter = new StoryAdapter(getContext() , storyList);
        storyRecyclerView.setAdapter(storyAdapter);

        //gonderi
        recyclerView = view.findViewById(R.id.recyler_view_Home);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        postList = new ArrayList<>();
        postAdapter = new PostAdapter(getContext() , postList);
        recyclerView.setAdapter(postAdapter);

        progressBar = view.findViewById(R.id.progress_home);

        ControlFollow();

        return view;
    }

    private void ControlFollow()
    {
        followList = new ArrayList<>();

        DatabaseReference followReference = FirebaseDatabase.getInstance().getReference("Follows")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("following");
        followReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    followList.add(dataSnapshot.getKey());
                }

                ReadPost();
                ReadStory();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void ReadPost()
    {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Posts");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    Post post = dataSnapshot.getValue(Post.class);
                    for (String id : followList)
                    {
                        if (post.getPublisher().equals(id))
                        {
                            postList.add(post);
                        }
                    }
                }
                postAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void ReadStory()
    {
        DatabaseReference storyReference = FirebaseDatabase.getInstance().getReference("Story");

        storyReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long currentTime = System.currentTimeMillis();
                storyList.clear();
                storyList.add(new Story("" , "", FirebaseAuth.getInstance().getCurrentUser().getUid() , 0, 0));
                for (String id : followList)
                {
                    int storyCounter = 0;

                    Story story = null;

                    for (DataSnapshot snapshot1 : snapshot.child(id).getChildren())
                    {
                        story = snapshot1.getValue(Story.class);
                        if (currentTime > story.getStartdate() && currentTime < story.getEnddate())
                        {
                            storyCounter++;
                        }
                    }
                    if (storyCounter > 0)
                    {
                        storyList.add(story);
                    }
                }
                storyAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}