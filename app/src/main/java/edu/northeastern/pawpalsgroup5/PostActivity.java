package edu.northeastern.pawpalsgroup5;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

public class PostActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private ImageView postImageView;
    private EditText postDescriptionEditText;
    private Uri photoURI;
    private int likesCount;
    private static final int REQUEST_PERMISSION = 1;


    ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == AppCompatActivity.RESULT_OK && result.getData() != null) {
                    Uri selectedImage = result.getData().getData();
                    postImageView.setImageURI(selectedImage);
                }
            });

    ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == AppCompatActivity.RESULT_OK) {
                    postImageView.setImageURI(photoURI);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);


        postImageView = findViewById(R.id.postImageView);
        postDescriptionEditText = findViewById(R.id.postDescriptionEditText);
        Button selectImageButton = findViewById(R.id.selectImageButton);
        Button captureImageButton = findViewById(R.id.captureImageButton);
        Button postButton = findViewById(R.id.postButton);


        likesCount = getIntent().getIntExtra("likesCount", 0);

        selectImageButton.setOnClickListener(view -> openGallery());
        captureImageButton.setOnClickListener(view -> dispatchTakePictureIntent());
        postButton.setOnClickListener(view -> uploadPost());
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try{
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No camera app available", Toast.LENGTH_SHORT).show();
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
    }

    private void uploadPost() {
        final String description = postDescriptionEditText.getText().toString().trim();
        if (description.isEmpty()) {
            Toast.makeText(this, "Please enter a description for your post.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (photoURI != null) {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();

            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String fileName = "JPEG_" + timeStamp + "_";
            StorageReference imageRef = storageRef.child("images/" + fileName);

            imageRef.putFile(photoURI).addOnSuccessListener(taskSnapshot -> {
                imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString();
                    savePostToDatabase(imageUrl, description);
                }).addOnFailureListener(e -> {
                    Toast.makeText(PostActivity.this, "Failed to get image URL.", Toast.LENGTH_SHORT).show();
                });
            }).addOnFailureListener(e -> {
                Toast.makeText(PostActivity.this, "Image upload failed.", Toast.LENGTH_SHORT).show();
            });
        } else {
            savePostToDatabase("", description);
        }
    }

    private void savePostToDatabase(String picture, String description) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference postsRef = database.getReference("posts");

        String postId = postsRef.push().getKey();
        long timestamp = new Date().getTime();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        HashMap<String, Object> post = new HashMap<>();
        post.put("picture", picture);
        post.put("description", description);
        post.put("likes", likesCount);
        post.put("timestamp", timestamp);
        post.put("userId", userId);

        postsRef.child(postId).setValue(post).addOnSuccessListener(aVoid -> {
            Toast.makeText(PostActivity.this, "Post uploaded successfully.", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            Toast.makeText(PostActivity.this, "Failed to save post to database.", Toast.LENGTH_SHORT).show();
        });
    }
}
