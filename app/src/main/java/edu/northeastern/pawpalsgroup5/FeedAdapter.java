package edu.northeastern.pawpalsgroup5;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
    }

    @Override
    public int getItemCount() {
        return posts.size();
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