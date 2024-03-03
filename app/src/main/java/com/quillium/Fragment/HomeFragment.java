package com.quillium.Fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.makeramen.roundedimageview.RoundedImageView;
import com.quillium.Adapter.PostAdapter;
import com.quillium.Adapter.RecentConversationsAdapter;
import com.quillium.Adapter.StoryAdapter;
import com.quillium.Model.Post;
import com.quillium.Model.Story;
import com.quillium.Model.UserStories;
import com.quillium.R;
import com.quillium.utils.Constants;
import com.quillium.utils.PreferenceManager;

import java.util.ArrayList;
import java.util.Date;


public class HomeFragment extends Fragment {


    RecyclerView storyRV, dashboardRV;
    ArrayList<Story> storyList;
    ArrayList<Post> PostList;
    FirebaseDatabase database;
    FirebaseAuth auth;
    RoundedImageView addStoryImage;
//    private PreferenceManager preferenceManager;
    private Uri imageUri;
    private static final int PICK_IMAGE_REQUEST_STORIES = 1;
    private static final int PICK_IMAGE_REQUEST_COVER = 2;
    private static final int RESULT_OK = -1; // or Activity.RESULT_OK
    FirebaseStorage storage;

    ProgressDialog dialog;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dialog = new ProgressDialog(getContext());
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

//        preferenceManager = new PreferenceManager(getContext());

//        updateToken();

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();

        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setTitle("Story Uploding");
        dialog.setMessage("Please Wait...");
        dialog.setCancelable(false);

        storyRV = view.findViewById(R.id.storyRV);

        storyList = new ArrayList<>();

        StoryAdapter adapter = new StoryAdapter(storyList, getContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
        storyRV.setLayoutManager(linearLayoutManager);
        storyRV.setNestedScrollingEnabled(false);
        storyRV.setAdapter(adapter);

        database.getReference()
                .child("stories").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            storyList.clear();
                            for (DataSnapshot storySnapshot : snapshot.getChildren()){
                                Story story = new Story();
                                story.setStoryBy(storySnapshot.getKey());
                                story.setStoryAt(storySnapshot.child("postedBy").getValue(String.class));

                                ArrayList<UserStories> stories = new ArrayList<>();
                                for (DataSnapshot snapshot1 : storySnapshot.child("userStories").getChildren()){
                                    UserStories userStories = snapshot1.getValue(UserStories.class);
                                    stories.add(userStories);
                                }
                                story.setStories(stories);
                                storyList.add(story);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



//      Dashboard Recycler View
        dashboardRV = view.findViewById(R.id.dashboardRv);
        dashboardRV.setHasFixedSize(true);
        PostList = new ArrayList<>();
        PostAdapter postAdapter = new PostAdapter(PostList,getContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        dashboardRV.setLayoutManager(layoutManager);
        dashboardRV.addItemDecoration(new DividerItemDecoration(dashboardRV.getContext(), DividerItemDecoration.HORIZONTAL));
        dashboardRV.setNestedScrollingEnabled(false);
        dashboardRV.setAdapter(postAdapter);

        database.getReference().child("posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                PostList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Post post = dataSnapshot.getValue(Post.class);
                    post.setPostId(dataSnapshot.getKey());
                    // Add posts to the beginning of the list
                    PostList.add(0, post);
                }
                postAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

//        addStoryImage = view.findViewById(R.id.postImageStores);
//        addStoryImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                openGalleryForStories();
//            }
//        });

        return view;
    }

//    private void updateToken() {
//        FirebaseMessaging.getInstance().getToken()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful() && task.getResult() != null) {
//                        String token = task.getResult();
//                        preferenceManager.putString(Constants.KEY_FCM_TOKEN, token);
//                        String userId = preferenceManager.getString(Constants.KEY_USER_ID);
//                        if (userId != null) {
//                            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
//                            DocumentReference documentReference = firestore.collection(Constants.KEY_COLLECTION_USERS).document(userId);
//                            documentReference.update(Constants.KEY_FCM_TOKEN, token)
//                                    .addOnSuccessListener(unused -> Toast.makeText(getContext(), "Token Update Successfully for user: " + userId, Toast.LENGTH_SHORT).show())
//                                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Token Update Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
//                        } else {
//                            Toast.makeText(getContext(), "User ID is null", Toast.LENGTH_SHORT).show();
//                        }
//                    } else {
//                        Toast.makeText(getContext(), "Failed to get token", Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }

    private void openGalleryForStories() {
        // Open gallery for Stories photo
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_STORIES);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();

            // Set the selected image to the Stories photo
//            addStoryImage.setImageURI(imageUri);

            dialog.show();

            final StorageReference reference = storage.getReference()
                    .child("stories")
                    .child(FirebaseAuth.getInstance().getUid())
                    .child(new Date().getTime()+"");

            reference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Story story = new Story();
                                    story.setStoryAt(String.valueOf(new Date().getTime()));

                                    database.getReference()
                                            .child("stories")
                                            .child(FirebaseAuth.getInstance().getUid())
                                            .child("postedBy")
                                            .setValue(story.getStoryAt()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    UserStories stories = new UserStories(uri.toString(),story.getStoryAt());

                                                    database.getReference()
                                                            .child("stories")
                                                            .child(FirebaseAuth.getInstance().getUid())
                                                            .child("userStories")
                                                            .push()
                                                            .setValue(stories).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    dialog.dismiss();
                                                                }
                                                            });
                                                }
                                            });
                                }
                            });
                        }
                    });
        }
    }

}