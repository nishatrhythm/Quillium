package com.quillium;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.quillium.Adapter.CommentAdapter;
import com.quillium.Model.Comment;
import com.quillium.Model.Notification;
import com.quillium.Model.Post;
import com.quillium.databinding.ActivityCommentBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

public class CommentActivity extends AppCompatActivity {

    ActivityCommentBinding binding;
    Intent intent;
    String postId;
    String postedBy;
    String postLike;

    // Initialize postLikeInt with a default value of 0
    int postLikeInt = 0;
    FirebaseDatabase database;
    FirebaseAuth auth;
    ArrayList<Comment> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCommentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        intent = getIntent();

        setSupportActionBar(binding.toolbar2);
        CommentActivity.this.setTitle("Comments");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        postId = intent.getStringExtra("postId");
        postedBy = intent.getStringExtra("postedBy");
        postLike = intent.getStringExtra("postLike");

        // Parse postLike to integer if it's not null
        if (postLike != null) {
            try {
                postLikeInt = Integer.parseInt(postLike);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                // Handle the case where postLike is not a valid integer
            }
        }

        database.getReference()
                .child("posts")
                .child(postId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Post post = snapshot.getValue(Post.class);
                        Picasso.get()
                                .load(post.getPostImage())
//                                .placeholder(R.drawable.placeholder)
                                .into(binding.postImage);
                        binding.description.setText(post.getPostDescription());
//                        binding.like.setText(post.getPostLike()+"");
//                        binding.comment.setText(post.getCommentCount()+"");

                        if(post.getPostLike()==0){
                            binding.like.setText("  Like");
                        } else if (post.getPostLike()==1) {
                            binding.like.setText(" "+post.getPostLike()+"  Like");
                        }else {
                            binding.like.setText(" "+post.getPostLike()+"  Likes");
                        }

                        if(post.getCommentCount()==0){
                            binding.comment.setText("Comment");
                        } else if (post.getCommentCount()==1) {
                            binding.comment.setText(" "+post.getCommentCount()+"  Comment");
                        }else {
                            binding.comment.setText(" "+post.getCommentCount()+"  Comments");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        database.getReference()
                .child("users")
                .child(postedBy).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        Picasso.get()
                                .load(user.getProfilePhotoUrl())
                                .placeholder(R.drawable.man)
                                .into(binding.profileImage);
                        binding.name.setText(user.getFullname());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        binding.commentPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(binding.commentET.getText().toString().isEmpty()){
                    Toast.makeText(CommentActivity.this, "Please write something.", Toast.LENGTH_SHORT).show();
                }
                else {
                    Comment comment = new Comment();
                    comment.setCommentBody(binding.commentET.getText().toString());
                    comment.setCommentedAt(new Date().getTime());
                    comment.setCommentedBy(FirebaseAuth.getInstance().getUid());

                    database.getReference()
                            .child("posts")
                            .child(postId)
                            .child("comments")
                            .push()
                            .setValue(comment).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    database.getReference()
                                            .child("posts")
                                            .child(postId)
                                            .child("commentCount").addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    int commentCount = 0;
                                                    if (snapshot.exists()){
                                                        commentCount = snapshot.getValue(Integer.class);
                                                    }
                                                    database.getReference()
                                                            .child("posts")
                                                            .child(postId)
                                                            .child("commentCount")
                                                            .setValue(commentCount + 1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    binding.commentET.setText("");
                                                                    Toast.makeText(CommentActivity.this, "Your comment is published", Toast.LENGTH_SHORT).show();

                                                                    Notification notification = new Notification();
                                                                    notification.setNotificationBy(FirebaseAuth.getInstance().getUid());
                                                                    notification.setNotificationAt(new Date().getTime());
                                                                    notification.setPostID(postId);
                                                                    notification.setPostedBy(postedBy);
                                                                    notification.setType("comment");

                                                                    FirebaseDatabase.getInstance().getReference()
                                                                            .child("notification")
                                                                            .child(postedBy)
                                                                            .push()
                                                                            .setValue(notification);
                                                                }
                                                            });
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                }
                            });
                }
            }
        });

        CommentAdapter adapter = new CommentAdapter(this, list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.commentRv.setLayoutManager(layoutManager);
        binding.commentRv.setAdapter(adapter);

        database.getReference()
                .child("posts")
                .child(postId)
                .child("comments").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                            Comment comment = dataSnapshot.getValue(Comment.class);
                            list.add(comment);
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        FirebaseDatabase.getInstance().getReference()
                .child("posts")
                .child(postId)
                .child("likes")
                .child(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            binding.like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.thumb_up_fill, 0,0,0);
                        }
                        else {
                            binding.like.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    FirebaseDatabase.getInstance().getReference()
                                            .child("posts")
                                            .child(postId)
                                            .child("likes")
                                            .child(FirebaseAuth.getInstance().getUid())
                                            .setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    FirebaseDatabase.getInstance().getReference()
                                                            .child("posts")
                                                            .child(postId)
                                                            .child("postLike")
                                                            .setValue(postLikeInt+1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    binding.like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.thumb_up_fill, 0,0,0);

                                                                    Notification notification = new Notification();
                                                                    notification.setNotificationBy(FirebaseAuth.getInstance().getUid());
                                                                    notification.setNotificationAt(new Date().getTime());
                                                                    notification.setPostID(postId);
                                                                    notification.setPostedBy(postedBy);
                                                                    notification.setType("like");

                                                                    FirebaseDatabase.getInstance().getReference()
                                                                            .child("notification")
                                                                            .child(postedBy)
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
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
}