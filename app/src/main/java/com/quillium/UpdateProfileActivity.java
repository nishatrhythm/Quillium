package com.quillium;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.quillium.Fragment.ProfileFragment;
import com.quillium.utils.Constants;
import com.quillium.utils.PreferenceManager;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class UpdateProfileActivity extends AppCompatActivity {

    ImageView coverPhoto, cPhoto, profilePhoto, pPhoto;
    AppCompatImageView closeButton;
    Button save;
    private String encodedImage;
    private PreferenceManager preferenceManager;
    Uri imageUri1, imageUri2;
    FirebaseStorage storage;
    FirebaseDatabase database;
    DatabaseReference userRef;
    ProgressDialog saveDialog;
    private static final int PICK_IMAGE_REQUEST_COVER = 2;
    private static final int RESULT_OK = -1; // or Activity.RESULT_OK

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();
        userRef = database.getReference("users").child(FirebaseAuth.getInstance().getUid());

        coverPhoto = findViewById(R.id.coverPhoto);
        cPhoto = findViewById(R.id.changeCoverPhoto);
        profilePhoto = findViewById(R.id.profile_picture_image);
        pPhoto = findViewById(R.id.changeProfilePhoto);
        save = findViewById(R.id.saveButton);

        preferenceManager = new PreferenceManager(getApplicationContext());

        saveDialog = new ProgressDialog(this);
        saveDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        saveDialog.setTitle("Updating Your Profile");
        saveDialog.setMessage("Please wait...");
        saveDialog.setCancelable(false);

        pPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                pickImage.launch(intent);
            }
        });

        cPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGalleryForCoverPhoto();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDialog.show();
                if (imageUri1 != null) {
                    uploadProfilePhotoToFirestore();
                    uploadProfilePhotoToFirebase();
                } else if (imageUri2 != null) {
                    // Upload the cover photo to Firebase if needed
                    uploadCoverPhotoToFirebase();
                }else {
                    Toast.makeText(getApplicationContext(), "No Image have to save.", Toast.LENGTH_SHORT).show();
                    saveDialog.dismiss();
                }
            }
        });

        // Back Button
        closeButton = findViewById(R.id.close_button_create_posst);
        closeButton.setOnClickListener(v -> onBackPressed());

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    String coverPhotoUrl = user.getCoverPhotoUrl();
                    String profilePhotoUrl = user.getProfilePhotoUrl();

                    // Load cover photo using Picasso or any other image loading library
                    Picasso.get()
                            .load(coverPhotoUrl)
                            .placeholder(R.drawable.cover_photo_placeholder)
                            .into(coverPhoto);

                    // Load Profile photo using Picasso or any other image loading library
                    Picasso.get()
                            .load(profilePhotoUrl)
                            .placeholder(R.drawable.profile_photo_placeholder)
                            .into(profilePhoto);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error if needed
            }
        });
    }

    private String encodeImage(Bitmap bitmap) {
        int previewWidth = 150;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    if (result.getData() != null) {
                        imageUri1 = result.getData().getData();
                        try {
                            InputStream inputStream = getApplicationContext().getContentResolver().openInputStream(imageUri1);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            profilePhoto.setImageBitmap(bitmap);
                            encodedImage = encodeImage(bitmap);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
    );

    private void uploadProfilePhotoToFirestore() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        String currentUserId = preferenceManager.getString(Constants.KEY_USER_ID);

        if (currentUserId != null && encodedImage != null) {
            Map<String, Object> data = new HashMap<>();
            data.put(Constants.KEY_IMAGE, encodedImage);

            firestore.collection(Constants.KEY_COLLECTION_USERS)
                    .document(currentUserId)
                    .set(data, SetOptions.merge()) // Merge to update without overwriting existing data
                    .addOnSuccessListener(aVoid -> {
                        // Image upload successful
                        Log.d("Upload", "Profile photo uploaded successfully");
//                        Toast.makeText(getApplicationContext(), "successfully", Toast.LENGTH_SHORT).show();
                        // You can add any additional logic here, like refreshing the UI
                    })
                    .addOnFailureListener(e -> {
                        // Image upload failed
                        Log.e("Upload", "Error uploading profile photo", e);
                        Toast.makeText(getApplicationContext(), "Fail", Toast.LENGTH_SHORT).show();
                        // Handle failure, such as displaying an error message to the user
                    });
        } else {
            Log.e("Upload", "currentUserId or encodedImage is null");
            Toast.makeText(getApplicationContext(), "currentUserId or encodedImage is null", Toast.LENGTH_SHORT).show();
            // Handle the case where either currentUserId or encodedImage is null
        }
    }

//     Upload Profile Photo into Realtime FirebaseDatabase
    private void uploadProfilePhotoToFirebase() {
        if (imageUri1 != null) {
            final StorageReference reference = storage.getReference().child("profile_photo")
                    .child(FirebaseAuth.getInstance().getUid());

            reference.putFile(imageUri1)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Get the download URL of the uploaded image
                        reference.getDownloadUrl().addOnSuccessListener(uri -> {
                            // Update user data in the database with the profile photo URL
                            updateProfilePhotoInDatabase(uri.toString());
                        });
                    })
                    .addOnFailureListener(e -> {
                    });
        }
    }

    private void updateProfilePhotoInDatabase(String imageUrl) {
        userRef = database.getReference("users").child(FirebaseAuth.getInstance().getUid());

        Map<String, Object> updates = new HashMap<>();
        updates.put("profilePhotoUrl", imageUrl);

        userRef.updateChildren(updates)
                .addOnSuccessListener(aVoid -> Toast.makeText(getApplicationContext(), "Profile Photo updated successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Failed to update Profile Photo", Toast.LENGTH_SHORT).show());

        saveDialog.dismiss();
        Intent intent = new Intent(getApplicationContext(), HomePageActivity.class);
        startActivity(intent);
        finish();
    }

    private void openGalleryForCoverPhoto() {
        // Open gallery for cover photo
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_COVER);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri2 = data.getData();

            if (requestCode == PICK_IMAGE_REQUEST_COVER) {
                // Set the selected image to the cover photo
                coverPhoto.setImageURI(imageUri2);
            }
        }
    }
    private void uploadCoverPhotoToFirebase() {
        if (imageUri2 != null) {
            final StorageReference reference = storage.getReference().child("cover_photo")
                    .child(FirebaseAuth.getInstance().getUid());

            reference.putFile(imageUri2)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Image uploaded successfully
                            // Get the download URL of the uploaded image
                            reference.getDownloadUrl().addOnSuccessListener(uri -> {
                                // Update user data in the database with the image URL
                                updateCoverPhotoInDatabase(uri.toString());
                            });
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Handle any errors that occurred during the upload
                    });

        }
    }
    private void updateCoverPhotoInDatabase(String imageUrl) {
        // Get the reference to the user's data in the database
        userRef = database.getReference("users").child(FirebaseAuth.getInstance().getUid());

        // Create a Map to update the user's data
        Map<String, Object> updates = new HashMap<>();
        updates.put("coverPhotoUrl", imageUrl);

        // Update the user's data in the database
        userRef.updateChildren(updates)
                .addOnSuccessListener(aVoid -> {
                    // Database update successful
                    Toast.makeText(getApplicationContext(), "Cover Photo updated successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Handle any errors that occurred during the database update
                    Toast.makeText(getApplicationContext(), "Failed to update Cover Photo", Toast.LENGTH_SHORT).show();
                });

        saveDialog.dismiss();
        Intent intent = new Intent(getApplicationContext(), HomePageActivity.class);
        startActivity(intent);
        finish();
    }
}