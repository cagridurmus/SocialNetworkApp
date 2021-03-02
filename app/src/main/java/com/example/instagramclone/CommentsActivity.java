package com.example.instagramclone;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagramclone.Adapter.CommentAdapter;
import com.example.instagramclone.Model.Comment;
import com.example.instagramclone.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CommentsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private CommentAdapter commentAdapter;
    private List<Comment> commentsList;

    ImageView profileImage;
    EditText addComments;
    TextView txt_Send;

    String postId;
    String publisherId;

    FirebaseUser currentFirebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        Toolbar toolbar = findViewById(R.id.toolbar_Comments);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Comments");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent intent = getIntent();
        postId = intent.getStringExtra("postid");
        publisherId = intent.getStringExtra("publisherid");

        recyclerView = findViewById(R.id.recyler_view_Comments);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        commentsList = new ArrayList<>();
        commentAdapter = new CommentAdapter(this , commentsList );
        recyclerView.setAdapter(commentAdapter);

        profileImage = findViewById(R.id.profile_image_Comments);
        addComments = findViewById(R.id.edt_addComment_comments);
        txt_Send = findViewById(R.id.txt_Send_comments);

        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        txt_Send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addComments.getText().toString().equals(""))
                {
                    Toast.makeText(CommentsActivity.this, "Bos yorum gonderemezsiniz...", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    AddComments();
                }
            }
        });
        GetPicture();

        ReadComments();
    }

    private void AddComments() {
        DatabaseReference commentsReference = FirebaseDatabase.getInstance().getReference("Comments").child(postId);

        String commentId = commentsReference.push().getKey();

        HashMap <String,Object> hashMap = new HashMap<>();
        hashMap.put("comment",addComments.getText().toString());
        hashMap.put("publisher",currentFirebaseUser.getUid());
        hashMap.put("commentId",commentId);

        commentsReference.child(commentId).setValue(hashMap);
        AddNotifications();
        addComments.setText("");
    }
    private void GetPicture()
    {
        DatabaseReference imageReference = FirebaseDatabase.getInstance().getReference("Users").child(currentFirebaseUser.getUid());

        imageReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

                Glide.with(getApplicationContext()).load(user.getImageurl()).into(profileImage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void ReadComments()
    {
        DatabaseReference commentReference = FirebaseDatabase.getInstance().getReference("Comments").child(postId);

        commentReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                commentsList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    Comment comment = dataSnapshot.getValue(Comment.class);
                    commentsList.add(comment);
                }
                commentAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void AddNotifications()
    {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Notifications").child(publisherId);

        HashMap<String , Object> hashMap = new HashMap<>();
        hashMap.put("userId" ,currentFirebaseUser.getUid());
        hashMap.put("text" ,"fotografina yorum yapti: " + addComments.getText().toString());
        hashMap.put("postId" , postId);
        hashMap.put("isPost" ,true);

        databaseReference.push().setValue(hashMap);
    }
}