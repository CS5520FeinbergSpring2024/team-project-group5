package edu.northeastern.pawpalsgroup5.models;

public class Post {
    private String description;
    private int likes;
    private String picture;
    private long timestamp;
    private String userId;
    private String postId;
    private String username;
    private String profilePicture;

    public Post() {
    }

    public Post(String description, int likes, String picture, long timestamp, String userId,String postId, String username, String profilePicture) {
        this.description = description;
        this.likes = likes;
        this.picture = picture;
        this.timestamp = timestamp;
        this.userId = userId;
        this.postId = postId;
        this.username = username;
        this.profilePicture = profilePicture;
    }

    public String getDescription() {
        return description;
    }

    public int getLikes() {
        return likes;
    }

    public String getPicture() {
        return picture;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getUserId() {
        return userId;
    }
    public String getUsername() {
        return username;
    }
    public String getPostId() {return postId;}
    public String getProfilePicture(){return profilePicture;}

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
    public void setPostId(String postId) {
        this.postId = postId;
    }
}
