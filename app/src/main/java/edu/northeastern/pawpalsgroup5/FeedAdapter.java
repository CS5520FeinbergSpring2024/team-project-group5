package edu.northeastern.pawpalsgroup5;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import edu.northeastern.pawpalsgroup5.models.Post;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.FeedViewHolder> {
    private List<Post> posts;

    public FeedAdapter(List<Post> posts) {
        this.posts = posts;
    }

    @NonNull
    @Override
    public FeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feed, parent, false);
        return new FeedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedViewHolder holder, int position) {
        Post post = posts.get(position);

        holder.descriptionTextView.setText(post.getDescription());
        holder.usernameTextView.setText(post.getUsername());
        holder.numLikeTextView.setText(String.format("%d likes", post.getLikes()));
        holder.usernameTextView.setText(post.getUsername());
        holder.likeImageView.setImageResource(R.drawable.paw_white);
        Picasso.get()
                .load(post.getProfilePicture())
                .into(holder.profileImageView);

        Picasso.get()
                .load(post.getPicture())
                .into(holder.postImageView);

        holder.likeImageView.setOnClickListener(v -> {
            // Increment the like count in the model
            post.setLikes(post.getLikes() + 1);
            holder.numLikeTextView.setText(String.format("%d likes", post.getLikes()));
            holder.likeImageView.setImageResource(R.drawable.paw_black);
            // Update the like count in Firebase
            DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("posts").child(post.getPostId());
            postRef.child("likes").setValue(post.getLikes()).addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    post.setLikes(post.getLikes() - 1);
                    holder.numLikeTextView.setText(String.format("%d likes", post.getLikes()));
                }
            });
        });

        holder.chatImageView.setOnClickListener(v -> {
            String postUserId = posts.get(position).getUserId();
            String otherUserName = posts.get(position).getUsername();
            String otherUserProfilePic = posts.get(position).getProfilePicture();

            openChat(v.getContext(), postUserId, otherUserName, otherUserProfilePic);
        });
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    private void openChat(Context context, String postUserId, String otherUserName, String otherUserProfilePic) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        assert currentUser != null;
        String currentUserId = currentUser.getUid();
        DatabaseReference userChatsRef = FirebaseDatabase.getInstance().getReference("/userChats/");
        String chatKey = postUserId.compareTo(currentUserId) > 0 ? currentUserId + "_" + postUserId : postUserId + "_" + currentUserId;

        userChatsRef.child(chatKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String chatId;
                if (!snapshot.exists()) {
                    // chat does not exist, create a new one with chatId
                    chatId = userChatsRef.push().getKey();
                    userChatsRef.child(chatKey).setValue(chatId);
                } else {
                    chatId = snapshot.getValue(String.class);
                }

                // go to chat activity with this chatId
                Intent intent = new Intent(context, Chat.class);
                intent.putExtra("chatId", chatId);
                intent.putExtra("otherUserId", postUserId);
                intent.putExtra("otherUserName", otherUserName);
                intent.putExtra("otherUserProfilePic", otherUserProfilePic);
                context.startActivity(intent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("FeedAdapter", "Failed to read chat ID.", error.toException());
            }
        });
    }


    static class FeedViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImageView;
        ImageView postImageView;
        ImageView likeImageView;
        ImageView chatImageView;
        TextView numLikeTextView;
        TextView usernameTextView;
        TextView descriptionTextView;
        ImageView followImageView;

        public FeedViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImageView = itemView.findViewById(R.id.profileImageView);
            likeImageView = itemView.findViewById(R.id.likeImageView);
            chatImageView = itemView.findViewById(R.id.chatImageView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            numLikeTextView = itemView.findViewById(R.id.numLikeTextView);
            usernameTextView = itemView.findViewById(R.id.usernameTextView);
            followImageView = itemView.findViewById(R.id.followImageView);
            postImageView = itemView.findViewById(R.id.postImageView);
        }
    }
}