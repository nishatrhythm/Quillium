package com.quillium.Fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.quillium.Adapter.FollowersAdapter;
import com.quillium.FirebaseRegistrationActivity;
import com.quillium.LoginActivity;
import com.quillium.Model.Follow;
import com.quillium.R;
import com.quillium.UpdateProfileActivity;
import com.quillium.User;
import com.quillium.databinding.FragmentProfileBinding;
import com.quillium.utils.Constants;
import com.quillium.utils.PreferenceManager;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import android.util.Base64;
import java.util.HashMap;
import java.util.Map;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProfileFragment extends Fragment {

    ImageView coverPhoto, cPhoto, profile,profilePhoto;
    TextView name, id, followCount, session;
    Button updateProfile;
    FragmentProfileBinding binding;
    FirebaseStorage storage;
    FirebaseDatabase database;
    private Uri imageUri;
    DatabaseReference userRef, databaseReference;
    RecyclerView recyclerView;
    ArrayList<Follow> list;
//    ProgressDialog profileDialog, coverDialog;
    private String encodedImage;
    private PreferenceManager preferenceManager;


    private static final int PICK_IMAGE_REQUEST_PROFILE = 1;
    private static final int PICK_IMAGE_REQUEST_COVER = 2;
    private static final int RESULT_OK = -1; // or Activity.RESULT_OK



    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        cPhoto = view.findViewById(R.id.coverPhoto);
        profilePhoto = view.findViewById(R.id.profile_picture_image);
        name = view.findViewById(R.id.username);
        id = view.findViewById(R.id.userId);
        recyclerView = view.findViewById(R.id.followersRV);
        followCount = view.findViewById(R.id.followers);
        updateProfile = view.findViewById(R.id.editProfileButton);
        session = view.findViewById(R.id.sessionId);

//        profileDialog = new ProgressDialog(getActivity());
//        profileDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        profileDialog.setTitle("Profile photo is uploading");
//        profileDialog.setMessage("Please wait...");
//        profileDialog.setCancelable(false);
//
//        coverDialog = new ProgressDialog(getActivity());
//        coverDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        coverDialog.setTitle("Cover photo is uploading");
//        coverDialog.setMessage("Please wait...");
//        coverDialog.setCancelable(false);

        updateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), UpdateProfileActivity.class);
                startActivity(intent);
            }
        });

        list = new ArrayList<>();

        preferenceManager = new PreferenceManager(getActivity());


        FollowersAdapter adapter = new FollowersAdapter(list,getContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        database.getReference().child("users")
                .child(FirebaseAuth.getInstance().getUid())
                .child("followers").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                            Follow follow = dataSnapshot.getValue(Follow.class);
                            list.add(follow);
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        userRef = database.getReference("users").child(FirebaseAuth.getInstance().getUid());
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
                            .placeholder(R.drawable.cover_photo)
                            .into(cPhoto);

                    // Load Profile photo using Picasso or any other image loading library
                    Picasso.get()
                            .load(profilePhotoUrl)
                            .placeholder(R.drawable.profile_photo)
                            .into(profilePhoto);

                    String fullname = user.getFullname();
                    String email = user.getEmail();
                    String follows = String.valueOf(user.getFollowerCount());

                    String sess = extractSessionFromEmail(email);

                    // Set the fullname and email to the TextViews
                    name.setText(fullname);
                    id.setText(email);
                    followCount.setText(follows);
                    session.setText(sess);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error if needed
            }
        });

//        profile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                pickImage.;
//                openGalleryForProfile();
//            }
//        });
//        profilePhoto.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                pickImage.launch(intent);
//
//                uploadProfilePhotoToFirestore();
//            }
//        });

//        coverPhoto.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                openGalleryForCoverPhoto();
//            }
//        });

        return view;
    }

    private String extractSessionFromEmail(String email) {
        // Define a pattern to extract session from email
        Pattern pattern = Pattern.compile("b(\\d{2})\\d+@");
        Matcher matcher = pattern.matcher(email);
        if (matcher.find()) {
            String year = matcher.group(1);
            int startYear = Integer.parseInt("20" + year);
            int endYear = startYear + 1;
            return startYear + "-" + String.valueOf(endYear).substring(2);
        } else {
            return "Unknown Session";
        }
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
                        Uri imageUri = result.getData().getData();
                        try {
                            InputStream inputStream = getActivity().getContentResolver().openInputStream(imageUri);
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
                        Toast.makeText(getActivity(), "successfully", Toast.LENGTH_SHORT).show();
                        // You can add any additional logic here, like refreshing the UI
                    })
                    .addOnFailureListener(e -> {
                        // Image upload failed
                        Log.e("Upload", "Error uploading profile photo", e);
                        Toast.makeText(getActivity(), "Fail", Toast.LENGTH_SHORT).show();
                        // Handle failure, such as displaying an error message to the user
                    });
        } else {
            Log.e("Upload", "currentUserId or encodedImage is null");
            Toast.makeText(getActivity(), "currentUserId or encodedImage is null", Toast.LENGTH_SHORT).show();
            // Handle the case where either currentUserId or encodedImage is null
        }
    }

    private void openGalleryForProfile() {
        // Open gallery for profile photo
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_PROFILE);
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
            imageUri = data.getData();

            if (requestCode == PICK_IMAGE_REQUEST_PROFILE) {
                // Set the selected image to the profile photo
                profilePhoto.setImageURI(imageUri);

//                profileDialog.show();

                // Upload the profile photo to Firebase if needed
                 uploadProfilePhotoToFirebase();
            } else if (requestCode == PICK_IMAGE_REQUEST_COVER) {
                // Set the selected image to the cover photo
                cPhoto.setImageURI(imageUri);

//                coverDialog.show();

                // Upload the cover photo to Firebase if needed
                 uploadCoverPhotoToFirebase();
            }
        }
    }

//    private void uploadProfilePhotoToFirestore(Uri profilePhotoUri) {
//        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
//        String currentUserId = preferenceManager.getString(Constants.KEY_USER_ID);
//
//        if (currentUserId != null) {
//            firestore.collection(Constants.KEY_COLLECTION_USERS)
//                    .document(currentUserId)
//                    .update(Constants.KEY_IMAGE, profilePhotoUri.toString())
//                    .addOnSuccessListener(aVoid -> {
//                        // Profile photo URI updated successfully
//                        Toast.makeText(getActivity(), "Profile photo uploaded to Firestore", Toast.LENGTH_SHORT).show();
//                    })
//                    .addOnFailureListener(e -> {
//                        // Handle failure
//                        Toast.makeText(getActivity(), "Failed to upload profile photo to Firestore", Toast.LENGTH_SHORT).show();
//                    });
//        } else {
//            // Handle case where current user ID is null
//            Toast.makeText(getActivity(), "User ID not found", Toast.LENGTH_SHORT).show();
//        }
//    }

//    private String encodeImage(Bitmap bitmap) {
//        int previewWidth = 150;
//        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();
//        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);
//        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
//        byte[] bytes = byteArrayOutputStream.toByteArray();
//        return Base64.encodeToString(bytes, Base64.DEFAULT);
//    }
//
//    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
//            new ActivityResultContracts.StartActivityForResult(),
//            result -> {
//                if(result.getResultCode() == RESULT_OK){
//                    if (result.getData() != null){
//                        Uri imageUri = result.getData().getData();
//                        try {
//                            InputStream inputStream = getActivity().getContentResolver().openInputStream(imageUri);
//                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
////                            binding.imageProfile.setImageBitmap(bitmap);
//                            encodeImage = encodeImage(bitmap);
//                        }
//                        catch (FileNotFoundException e){
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            }
//    );

    private void uploadCoverPhotoToFirebase() {
        if (imageUri != null) {
            final StorageReference reference = storage.getReference().child("cover_photo")
                    .child(FirebaseAuth.getInstance().getUid());

            reference.putFile(imageUri)
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
                    Toast.makeText(getActivity(), "Cover Photo uploaded successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Handle any errors that occurred during the database update
                    Toast.makeText(getActivity(), "Failed to upload Cover Photo", Toast.LENGTH_SHORT).show();
                });

//        coverDialog.dismiss();
    }

    private void uploadProfilePhotoToFirebase() {
        if (imageUri != null) {
            final StorageReference reference = storage.getReference().child("profile_photo")
                    .child(FirebaseAuth.getInstance().getUid());

            reference.putFile(imageUri)
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
                .addOnSuccessListener(aVoid -> Toast.makeText(getActivity(), "Profile Photo uploaded successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(getActivity(), "Failed to upload Profile Photo", Toast.LENGTH_SHORT).show());

//        profileDialog.dismiss();
    }
}