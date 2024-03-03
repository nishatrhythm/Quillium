package com.quillium;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.quillium.Adapter.RecentConversationsAdapter;
import com.quillium.Fragment.FriendFragment;
import com.quillium.Fragment.HomeFragment;
import com.quillium.Fragment.NotificationFragment;
import com.quillium.Fragment.ProfileFragment;
import com.quillium.Model.ChatMessage;
import com.quillium.Model.Story;
import com.quillium.Model.UserStories;
import com.quillium.utils.Constants;
import com.quillium.utils.PreferenceManager;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomePageActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout drawerLayout;
    BottomNavigationView bottomNavigationView;
    FragmentManager fragmentManager;
    Toolbar toolbar;
    FloatingActionButton fab;
    TextView fullName, studentEmail, studentId;
    FirebaseAuth auth;
    DatabaseReference userRef;
    CircleImageView profile;
    private PreferenceManager preferenceManager;
    ProgressDialog storyDialog, logoutDialog;
    private Uri imageUri;
    private static final int PICK_IMAGE_REQUEST_STORIES = 3;
    FirebaseStorage storage;
    FirebaseDatabase database;
    private List<ChatMessage> conversations;
    private RecentConversationsAdapter conversationsAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

//        checkEmailVerification();

//        Toolbar toolbar = findViewById(R.id.toolbar); // Replace with the correct ID
////        setSupportActionBar(toolbar);
//
//        ActionBar actionBar = getSupportActionBar();
//        if (actionBar != null) {
//            actionBar.setHomeAsUpIndicator(R.drawable.baseline_person_outline_24); // This line sets the drawer icon
//            actionBar.setDisplayHomeAsUpEnabled(true);
//        }

        preferenceManager = new PreferenceManager(getApplicationContext());
        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();

        storyDialog = new ProgressDialog(this);
        storyDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        storyDialog.setTitle("Story Uploading");
        storyDialog.setMessage("Please wait...");
        storyDialog.setCancelable(false);

        logoutDialog = new ProgressDialog(this);
        logoutDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        logoutDialog.setTitle("Signing out from Quillium");
        logoutDialog.setMessage("Please Wait...");
        logoutDialog.setCancelable(false);

        fab = findViewById(R.id.fab);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav,
                R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.navigation_drawer);
        navigationView.setNavigationItemSelectedListener(this);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setBackground(null);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.home_button) {
                    openFragment(new HomeFragment());
                    return true;
                } else if (id == R.id.friends_button) {
                    openFragment(new FriendFragment());
                    return true;
                } else if (id == R.id.notifications_button) {
                    openFragment(new NotificationFragment());
                    return true;
                } else if (id == R.id.profile_button) {
                    openFragment(new ProfileFragment());
                    return true;
                }
                return false;
            }
        });

        fragmentManager = getSupportFragmentManager();
        openFragment(new HomeFragment());

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomDialog();
            }
        });


//==================================================================================================
        // Get the header view of the NavigationView
        View headerView = navigationView.getHeaderView(0);

        // Find the TextViews within the header view
        fullName = headerView.findViewById(R.id.nav_full_name);
        studentEmail = headerView.findViewById(R.id.nav_student_email);
        profile = headerView.findViewById(R.id.profile_image_picture_header);
        studentId = headerView.findViewById(R.id.nav_student_ID);

        // Initialize Firebase Auth and Database references
        auth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference("users");

        // Load user data
        loadUserData();
//==================================================================================================

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
                                .into(profile);

                        String fullname = user.getFullname();
                        String email = user.getDepartment();
                        String id = user.getStudentId();

                        // Set the fullname and email to the TextViews
                        fullName.setText(fullname);
                        studentEmail.setText(email);
                        studentId.setText(id);
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemID = item.getItemId();
        if (itemID == R.id.nav_home) {
//            openFragment(new HomeFragment());
            Toast.makeText(HomePageActivity.this, "Home Menu is Clicked", Toast.LENGTH_LONG).show();
        } else if (itemID == R.id.nav_settings) {
//            openFragment(new FriendFragment());
            Toast.makeText(HomePageActivity.this, "Settings Menu is Clicked", Toast.LENGTH_LONG).show();
        } else if (itemID == R.id.nav_share) {
//            openFragment(new NotificationFragment());
            Toast.makeText(HomePageActivity.this, "Share Menu is Clicked", Toast.LENGTH_LONG).show();
        } else if (itemID == R.id.nav_about) {
//            openFragment(new ProfileFragment());
            Toast.makeText(HomePageActivity.this, "About Menu is Clicked", Toast.LENGTH_LONG).show();
        } else if (itemID == R.id.nav_logout) {
            logoutDialog.show();
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            DocumentReference documentReference = firestore.collection(Constants.KEY_COLLECTION_USERS).document(
                    preferenceManager.getString(Constants.KEY_USER_ID)
            );
            HashMap<String, Object> updates = new HashMap<>();
            updates.put(Constants.KEY_FCM_TOKEN, FieldValue.delete());
            documentReference.update(updates)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            // FCM token updated successfully
                            // Now, proceed to logout
                            auth.signOut();
                            preferenceManager.clear();
                            Toast.makeText(getApplicationContext(), "Successfully logged out", Toast.LENGTH_LONG).show();
                            logoutDialog.dismiss();

                            // Redirect the user to the login screen or perform any other necessary actions
                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                            startActivity(intent);
                            finish(); // Close the current activity to prevent the user from going back
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Failed to update FCM token
                            Toast.makeText(getApplicationContext(), "Unable to Sign Out.", Toast.LENGTH_LONG).show();
                            logoutDialog.dismiss();
                        }
                    });
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void openFragment(Fragment fragment) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frame_layout, fragment);
        transaction.commit();
    }

    private void showBottomDialog() {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomsheetlayout);

        ConstraintLayout createPostLayout = dialog.findViewById(R.id.createPost);
        ConstraintLayout createStoryLayout = dialog.findViewById(R.id.createStory);
//        ImageView cancelButton = dialog.findViewById(R.id.cancelButton);

        createPostLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
//                openFragment(new CreatePostFragment());
                Intent intent = new Intent(HomePageActivity.this, CreatePostActivity.class);
                startActivity(intent);
//                Toast.makeText(HomePage.this, "Create a Post is clicked", Toast.LENGTH_SHORT).show();
            }
        });

        createStoryLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
//                Toast.makeText(getApplicationContext(), "Create a Story is Clicked", Toast.LENGTH_SHORT).show();
                openGalleryForStories();
            }
        });

//        cancelButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dialog.dismiss();
//            }
//        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_item, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

/*        if (id == R.id.menu_search) {
//            openFragment(new HomeFragment());
            Toast.makeText(HomePage.this, "Search is Clicked", Toast.LENGTH_LONG).show();
            return true;
        } else */if (id == R.id.menu_messenger) {
//            openFragment(new FriendFragment());
            Intent intent = new Intent(HomePageActivity.this, MessengerHomePageActivity.class);
            startActivity(intent);
//            Toast.makeText(HomePageActivity.this, "Messenger button is clicked", Toast.LENGTH_LONG).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

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

            if (requestCode == PICK_IMAGE_REQUEST_STORIES) { // Check the request code
                // Set the selected image to the Stories photo
//            addStoryImage.setImageURI(imageUri);

                storyDialog.show();

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
                                                                Toast.makeText(HomePageActivity.this, "Story Uploaded Successfully.", Toast.LENGTH_LONG).show();
                                                                storyDialog.dismiss();
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

}