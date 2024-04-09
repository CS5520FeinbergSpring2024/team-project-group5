package edu.northeastern.pawpalsgroup5;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import edu.northeastern.pawpalsgroup5.models.Message;

public class ChatMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final List<Message> messages;
    private final String currUserId;

    public ChatMessageAdapter(List<Message> messages, String currUserId) {
        this.messages = messages;
        this.currUserId = currUserId;
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);
        if (message.getSenderId().equals(currUserId)) {
            return 1;
        } else {
            return 2;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == 1) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.sent_message_item, parent, false);
            return new SentMessageHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.received_message_item, parent, false);
            return new ReceivedMessageHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messages.get(position);

        switch (holder.getItemViewType()) {
            case 1:
                ((SentMessageHolder) holder).bind(message);
                break;
            case 2:
                ((ReceivedMessageHolder) holder).bind(message);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    private static class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText;

        SentMessageHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.sent_message_body);
            timeText = itemView.findViewById(R.id.sent_message_time);
        }

        void bind(Message message) {
            if (message.getText() != null) {
                messageText.setText(message.getText());
            } else {
                // if message is null
                messageText.setText("");
            }

            if (message.getTimestamp() > 0) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
                String formattedDate = dateFormat.format(new Date(message.getTimestamp()));
                timeText.setText(formattedDate);
            } else {
                // if timestamp is not valid
                timeText.setText("");
            }
        }
    }

    private static class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView senderName, messageText, timeText;
        ImageView senderProfilePicture;

        ReceivedMessageHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.received_message_body);
            timeText = itemView.findViewById(R.id.received_message_time);
            senderProfilePicture = itemView.findViewById(R.id.profilePicture);
        }

        void bind(Message message) {
            if (message.getText() != null) {
                messageText.setText(message.getText());
            } else {
                messageText.setText("");
            }

            if (message.getTimestamp() > 0) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
                String formattedDate = dateFormat.format(new Date(message.getTimestamp()));
                timeText.setText(formattedDate);
            } else {
                timeText.setText("");
            }
        }
    }
}
