package com.quillium;

import androidx.annotation.NonNull;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.quillium.Adapter.UsersAdapter;
import com.quillium.Listeners.UserListener;
import com.quillium.Model.User;
import com.quillium.databinding.ActivityMessengerUsersBinding;
import com.quillium.utils.Constants;
import com.quillium.utils.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

public class MessengerUsersActivity extends BaseActivity  implements UserListener {

    private ActivityMessengerUsersBinding binding;
    private PreferenceManager preferenceManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMessengerUsersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setListeners();
        getUsers();
    }

    private void setListeners(){
        binding.imageBack.setOnClickListener(v -> onBackPressed());
    }

    private void getUsers(){
        loading(true);
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection(Constants.KEY_COLLECTION_USERS)
                .whereEqualTo("verify", true) // Filter users where the verify field is true
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        loading(false);
                        String currentUserId = preferenceManager.getString(Constants.KEY_USER_ID);
                        if (task.isSuccessful() && task.getResult() !=null){
                            List<User> users = new ArrayList<>();
                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()){
                                if (currentUserId.equals(queryDocumentSnapshot.getId())){
                                    continue;
                                }
                                User user = new User();
                                user.name = queryDocumentSnapshot.getString(Constants.KEY_NAME);
                                user.email = queryDocumentSnapshot.getString(Constants.KEY_EMAIL);
                                user.image = queryDocumentSnapshot.getString(Constants.KEY_IMAGE);
                                user.token = queryDocumentSnapshot.getString(Constants.KEY_FCM_TOKEN);
                                user.id = queryDocumentSnapshot.getId();
                                users.add(user);
                            }
                            if (users.size()>0){
                                UsersAdapter usersAdapter = new UsersAdapter(users, MessengerUsersActivity.this);
                                binding.usersRecyclerView.setAdapter(usersAdapter);
                                binding.usersRecyclerView.setVisibility(View.VISIBLE);
                            } else {
                                showErrorMessage();
                            }
                        } else {
                            showErrorMessage();
                        }
                    }
                });
    }

    private void showErrorMessage(){
        binding.textErrorMessage.setText(String.format("%s", "No User Available"));
        binding.textErrorMessage.setVisibility(View.VISIBLE);
    }

    private void loading(Boolean isLoading){
        if(isLoading){
            binding.progressBar.setVisibility(View.VISIBLE);
        }else{
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onUserClicked(User user) {
        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
        intent.putExtra(Constants.KEY_USER, user);
        startActivity(intent);
        finish();
    }
}