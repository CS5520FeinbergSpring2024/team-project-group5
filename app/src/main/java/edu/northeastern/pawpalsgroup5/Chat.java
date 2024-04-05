package edu.northeastern.pawpalsgroup5;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import edu.northeastern.pawpalsgroup5.models.Message;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Chat extends AppCompatActivity {
    private RecyclerView chatRecyclerView;
    private EditText messageInput;

    private DatabaseReference chatMessagesRef;
    private String currentUserId;

    private List<Message> messageList = new ArrayList<>();
    private ChatMessageAdapter messageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        currentUserId = Objects.requireNonNull(auth.getCurrentUser()).getUid();
        // TODO: pass the chatId when starting this Chat activity using intent
        String chatId = getIntent().getStringExtra("chatId");

        chatMessagesRef = FirebaseDatabase.getInstance().getReference().child("chats").child(chatId).child("messages");

        initializeUI();

        loadMessages();
    }

    private void initializeUI() {
        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        messageInput = findViewById(R.id.messageInput);
        Button sendButton = findViewById(R.id.sendButton);

        messageAdapter = new ChatMessageAdapter(messageList, currentUserId);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setAdapter(messageAdapter);

        sendButton.setOnClickListener(v -> sendMessage());
    }

    private void sendMessage() {
        String messageText = messageInput.getText().toString();
        if (!messageText.isEmpty()) {
            Map<String, Object> messageData = new HashMap<>();
            messageData.put("senderId", currentUserId);
            messageData.put("text", messageText);
            messageData.put("timestamp", ServerValue.TIMESTAMP);

            chatMessagesRef.push().setValue(messageData).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // clear out input box if sent
                    messageInput.setText("");
                } else {
                    Toast.makeText(Chat.this, "Cannot send message", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void loadMessages() {
        chatMessagesRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                messageList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Message message = snapshot.getValue(Message.class);
                    messageList.add(message);
                }
                messageAdapter.notifyDataSetChanged();
                chatRecyclerView.scrollToPosition(messageList.size() - 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Chat.this, "Cannot load messages", Toast.LENGTH_SHORT).show();
            }
        });
    }
}