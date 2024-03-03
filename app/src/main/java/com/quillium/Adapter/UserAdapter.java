package com.quillium.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.quillium.Model.Follow;
import com.quillium.Model.Notification;
import com.quillium.R;
import com.quillium.User;
import com.quillium.databinding.UserSampleBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;


public class UserAdapter extends RecyclerView.Adapter<UserAdapter.viewHolder>{
    Context context;
    ArrayList<User> list;
    private Button FollowBtn;

    public UserAdapter(Context context, ArrayList<User> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_sample,parent,false);

        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        User user = list.get(position);

        Picasso.get()
                .load(user.getProfilePhotoUrl())
                .placeholder(R.drawable.man)
                .into(holder.binding.profilePictureImage);

        holder.binding.textView4.setText(user.getFullname());
        holder.binding.textView5.setText(user.getDepartment());

        FirebaseDatabase.getInstance().getReference()
                        .child("users")
                                .child(user.getUserId())
                                        .child("followers")
                                                .child(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            holder.binding.followBtn.setBackgroundDrawable(ContextCompat.getDrawable(context,R.drawable.follow_active_btn));
                            holder.binding.followBtn.setText("Following");
                            holder.binding.followBtn.setTextColor(context.getResources().getColor(R.color.lavender));
                            holder.binding.followBtn.setEnabled(false);
                        }
                        else {
                            holder.binding.followBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    holder.binding.followBtn.setBackgroundDrawable(ContextCompat.getDrawable(context,R.drawable.follow_active_btn));
                                    holder.binding.followBtn.setText("Following");
                                    holder.binding.followBtn.setTextColor(context.getResources().getColor(R.color.lavender));
                                    holder.binding.followBtn.setEnabled(false);

                                    Follow follow = new Follow();
                                    follow.setFollowedBy(FirebaseAuth.getInstance().getUid());
                                    follow.setFollowedAt(String.valueOf(new Date().getTime()));

                                    FirebaseDatabase.getInstance().getReference()
                                            .child("users")
                                            .child(user.getUserId())
                                            .child("followers")
                                            .child(FirebaseAuth.getInstance().getUid())
                                            .setValue(follow).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    FirebaseDatabase.getInstance().getReference()
                                                            .child("users")
                                                            .child(user.getUserId())
                                                            .child("followerCount")
                                                            .setValue(user.getFollowerCount()+1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    Toast.makeText(context,"You Followed "+user.getFullname(),Toast.LENGTH_SHORT).show();

                                                                    Notification notification = new Notification();
                                                                    notification.setNotificationBy(FirebaseAuth.getInstance().getUid());
                                                                    notification.setNotificationAt(new Date().getTime());
                                                                    notification.setType("follow");

                                                                    FirebaseDatabase.getInstance().getReference()
                                                                            .child("notification")
                                                                            .child(user.getUserId())
                                                                            .push()
                                                                            .setValue(notification);
                                                                }
                                                            });
                                                }
                                            });
                                }
                            });

                        }
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
        UserSampleBinding binding;
        public viewHolder(@NonNull View itemView) {
            super(itemView);

            binding = UserSampleBinding.bind(itemView);
        }
    }
}
