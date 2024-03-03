package com.quillium.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.quillium.CommentActivity;
import com.quillium.Model.Notification;
import com.quillium.R;
import com.quillium.User;
import com.quillium.databinding.NotificationRvSampleBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.viewHolder>{

    ArrayList<Notification> list;
    Context context;

    public NotificationAdapter(ArrayList<Notification> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.notification_rv_sample,parent,false);


        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {

        Notification notification = list.get(position);

//        holder.profile.setImageResource(model.getProfile());
//        holder.notification.setText(Html.fromHtml(model.getNotification()));
//        holder.time.setText(model.getTime());

        String type = notification.getType();

        FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(notification.getNotificationBy())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);

                        Picasso.get()
                                .load(user.getProfilePhotoUrl())
                                .placeholder(R.drawable.man)
                                .into(holder.binding.profileImagePicture);

                        if (type.equals("like")) {
                            holder.binding.notification.setText(HtmlCompat.fromHtml("<b>" + user.getFullname() + "</b>" + " liked your post.", HtmlCompat.FROM_HTML_MODE_LEGACY));
                        } else if (type.equals("comment")) {
                            holder.binding.notification.setText(HtmlCompat.fromHtml("<b>" + user.getFullname() + "</b>" + " commented on your post.", HtmlCompat.FROM_HTML_MODE_LEGACY));
                        } else {
                            holder.binding.notification.setText(HtmlCompat.fromHtml("<b>" + user.getFullname() + "</b>" + " started following you.", HtmlCompat.FROM_HTML_MODE_LEGACY));
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        holder.binding.openNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!type.equals("follow")){
                    holder.binding.openNotification.setBackgroundColor(Color.parseColor("#FFFFFF"));

                    FirebaseDatabase.getInstance().getReference()
                            .child("notification")
                            .child(notification.getPostedBy())
                            .child(notification.getNotificationID())
                            .child("checkOpen")
                            .setValue(true);

                    Intent intent = new Intent(context, CommentActivity.class);
                    intent.putExtra("postId", notification.getPostID());
                    intent.putExtra("postedBy", notification.getPostedBy());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
                else {
                    FirebaseDatabase.getInstance().getReference()
                            .child("notification")
                            .child(FirebaseAuth.getInstance().getUid())
                            .child(notification.getNotificationID())
                            .child("checkOpen")
                            .setValue(true);
                }
            }
        });

        Boolean checkOpen = notification.isCheckOpen();
        if (checkOpen == true){
            holder.binding.openNotification.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }
        else {

        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{
//        ImageView profile;
//        TextView notification, time;

        NotificationRvSampleBinding binding;

        public viewHolder(@NonNull View itemView) {
            super(itemView);

//            profile = itemView.findViewById(R.id.profile_image_picture);
//            notification = itemView.findViewById(R.id.notification);
//            time = itemView.findViewById(R.id.time);

            binding = NotificationRvSampleBinding.bind(itemView);
        }
    }
}
