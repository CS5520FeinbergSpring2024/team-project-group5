package edu.northeastern.pawpalsgroup5;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.northeastern.pawpalsgroup5.models.Message;

public class ChatHistoryActivity extends AppCompatActivity {
    private ChatHistoryAdapter chatHistoryAdapter;
    private final List<Message> messages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_history);

        RecyclerView chatHistoryRecyclerView = findViewById(R.id.chatHistoryRecyclerView);
        chatHistoryRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatHistoryAdapter = new ChatHistoryAdapter(messages, this, true);
        chatHistoryRecyclerView.setAdapter(chatHistoryAdapter);

        fetchChatHistories();
    }

    private void fetchChatHistories() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String currentUserId = currentUser.getUid();
            db.collection("userChats").document(currentUserId).get().addOnSuccessListener(userChatSnapshot -> {
                List<String> chatIds = (List<String>) userChatSnapshot.get("chatIds");
                if (chatIds != null) {
                    for (String chatId : chatIds) {
                        db.collection("chats").document(chatId).collection("messages")
                                .orderBy("timestamp", Query.Direction.DESCENDING)
                                .limit(1)
                                .get()
                                .addOnSuccessListener(messagesQueryDocumentSnapshots -> {
                                    for (DocumentSnapshot messageSnapshot : messagesQueryDocumentSnapshots) {
                                        Message lastMessage = messageSnapshot.toObject(Message.class);
                                        if (lastMessage != null) {
                                            messages.add(lastMessage);
                                            chatHistoryAdapter.notifyDataSetChanged();
                                        }
                                    }
                                })
                                .addOnFailureListener(e -> Log.e("ChatHistoryActivity", "Error fetching last message for chat " + chatId, e));
                    }
                }
            }).addOnFailureListener(e -> Log.e("ChatHistoryActivity", "Error fetching user chats", e));
        }
    }
}



