package edu.northeastern.pawpalsgroup5.models;
import java.util.Map;

public class Chat {
    private Map<String, Boolean> participants;
    private Map<String, Message> messages;

    public Chat() {
    }

    public Chat(Map<String, Boolean> participants, Map<String, Message> messages) {
        this.participants = participants;
        this.messages = messages;
    }

    public Map<String, Boolean> getParticipants() {
        return participants;
    }

    public void setParticipants(Map<String, Boolean> participants) {
        this.participants = participants;
    }

    public Map<String, Message> getMessages() {
        return messages;
    }

    public void setMessages(Map<String, Message> messages) {
        this.messages = messages;
    }
}
