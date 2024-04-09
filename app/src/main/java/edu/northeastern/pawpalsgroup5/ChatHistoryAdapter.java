package edu.northeastern.pawpalsgroup5;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;
import edu.northeastern.pawpalsgroup5.models.Message;

public class ChatHistoryAdapter extends RecyclerView.Adapter<ChatHistoryAdapter.ViewHolder> {
    private List<Message> messages;
    private Context context;
    private boolean isChat;

    public ChatHistoryAdapter(List<Message> messages, Context context, boolean isChat) {
        this.messages = messages;
        this.context = context;
        this.isChat = isChat;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_history_item, parent, false);
        return new ChatHistoryAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Message message = messages.get(position);
        holder.senderNameTextView.setText(message.getSenderName());
        if (message.getSenderProfilePictureUrl().equals("default")) {
            holder.profileImageView.setImageResource(R.drawable.user_default);
        } else {
            Glide.with(context).load(message.getSenderProfilePictureUrl()).into(holder.profileImageView);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, Chat.class);
            intent.putExtra("username", message.getSenderName());
            context.startActivity(intent);
        });

        holder.lastMessageTextView.setText(message.getLastText());
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView senderNameTextView, lastMessageTextView;
        ImageView profileImageView;

        ViewHolder(View itemView) {
            super(itemView);
            senderNameTextView = itemView.findViewById(R.id.senderName);
            lastMessageTextView = itemView.findViewById(R.id.lastText);
            profileImageView = itemView.findViewById(R.id.profilePicture);
        }
    }
}



