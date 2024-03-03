package com.quillium;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class CreatePostActivity extends AppCompatActivity {

    ImageView postPhotoImageView;

    AppCompatImageView closeButton;
    TextView Name;
    FirebaseAuth auth;
    DatabaseReference userRef, databaseReference;
    ProgressDialog postDialog;

    private EditText whatsOnYourMindEditText;
    private String name;
    private Button postButton;
    private LinearLayout uploadPhotoImageView;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    CircleImageView profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        // Initialize Views
        closeButton = findViewById(R.id.close_button_create_posst);
        Name = findViewById(R.id.create_post_fullName);
        whatsOnYourMindEditText = findViewById(R.id.whatsOnYourMind);
        postButton = findViewById(R.id.postButton);
        uploadPhotoImageView = findViewById(R.id.craetepost_upload_photo);
        postPhotoImageView = findViewById(R.id.create_post_PostPhoto);
        profile = findViewById(R.id.profilePictureCreatePost);

        postDialog = new ProgressDialog(this);
        postDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        postDialog.setTitle("Uploading Your Post");
        postDialog.setMessage("Please wait...");
        postDialog.setCancelable(false);

        closeButton.setOnClickListener(v -> onBackPressed());

        // Initialize Firebase Auth and Database references
        auth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference("users");

        // Load user data
        loadUserData();

        // Initialize Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference().child("posts");

        // Set click listener for the post button
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imageUri == null) {
                    // Show a message indicating that no image is selected
                    Toast.makeText(CreatePostActivity.this, "Please select an image to post", Toast.LENGTH_SHORT).show();
                } else {
                    // If an image is selected, proceed with creating the post
                    postDialog.show();
                    createPost();
                }
            }
        });

        // Set click listener for the upload photo ImageView
        uploadPhotoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            // Display the selected image
            postPhotoImageView.setImageURI(imageUri);
            // Hide the upload photo LinearLayout
            uploadPhotoImageView.setVisibility(View.GONE);
        }
    }

    private void createPost() {
        String postText = whatsOnYourMindEditText.getText().toString().trim();

        if (!postText.isEmpty()) {
            // Get the current user's ID (you may need to replace this with your own user ID retrieval logic)
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

            // Get the current timestamp
 //            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            String timestamp = String.valueOf(new Date().getTime());

            // Create a new post object
            Map<String, Object> postMap = new HashMap<>();
            postMap.put("postedBy", userId);
            postMap.put("postDescription", postText);
            postMap.put("postedAt", timestamp);

            // Push the post to Firebase Realtime Database
            DatabaseReference newPostRef = databaseReference.push();
            newPostRef.setValue(postMap);

            // Upload the image to Firebase Storage if an image is selected
            if (imageUri != null) {
                uploadImage(newPostRef.getKey(), postText);
            } else {
                // No image selected, you can still add the postText
                newPostRef.child("imageUrl").setValue(""); // Set an empty string or null
            }

            // Clear the EditText after posting
            whatsOnYourMindEditText.setText("");
        } else {
            // Show an error message if the post text is empty
            Toast.makeText(this, "Please enter something to post", Toast.LENGTH_SHORT).show();
            postDialog.dismiss();
        }
    }

    private void uploadImage(String postId, String postText) {
        // Get the current user's ID (you may need to replace this with your own user ID retrieval logic)
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Specify a storage reference (replace "images" with your desired storage path)
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("posts").child(userId);

        // Create a unique filename for the image
        String imageName = postId + "." + getFileExtension(imageUri);

        // Create a reference to the file to upload
        StorageReference imageReference = storageReference.child(imageName);

        // Upload the file
        imageReference.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
            // Image upload success
            // Get the download URL
            imageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                // Update the post with the actual image URL
                DatabaseReference postRef = databaseReference.child(postId);
                postRef.child("postImage").setValue(uri.toString());
                Log.d("CreatePostActivity", "Image is successfully uploaded, URL: " + uri.toString());

                // Show a success message
                Toast.makeText(this, "Post uploaded successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(CreatePostActivity.this, HomePageActivity.class);
                startActivity(intent);
                postDialog.dismiss();
            }).addOnFailureListener(e -> {
                // Handle failure to get download URL
                Log.e("CreatePostActivity", "Error getting the download URL: " + e.getMessage());
            });
        }).addOnFailureListener(e -> {
            // Image upload failed
            Log.e("CreatePostActivity", "Image upload failed: " + e.getMessage());
        });
    }


    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void loadUserData() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            userRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {

                        User user = dataSnapshot.getValue(User.class);
                        String profilePhotoUrl = user.getProfilePhotoUrl();

                        // Load Profile photo using Picasso or any other image loading library
                        Picasso.get()
                                .load(profilePhotoUrl)
                                .placeholder(R.drawable.man)
                                .into(profile);

                        String fullname = user.getFullname();
                        String email = user.getEmail();

                        // Set the fullname and email to the TextViews
                        Name.setText(fullname);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle error
                    Log.e("FirebaseData", "Error reading user data: " + databaseError.getMessage());
                }
            });
        }
    }
}