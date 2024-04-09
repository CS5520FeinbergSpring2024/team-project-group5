package edu.northeastern.pawpalsgroup5;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import edu.northeastern.pawpalsgroup5.models.Message;

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
        String chatId = getIntent().getStringExtra("chatId");
        String otherUserName = getIntent().getStringExtra("otherUserName");
        String otherUserProfilePicUrl = getIntent().getStringExtra("otherUserProfilePic");

        initializeUI(otherUserName, otherUserProfilePicUrl);
        if (chatId != null) {
            chatMessagesRef = FirebaseDatabase.getInstance().getReference().child("chats").child(chatId).child("messages");
            loadMessages();
        }
    }

    private void initializeUI(String otherUserName, String otherUserProfilePicUrl) {
        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        messageInput = findViewById(R.id.messageInput);
        Button sendButton = findViewById(R.id.sendButton);

        TextView userNameText = findViewById(R.id.userNameText);
        ImageView userProfilePicture = findViewById(R.id.userProfilePicture);

        userNameText.setText(otherUserName);
        if (otherUserProfilePicUrl != null && !otherUserProfilePicUrl.isEmpty()) {
            Picasso.get().load(otherUserProfilePicUrl).placeholder(R.drawable.user_default).into(userProfilePicture);
        } else {
            userProfilePicture.setImageResource(R.drawable.user_default);
        }

        messageAdapter = new ChatMessageAdapter(messageList, currentUserId);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setAdapter(messageAdapter);

        sendButton.setOnClickListener(v -> sendMessage());
    }


    private void sendMessage() {
        String messageText = messageInput.getText().toString().trim();
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