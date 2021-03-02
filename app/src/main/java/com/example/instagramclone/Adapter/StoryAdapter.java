package com.example.instagramclone.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagramclone.AddStoryActivity;
import com.example.instagramclone.Model.Story;
import com.example.instagramclone.Model.User;
import com.example.instagramclone.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.ViewHolder>{

    private Context mContext;
    private List <Story> mStory;

    public StoryAdapter(Context mContext, List<Story> mStory) {
        this.mContext = mContext;
        this.mStory = mStory;
    }

    @NonNull
    @Override
    public StoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0)
        {
            View view = LayoutInflater.from(mContext).inflate(R.layout.story_add_object, parent, false);
            return new ViewHolder(view);
        }
        else
        {
            View view = LayoutInflater.from(mContext).inflate(R.layout.story_object, parent, false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        final Story story = mStory.get(position);

        UserInfo(holder , story.getUserid() , position);

        if (holder.getAdapterPosition() != 0)
        {
            ViewStory(holder , story.getUserid());
        }

        if (holder.getAdapterPosition() == 0)
        {
            MyStory(holder.txt_storyAdd , holder.img_storyAdd , false);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.getAdapterPosition() == 0)
                {
                    MyStory(holder.txt_storyAdd , holder.img_storyImage , true);
                }
                else
                {

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mStory.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView img_storyImage;
        public ImageView img_storyAdd;
        public ImageView img_viewStory;

        public TextView txt_StoryUsername;
        public TextView txt_storyAdd;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            img_storyImage = itemView.findViewById(R.id.story_image);
            img_storyAdd = itemView.findViewById(R.id.story_add_image);
            img_viewStory = itemView.findViewById(R.id.story_image_view);

            txt_StoryUsername = itemView.findViewById(R.id.txt_story_username);
            txt_storyAdd = itemView.findViewById(R.id.txt_addstory);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
        {
            return 0;
        }
        return 1;
    }

    private void UserInfo(final ViewHolder viewHolder , String userId , final int pos)
    {
        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("Users").child(userId);

        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

                Glide.with(mContext).load(user.getImageurl()).into(viewHolder.img_storyImage);

                if (pos != 0)
                {
                    Glide.with(mContext).load(user.getImageurl()).into(viewHolder.img_viewStory);
                    viewHolder.txt_StoryUsername.setText(user.getUserName());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void MyStory(final TextView textView , final ImageView imageView , final boolean click)
    {
        DatabaseReference storyReference = FirebaseDatabase.getInstance().getReference("Story")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        storyReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int i = 0;
                long currentTime = System.currentTimeMillis();
                for (DataSnapshot snapshot1 : snapshot.getChildren())
                {
                    Story story = snapshot1.getValue(Story.class);

                    if (currentTime > story.getStartdate() && currentTime < story.getEnddate())
                    {
                        i++;
                    }
                }
                if (click)
                {
                    if (i > 0)
                    {
                        AlertDialog dialog = new AlertDialog.Builder(mContext).create();
                        dialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Hikaye Goruntuleme", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //hikaye sayfasina gidecek

                                dialog.dismiss();
                            }
                        });
                        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Hikaye Ekle", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent addStoryIntent = new Intent(mContext , AddStoryActivity.class);
                                mContext.startActivity(addStoryIntent);
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                    }
                    else
                    {
                        Intent addStoryIntent = new Intent(mContext ,  AddStoryActivity.class);
                        mContext.startActivity(addStoryIntent);
                    }
                }
                else
                {
                    if (i > 0)
                    {
                        textView.setText("Hikayem");
                        imageView.setVisibility(View.GONE);
                    }
                    else
                    {
                        textView.setText("Hikaye Ekle");
                        imageView.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void ViewStory(final ViewHolder viewHolder , String userId)
    {
        DatabaseReference storyReference = FirebaseDatabase.getInstance().getReference("Story").child(userId);

        storyReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int i = 0;
                for (DataSnapshot snapshot1 : snapshot.getChildren())
                {
                    if (!snapshot1.child("goruntulenmeler").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .exists() && System.currentTimeMillis() < snapshot1.getValue(Story.class).getEnddate())
                    {
                        i++;
                    }
                }
                if (i > 0)
                {
                    viewHolder.img_storyImage.setVisibility(View.VISIBLE);
                    viewHolder.img_viewStory.setVisibility(View.GONE);
                }
                else
                {
                    viewHolder.img_storyImage.setVisibility(View.GONE);
                    viewHolder.img_viewStory.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
