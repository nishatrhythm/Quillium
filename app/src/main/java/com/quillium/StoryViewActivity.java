// StoryViewActivity.java

package com.quillium;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class StoryViewActivity extends AppCompatActivity {

    public static final String EXTRA_IMAGE_URLS = "extra_image_urls";
    public static final String EXTRA_NAME = "extra_name";
    public static final String EXTRA_EMAIL = "extra_email";
    public static final String EXTRA_TIME = "extra_time";

    private ArrayList<String> imageUrls;
    private int currentIndex = 0;
    DatabaseReference userRef;
    FirebaseDatabase database;
    CircleImageView profile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_view);

        ImageView storyImageView = findViewById(R.id.storyImageView);
        ImageView closeBtn = findViewById(R.id.close_button_story);
        Button btnPrevious = findViewById(R.id.btnPrevious);
        Button btnNext = findViewById(R.id.btnNext);
        TextView textViewName = findViewById(R.id.storyPersonName);
        TextView textViewEmail = findViewById(R.id.storyPersonEmail);
        profile = findViewById(R.id.storyPersonProfile);

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StoryViewActivity.this, HomePageActivity.class);
                startActivity(intent);
            }
        });


//        userRef = database.getReference("users").child(FirebaseAuth.getInstance().getUid());
//        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.exists()) {
//                    User user = snapshot.getValue(User.class);
//                    String profilePhotoUrl = user.getProfilePhotoUrl();
//
//                    // Load Profile photo using Picasso or any other image loading library
//                    Picasso.get()
//                            .load(profilePhotoUrl)
//                            .placeholder(R.drawable.profile_photo_placeholder)
//                            .into(profile);
//
//                    String fullname = user.getFullname();
//                    String email = user.getEmail();
//
//                    // Set the fullname and email to the TextViews
//                    textViewName.setText(fullname);
//                    textViewEmail.setText(email);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                // Handle database error if needed
//            }
//        });


        if (getIntent().hasExtra(EXTRA_IMAGE_URLS) &&
                getIntent().hasExtra(EXTRA_NAME) &&
                getIntent().hasExtra(EXTRA_EMAIL)) {

            imageUrls = getIntent().getStringArrayListExtra(EXTRA_IMAGE_URLS);
            String name = getIntent().getStringExtra(EXTRA_NAME);
            String email = getIntent().getStringExtra(EXTRA_EMAIL);

            if (!imageUrls.isEmpty()) {
                displayImage(storyImageView, currentIndex);
            }

            textViewName.setText(name);
            textViewEmail.setText(email);

            btnPrevious.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPreviousStory();
                }
            });

            btnNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showNextStory();
                }
            });
        }
    }

    public void showPreviousStory() {
        if (currentIndex > 0) {
            currentIndex--;
            displayImage(findViewById(R.id.storyImageView), currentIndex);
        }
    }

    public void showNextStory() {
        if (currentIndex < imageUrls.size() - 1) {
            currentIndex++;
            displayImage(findViewById(R.id.storyImageView), currentIndex);
        }
    }

    private void displayImage(ImageView imageView, int index) {
        Picasso.get().load(imageUrls.get(index)).into(imageView);
    }
}
