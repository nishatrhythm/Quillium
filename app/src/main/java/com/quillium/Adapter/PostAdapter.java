package com.quillium.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.quillium.CommentActivity;
import com.quillium.Model.Notification;
import com.quillium.Model.Post;
import com.quillium.R;
import com.quillium.User;
import com.quillium.databinding.DashboardRvSampleBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.viewHolder>{


    ArrayList<Post> list;
    Context context;

    public PostAdapter(ArrayList<Post> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.dashboard_rv_sample,parent,false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        Post model = list.get(position);
        Picasso.get()
                .load(model.getPostImage())
                .into(holder.binding.postImage);

//        holder.binding.like.setText(model.getPostLike()+""  );
//        holder.binding.comment.setText(model.getCommentCount()+""+" Comment");
//        holder.binding.postDescription.setText(model.getPostDescription());

        if(model.getPostLike()==0){
            holder.binding.like.setText("  Like");
        } else if (model.getPostLike()==1) {
            holder.binding.like.setText(" "+model.getPostLike()+"  Like");
        }else {
            holder.binding.like.setText(" "+model.getPostLike()+"  Likes");
        }

        if(model.getCommentCount()==0){
            holder.binding.comment.setText("Comment");
        } else if (model.getCommentCount()==1) {
            holder.binding.comment.setText(" "+model.getCommentCount()+"  Comment");
        }else {
            holder.binding.comment.setText(" "+model.getCommentCount()+"  Comments");
        }

        holder.binding.postDescription.setText(model.getPostDescription());


        FirebaseDatabase.getInstance().getReference().child("users")
                .child(model.getPostedBy()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            User user = snapshot.getValue(User.class);
                            if (user != null) {
                                String profilePhotoUrl = user.getProfilePhotoUrl();

                                holder.binding.userName.setText(user.getFullname());
                                holder.binding.about.setText(user.getDepartment());
                                Picasso.get()
                                        .load(profilePhotoUrl)
                                        .placeholder(R.drawable.man)
                                        .into(holder.binding.profileImagePicture);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



        FirebaseDatabase.getInstance().getReference()
                        .child("posts")
                        .child(model.getPostId())
                        .child("likes")
                        .child(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            holder.binding.like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.thumb_up_fill, 0,0,0);

//                            holder.binding.like.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    FirebaseDatabase.getInstance().getReference()
//                                            .child("posts")
//                                            .child(model.getPostId())
//                                            .child("likes")
//                                            .child(FirebaseAuth.getInstance().getUid())
//                                            .setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
//                                                @Override
//                                                public void onSuccess(Void unused) {
//                                                    FirebaseDatabase.getInstance().getReference()
//                                                            .child("posts")
//                                                            .child(model.getPostId())
//                                                            .child("postLike")
//                                                            .setValue(model.getPostLike()-1).addOnSuccessListener(new OnSuccessListener<Void>() {
//                                                                @Override
//                                                                public void onSuccess(Void unused) {
//                                                                    holder.binding.like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.thumb_up, 0,0,0);
//
////                                                                    Notification notification = new Notification();
////                                                                    notification.setNotificationBy(FirebaseAuth.getInstance().getUid());
////                                                                    notification.setNotificationAt(new Date().getTime());
////                                                                    notification.setPostID(model.getPostId());
////                                                                    notification.setPostedBy(model.getPostedBy());
////                                                                    notification.setType("like");
////
////                                                                    FirebaseDatabase.getInstance().getReference()
////                                                                            .child("notification")
////                                                                            .child(model.getPostedBy())
////                                                                            .push()
////                                                                            .setValue(notification);
//                                                                }
//                                                            });
//                                                }
//                                            });
//                                }
//                            });
                        }
                        else {
                            holder.binding.like.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    FirebaseDatabase.getInstance().getReference()
                                            .child("posts")
                                            .child(model.getPostId())
                                            .child("likes")
                                            .child(FirebaseAuth.getInstance().getUid())
                                            .setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    FirebaseDatabase.getInstance().getReference()
                                                            .child("posts")
                                                            .child(model.getPostId())
                                                            .child("postLike")
                                                            .setValue(model.getPostLike()+1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    holder.binding.like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.thumb_up_fill, 0,0,0);

                                                                    Notification notification = new Notification();
                                                                    notification.setNotificationBy(FirebaseAuth.getInstance().getUid());
                                                                    notification.setNotificationAt(new Date().getTime());
                                                                    notification.setPostID(model.getPostId());
                                                                    notification.setPostedBy(model.getPostedBy());
                                                                    notification.setType("like");

                                                                    FirebaseDatabase.getInstance().getReference()
                                                                            .child("notification")
                                                                            .child(model.getPostedBy())
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
        holder.binding.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CommentActivity.class);
                intent.putExtra("postId", model.getPostId());
                intent.putExtra("postedBy", model.getPostedBy());
                intent.putExtra("postLike", model.getPostLike());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {

        DashboardRvSampleBinding binding;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            binding = DashboardRvSampleBinding.bind(itemView);
        }
    }

}
