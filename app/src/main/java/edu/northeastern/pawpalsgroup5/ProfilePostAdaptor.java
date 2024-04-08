package edu.northeastern.pawpalsgroup5;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import edu.northeastern.pawpalsgroup5.models.Post;

public class ProfilePostAdaptor extends RecyclerView.Adapter<ProfilePostAdaptor.PostViewHolder> {
    private List<Post> posts;
    private LayoutInflater inflater;

    public ProfilePostAdaptor(Context context, List<Post> posts) {
        this.inflater = LayoutInflater.from(context);
        this.posts = posts;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post currentPost = posts.get(position);
        holder.postTextView.setText(currentPost.getDescription());
        Picasso.get()
                .load(currentPost.getPicture())
                .into(holder.postImageView);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        public ImageView postImageView;
        public TextView postTextView;

        public PostViewHolder(View itemView) {
            super(itemView);
            postImageView = itemView.findViewById(R.id.postImageView);
            postTextView = itemView.findViewById(R.id.postTextView);
        }
    }
}
