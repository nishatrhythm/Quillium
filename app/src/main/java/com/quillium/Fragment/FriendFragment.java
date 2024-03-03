package com.quillium.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.quillium.Adapter.UserAdapter;
import com.quillium.R;
import com.quillium.User;
import com.quillium.databinding.FragmentFriendBinding;

import java.util.ArrayList;

public class FriendFragment extends Fragment {

    FragmentFriendBinding binding;
    ArrayList<User> list = new ArrayList<>();
    FirebaseAuth auth;
    FirebaseDatabase database;

    public FriendFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentFriendBinding.inflate(inflater, container, false);

        UserAdapter adapter = new UserAdapter(getContext(),list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.usersRV.setLayoutManager(layoutManager);
        binding.usersRV.setAdapter(adapter);

        database.getReference().child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    user.setUserId(dataSnapshot.getKey());

                    // Check if the 'verified' field exists and its value is "true"
                    String verified = dataSnapshot.child("verify").getValue(String.class);
                    if (verified != null && verified.equals("true")) {
                        // If verified, add user to the list
                        list.add(0, user);
                    } else {
                        // If not verified, you can choose to skip adding this user to the list
                        // For now, let's just log a message
//                        Log.d("UserAdapter", "User is not verified");
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle any errors that may occur
            }
        });


        return binding.getRoot();
    }
}