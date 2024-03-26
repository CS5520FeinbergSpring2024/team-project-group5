package edu.northeastern.pawpalsgroup5.models;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User {
    private String age;
    private String breed;
    private String description;
    private String petName;
    private Map<String, Boolean> chatRefs;
    private List<String>  followers;
    private List<String> following;
    private String picture;
    private String username;

    public User() {
        this.chatRefs = new HashMap<>();
        this.followers = new ArrayList<>();
        this.following = new ArrayList<>();
    }

    public User(String age, String breed, String description, String petName) {
        this();
        this.age = age;
        this.breed = breed;
        this.description = description;
        this.petName = petName;
    }

    public User(String age, String breed, String description, String petName,
                Map<String, Boolean> chatRefs, List<String> followers,
                List<String> following, String picture, String username) {
        this.age = age;
        this.breed = breed;
        this.description = description;
        this.petName = petName;
        this.chatRefs = chatRefs;
        this.followers = followers;
        this.following = following;
        this.picture = picture;
        this.username = username;
    }


    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPetName() {
        return petName;
    }

    public void setPetName(String petName) {
        this.petName = petName;
    }

    public Map<String, Boolean> getChatRefs() {
        return chatRefs;
    }

    public void setChatRefs(Map<String, Boolean> chatRefs) {
        this.chatRefs = chatRefs;
    }

    public List<String> getFollowers() {
        return followers;
    }

    public void setFollowers(List<String> followers) {
        this.followers = followers;
    }

    public List<String>getFollowing() {
        return following;
    }

    public void setFollowing(List<String> following) {
        this.following = following;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

