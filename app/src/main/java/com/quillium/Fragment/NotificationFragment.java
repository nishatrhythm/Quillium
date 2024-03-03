package com.quillium.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.quillium.Adapter.NotificationAdapter;
import com.quillium.Model.Notification;
import com.quillium.R;

import java.util.ArrayList;

public class NotificationFragment extends Fragment {
    RecyclerView recyclerView;
    ArrayList<Notification> list;

    FirebaseDatabase database;

    public NotificationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        database = FirebaseDatabase.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        recyclerView = view.findViewById(R.id.notification2RV);

        list = new ArrayList<>();

//        list.add(new Notification(R.drawable.asir,"<b>Asir</b> Mentioned You", "Just now"));
//        list.add(new Notification(R.drawable.asir,"Asir Mentioned You", "Just now"));
//        list.add(new Notification(R.drawable.asir,"Asir Mentioned You", "Just now"));
//        list.add(new Notification(R.drawable.asir,"Asir Mentioned You", "Just now"));
//        list.add(new Notification(R.drawable.asir,"Asir Mentioned You", "Just now"));
//        list.add(new Notification(R.drawable.asir,"Asir Mentioned You", "Just now"));
//        list.add(new Notification(R.drawable.asir,"Asir Mentioned You", "Just now"));
//        list.add(new Notification(R.drawable.asir,"Asir Mentioned You", "Just now"));
//        list.add(new Notification(R.drawable.asir,"Asir Mentioned You", "Just now"));
//        list.add(new Notification(R.drawable.asir,"Asir Mentioned You", "Just now"));
//        list.add(new Notification(R.drawable.asir,"Asir Mentioned You", "Just now"));
//        list.add(new Notification(R.drawable.asir,"Asir Mentioned You", "Just now"));

        NotificationAdapter adapter = new NotificationAdapter(list, getContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

                database.getReference()
                .child("notification")
                .child(FirebaseAuth.getInstance().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                            Notification notification = dataSnapshot.getValue(Notification.class);
                            notification.setNotificationID(dataSnapshot.getKey());
                            list.add(0,notification);
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        return view;
    }
}