package com.quillium.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.quillium.Model.Story;
import com.quillium.Model.UserStories;
import com.quillium.R;
import com.quillium.StoryViewActivity;
import com.quillium.User;
import com.quillium.databinding.StoryRvDesignBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.viewHolder> {


    ArrayList<Story> list;
    Context context;
    DatabaseReference userRef;
    FirebaseDatabase database;
    CircleImageView profile;

    public StoryAdapter(ArrayList<Story> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.story_rv_design, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {

        Story story = list.get(position);

        if (story.getStories().size() > 0) {
            UserStories lastStory = story.getStories().get(story.getStories().size() - 1);
            Picasso.get()
                    .load(lastStory.getImage())
                    .into(holder.binding.storyImg);

            FirebaseDatabase.getInstance().getReference()
                    .child("users")
                    .child(story.getStoryBy())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            User user = snapshot.getValue(User.class);
                            if (user != null) {
                                Picasso.get()
                                        .load(user.getProfilePhotoUrl())
                                        .into(holder.binding.profileImagePicture);
                                holder.binding.storyPersonFrontName.setText(user.getFullname());

                                holder.binding.storyImg.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
//                                   String time = TimeAgo.using(lastStory.getStoryAt());
//                                   holder.binding.time.setText(time);


                                        UserStories lastStory = story.getStories().get(story.getStories().size() - 1);
                                        String name = holder.binding.storyPersonFrontName.getText().toString();
//                                   String email = user.getEmail(); // You should replace this with the actual email value
//                                   String email = longNumber; // You should replace this with the actual email value
//                                   String time = String.valueOf(lastStory.getStoryAt()); // Assuming you have a timestamp in UserStories
                                        String longNumber = TimeAgo.using(lastStory.getStoryAtLong());
                                        String time = "";


                                        openStoryViewActivity(story.getStories(), name, longNumber, time);
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Handle onCancelled event if needed
                        }
                    });
        }
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {

//        ImageView storyImg, profile, storyType;
//        TextView name;

        StoryRvDesignBinding binding;

        public viewHolder(@NonNull View itemView) {
            super(itemView);

//            storyImg = itemView.findViewById(R.id.postImage);
//            profile = itemView.findViewById(R.id.profile_image_picture);
//            storyType = itemView.findViewById(R.id.storyType);
//            name = itemView.findViewById(R.id.name);

            binding = StoryRvDesignBinding.bind(itemView);

        }
    }

    private void openStoryViewActivity(ArrayList<UserStories> userStories, String name, String email, String time) {
        ArrayList<String> imageUrls = new ArrayList<>();
        for (UserStories userStory : userStories) {
            imageUrls.add(userStory.getImage());
        }

        Intent intent = new Intent(context, StoryViewActivity.class);
        intent.putStringArrayListExtra(StoryViewActivity.EXTRA_IMAGE_URLS, imageUrls);
        intent.putExtra(StoryViewActivity.EXTRA_NAME, name);
        intent.putExtra(StoryViewActivity.EXTRA_EMAIL, email);
        intent.putExtra(StoryViewActivity.EXTRA_TIME, time);
        context.startActivity(intent);
    }
}
