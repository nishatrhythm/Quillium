package com.quillium.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.quillium.Model.Follow;
import com.quillium.R;
import com.quillium.User;
import com.quillium.databinding.FriendRvSampleBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FollowersAdapter extends RecyclerView.Adapter<FollowersAdapter.viewHolder>{

    ArrayList<Follow> list;
    Context context;

    public FollowersAdapter(ArrayList<Follow> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.friend_rv_sample,parent,false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        Follow follow = list.get(position);

        FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(follow.getFollowedBy()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);

                        Picasso.get()
                                .load(user.getProfilePhotoUrl())
                                .placeholder(R.drawable.man)
                                .into(holder.binding.profilePictureFollower);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{

        FriendRvSampleBinding binding;
        public viewHolder(@NonNull View itemView) {
            super(itemView);

            binding = FriendRvSampleBinding.bind(itemView);

        }
    }
}
