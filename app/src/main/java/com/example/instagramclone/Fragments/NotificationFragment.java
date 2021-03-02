package com.example.instagramclone.Fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagramclone.Adapter.NotificationsAdapter;
import com.example.instagramclone.Model.Notifications;
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
import java.util.List;


public class NotificationFragment extends Fragment {

    private RecyclerView recyclerView;
    private NotificationsAdapter  notificationsAdapter;
    private List <Notifications> notificationsList;

    public NotificationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        recyclerView = view.findViewById(R.id.recyleView_notifications);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        notificationsList = new ArrayList<>();
        notificationsAdapter = new NotificationsAdapter(getContext(),notificationsList);
        recyclerView.setAdapter(notificationsAdapter);


        ReadNotifications();

        return view;
    }



    private void ReadNotifications() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Notifications").child(currentUser.getUid());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                notificationsList.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren())
                {
                    Notifications notifications = snapshot1.getValue(Notifications.class);
                    notificationsList.add(notifications);
                }
                Collections.reverse(notificationsList);
                notificationsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}