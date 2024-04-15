package edu.northeastern.pawpalsgroup5;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import edu.northeastern.pawpalsgroup5.models.Post;

public class PostActivity extends AppCompatActivity {

    private ImageView postImageView;
    private EditText postDescriptionEditText;
    private Uri photoURI;
    private String currentPhotoPath;

    static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        postImageView = findViewById(R.id.postImageView);
        postDescriptionEditText = findViewById(R.id.postDescriptionEditText);

        Button selectImageButton = findViewById(R.id.selectImageButton);
        Button captureImageButton = findViewById(R.id.captureImageButton);
        Button postButton = findViewById(R.id.postButton);

        selectImageButton.setOnClickListener(view -> openGallery());
        captureImageButton.setOnClickListener(view -> dispatchTakePictureIntent());
        postButton.setOnClickListener(view -> sendPost());
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, 2);
    }

    private void checkPermissionsAndOpenCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_IMAGE_CAPTURE);
        } else {
            openCamera();
        }
    }


    private void dispatchTakePictureIntent() {
        checkPermissionsAndOpenCamera();
    }


    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
                if (photoFile != null) {
                    photoURI = FileProvider.getUriForFile(this, "edu.northeastern.pawpalsgroup5.fileprovider", photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            } catch (IOException ex) {
                Toast.makeText(this, "Error occurred while creating the file", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_IMAGE_CAPTURE && grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        } else {
            Toast.makeText(this, "Both camera and storage permissions are needed to take pictures", Toast.LENGTH_SHORT).show();
        }
    }


    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void sendPost() {
        String description = postDescriptionEditText.getText().toString().trim();
        if (photoURI != null && !description.isEmpty()) {
            uploadImageToFirebase(photoURI, description);
        } else {
            Toast.makeText(this, "Please select an image and write a description.", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadImageToFirebase(Uri imageUri, String description) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("postImages/" + UUID.randomUUID().toString());
        storageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
            storageRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser != null) {

                    DatabaseReference databaseRefUser = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid());

                    databaseRefUser.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                String pictureUrl = dataSnapshot.child("picture").getValue(String.class);
                                String username = dataSnapshot.child("petName").getValue(String.class);
                                DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("posts");
                                String postId = databaseRef.push().getKey();

                                long timestamp = System.currentTimeMillis();
                                Post post = new Post(description, 0, downloadUri.toString(), timestamp, currentUser.getUid(), postId, username, pictureUrl);

                                databaseRef.child(postId).child("description").setValue(post.getDescription());
                                databaseRef.child(postId).child("likes").setValue(post.getLikes());
                                databaseRef.child(postId).child("picture").setValue(post.getPicture());
                                databaseRef.child(postId).child("timestamp").setValue(post.getTimestamp());
                                databaseRef.child(postId).child("userId").setValue(post.getUserId());
                                databaseRef.child(postId).child("postId").setValue(postId);
                                databaseRef.child(postId).child("username").setValue(post.getUsername());
                                databaseRef.child(postId).child("profilePicture").setValue(post.getProfilePicture());

                                Toast.makeText(PostActivity.this, "Post uploaded successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                Log.d("FirebaseData", "No picture found for this user.");
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.w("FirebaseData", "loadPost:onCancelled", databaseError.toException());
                            // Handle possible errors
                        }
                    });


                } else {
                    Toast.makeText(PostActivity.this, "User authentication is required", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(e -> Toast.makeText(PostActivity.this, "Failed to get image URL", Toast.LENGTH_SHORT).show());
        }).addOnFailureListener(e -> Toast.makeText(PostActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show());
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            postImageView.setImageURI(photoURI);
        } else if (requestCode == 2 && resultCode == RESULT_OK) {
            photoURI = data.getData();
            postImageView.setImageURI(photoURI);
        }
    }
}


