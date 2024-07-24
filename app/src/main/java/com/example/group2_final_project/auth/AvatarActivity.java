package com.example.group2_final_project.auth;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.group2_final_project.MainActivity;
import com.example.group2_final_project.R;
import com.example.group2_final_project.databinding.ActivityAvatarBinding;
import com.example.group2_final_project.helpers.SweetAlert;
import com.example.group2_final_project.onboarding.Onboarding;
import com.example.group2_final_project.user.Dashboard;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class AvatarActivity extends AppCompatActivity {
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    private static final String[] CAMERA_PERMISSIONS = {Manifest.permission.CAMERA};
    private PreviewView cameraView;
    private ImageView capturedImageView;
    private ImageCapture imageCapture;
    private File outputDirectory;
    private User user;
    private FirebaseAuth firebaseAuth;
    private FirebaseStorage firebaseStorage;
    private FirebaseUser loggedInUser;
    private static final int PICK_IMAGE_REQUEST = 2;
    ActivityAvatarBinding activityAvatarBinding;
    SweetAlert sweetAlert;

    private Object profilePicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityAvatarBinding = ActivityAvatarBinding.inflate(getLayoutInflater());
        View view = activityAvatarBinding.getRoot();
        setContentView(view);
        sweetAlert = new SweetAlert(this);
        firebaseStorage = FirebaseStorage.getInstance();

        getUserDetail();
        capturedImageView = activityAvatarBinding.capturedImageView;
        if (checkPermissions()) {
            initializeCamera();
        } else {
            requestPermissions();
        }

        outputDirectory = getOutputDirectory();
        setupClickListeners();
    }

    private void setupClickListeners() {
        activityAvatarBinding.buttonCaptureImage.setOnClickListener(view -> captureImage());
        activityAvatarBinding.buttonChooseImage.setOnClickListener(view -> openFileChooser());
        activityAvatarBinding.buttonRetakePhoto.setOnClickListener(view -> showImageChooserView());
        activityAvatarBinding.buttonUploadPicture.setOnClickListener(view -> uploadImageToFirebase());
        activityAvatarBinding.logOut.setOnClickListener(view -> logOutOuser());
    }

    private void logOutOuser() {
        firebaseAuth.signOut();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }


    private void captureImage() {
        File photoFile = createImageFile(outputDirectory);
        ImageCapture.OutputFileOptions outputFileOptions =
                new ImageCapture.OutputFileOptions.Builder(photoFile).build();


        imageCapture.takePicture(outputFileOptions, getMainExecutor(), new ImageCapture.OnImageSavedCallback() {
            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                displayCapturedImage(photoFile);
                profilePicture = photoFile;
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                Log.e("CameraX", "Error capturing image: " + exception.getMessage(), exception);
            }
        });
    }

    private void displayCapturedImage(File photoFile) {
        // Display the captured image in the ImageView
        capturedImageView.setImageURI(Uri.fromFile(photoFile));
        capturedImageView.setScaleX(-1f);
        showImageCapturedView();
    }

    private void showImageCapturedView() {
        activityAvatarBinding.layoutPictureChoosen.setVisibility(View.VISIBLE);
        activityAvatarBinding.layoutPictureChooser.setVisibility(View.GONE);
    }

    private void showImageChooserView() {
        activityAvatarBinding.layoutPictureChoosen.setVisibility(View.GONE);
        activityAvatarBinding.layoutPictureChooser.setVisibility(View.VISIBLE);
    }

    private void initializeCamera() {
        cameraView = activityAvatarBinding.cameraPreview;
        ListenableFuture<ProcessCameraProvider> cameraProviderListenableFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderListenableFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderListenableFuture.get();
                bindCameraPreview(cameraProvider);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private boolean checkPermissions() {
        for (String permission : CAMERA_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

//request camera permission to capture image
    private void requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this, CAMERA_PERMISSIONS, CAMERA_PERMISSION_REQUEST_CODE);
        }
    }

    // Bind camera preview to the layout
    private void bindCameraPreview(ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder().build();
        CameraSelector cameraSelector = new CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_FRONT).build();
        preview.setSurfaceProvider(cameraView.getSurfaceProvider());
        imageCapture = new ImageCapture.Builder().build();
        cameraProvider.unbindAll();
        cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);
    }

    // Create a file to store the captured image
    private File createImageFile(File baseFolder) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(System.currentTimeMillis());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File imageFile = new File(baseFolder, imageFileName + ".jpg");
        return imageFile;
    }

    // Get the directory to store the captured image
    private File getOutputDirectory() {
        File mediaDir = new File(getExternalMediaDirs()[0], "CameraX");
        mediaDir.mkdirs();
        return mediaDir;
    }

    // Handle the result of permission requests of camera
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            // Check if all permissions are granted
            boolean allPermissionsGranted = true;
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }

            if (allPermissionsGranted) {
                initializeCamera();
            }
        }
    }

    private void openFileChooser() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                capturedImageView.setImageBitmap(bitmap);
                showImageCapturedView();

                profilePicture = bitmap;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void getUserDetail() {
        firebaseAuth = FirebaseAuth.getInstance();
        loggedInUser = firebaseAuth.getCurrentUser();
        activityAvatarBinding.textGreeting.setText("Welcome " + loggedInUser.getDisplayName() + "!");
    }

    private void uploadImageToFirebase() {
        sweetAlert.showLoader("Hold tight while the profile picture is being uploaded.");

        if (loggedInUser != null) {
            String uid = loggedInUser.getUid();
            StorageReference storageRef = firebaseStorage.getReference().child("avatars").child(uid);

            if (profilePicture instanceof File) {

                File file = (File) profilePicture;
                Uri fileUri = Uri.fromFile(file);

                storageRef.putFile(fileUri)
                        .addOnSuccessListener(taskSnapshot -> {
                            // Handle success
                            Log.d("FirebaseStorage", "Image uploaded to Firebase Storage");
                            handleUploadSuccess(storageRef);
                        }).addOnFailureListener(e -> {
                            Log.e("FirebaseStorage", "Error uploading image: " + e.getMessage(), e);
                        });
                ;
            } else if (profilePicture instanceof Bitmap) {
                // Upload the bitmap (image selected from gallery)
                Bitmap bitmap = (Bitmap) profilePicture;
                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                // Compress the bitmap to JPEG format
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data = baos.toByteArray();

                storageRef.putBytes(data)
                        .addOnSuccessListener(taskSnapshot -> {
                            // Handle success
                            Log.d("FirebaseStorage", "Image uploaded to Firebase Storage");
                            handleUploadSuccess(storageRef);
                        }).addOnFailureListener(e -> {
                            // Handle failure
                            Log.e("FirebaseStorage", "Error uploading image: " + e.getMessage(), e);
                        });
                ;
            }
        }
    }

    private void handleUploadSuccess(StorageReference storageRef) {
        // Get the download URL of the uploaded file
        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            // Update the user's avatar URL in Firebase Authentication (if applicable)
            updateAvatarInFirebaseAuth(uri.toString());
        }).addOnFailureListener(e -> {
            // Handle failure to get download URL
            Log.e("FirebaseStorage", "Error getting download URL: " + e.getMessage(), e);
        });
    }


    private void updateAvatarInFirebaseAuth(String avatarUrl) {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setPhotoUri(Uri.parse(avatarUrl))
                    .build();

            currentUser.updateProfile(profileUpdates)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            sweetAlert.hideLoader();

                            new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE).setTitleText("Awesome").setContentText("Your profile picture is uploaded!")
                                    .setConfirmText("Continue")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            Intent dashboardIntent = new Intent(AvatarActivity.this, Dashboard.class);
                                            startActivity(dashboardIntent);
                                            finish();
                                        }
                                    }).show();
                        } else {
                            Log.e("FirebaseAuth", "Error updating user profile: " + task.getException(), task.getException());
                        }
                    });
        }
    }
}