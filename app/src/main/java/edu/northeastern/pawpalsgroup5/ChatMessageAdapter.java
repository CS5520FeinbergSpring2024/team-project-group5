package edu.northeastern.pawpalsgroup5;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import edu.northeastern.pawpalsgroup5.models.Message;
import java.util.List;
import java.util.Date;
import java.util.Locale;
import java.text.SimpleDateFormat;

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
            return 1; // sent message
        } else {
            return 2; // received message
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        // sent message
        if (viewType == 1) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.sent_message_item, parent, false);
            return new SentMessageHolder(view);
        } else { // received message
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
            messageText.setText(message.getText());
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
            String formattedDate = dateFormat.format(new Date(message.getTimestamp()));
            timeText.setText(formattedDate);
        }
    }

    private static class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText;

        ReceivedMessageHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.received_message_body);
            timeText = itemView.findViewById(R.id.received_message_time);
        }

        void bind(Message message) {
            messageText.setText(message.getText());
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
            String formattedDate = dateFormat.format(new Date(message.getTimestamp()));
            timeText.setText(formattedDate);
        }
    }
}
