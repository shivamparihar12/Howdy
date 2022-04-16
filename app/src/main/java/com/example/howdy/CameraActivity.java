package com.example.howdy;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.video.Recording;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.example.howdy.databinding.ActivityCameraBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import kotlin.collections.ArraysKt;
import kotlin.jvm.internal.Intrinsics;

//typealias LumaListener = (luma: Double) -> Unit

public class CameraActivity extends AppCompatActivity {

    private ActivityCameraBinding binding;
    private ImageCapture imageCapture;
    private Recording recording;
    private File outPutDirectory;
    private ExecutorService cameraExecutor;
    private Uri imageUri;
    private static final String TAG = "CameraActivity";
    private static final int REQUEST_CODE_PERMISSION = 3143;
    private static final String FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS";
    private static final String PHOTO_EXTENSION = ".jpg";
    private final String[] REQUIRED_PERMISSIONS = {Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE};
    private static final int OPEN_GALLERY_REQUEST_CODE = 98675;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCameraBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (allPermissionsGranted()) {
            try {
                startCamera();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            ActivityCompat.requestPermissions(
                    this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSION);
        }

        cameraExecutor = Executors.newSingleThreadExecutor();
        outPutDirectory = getOutPutDirectory(this);


        binding.openGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Choose Image"), OPEN_GALLERY_REQUEST_CODE);
            }
        });

        binding.button.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public void onClick(View view) {
                try {
                    takePhoto();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        binding.uploadStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (imageUri != null) {
                    Log.d(TAG, "not null");
                    binding.progressCircular.setVisibility(View.VISIBLE);
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), imageUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Bitmap reducedBitmap = ImageResizer.reduceBitmapSize(bitmap, 240000);
                    final String timestamp = String.valueOf(System.currentTimeMillis());
                    String filePathName = "User/" + "todayStory" + FirebaseAuth.getInstance().getCurrentUser().getUid();


                    final int length = reducedBitmap.getByteCount();
                    ByteBuffer dst = ByteBuffer.allocate(length);
                    reducedBitmap.copyPixelsFromBuffer(dst);
                    byte[] data = dst.array();

                    StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(filePathName);
                    storageReference.putBytes(data)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).child("todayStory");
                                    HashMap hashMap = new HashMap();
                                    hashMap.put("story", imageUri);
                                    hashMap.put("timestamp", timestamp);
                                    database.setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            binding.progressCircular.setVisibility(View.GONE);
                                            Toast.makeText(CameraActivity.this, "Posted", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(CameraActivity.this, MainActivity.class));
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(CameraActivity.this, "Failed, Try Again", Toast.LENGTH_SHORT).show();
                                        }
                                    });


                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
                } else Log.d(TAG, "image uri is null");

            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void takePhoto() throws IOException {
        ImageCapture imageCapture = this.imageCapture;
        if (imageCapture == null) {
            Intrinsics.throwUninitializedPropertyAccessException("imageCapture");
        }
        if (imageCapture != null) {
            String name = (new SimpleDateFormat(FILENAME_FORMAT, Locale.US)).format(System.currentTimeMillis());
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name);
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                contentValues.put(MediaStore.Downloads.RELATIVE_PATH, "Picture/CameraX_Image");
            }

            File photoFile = File.createTempFile(FILENAME_FORMAT, PHOTO_EXTENSION, outPutDirectory);

//            ImageCapture.OutputFileOptions outputOptions = new ImageCapture.OutputFileOptions.Builder(getContentResolver(),
//                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                    contentValues)
//                    .build();

            ImageCapture.OutputFileOptions outputOptions = new ImageCapture.OutputFileOptions.Builder(photoFile)
                    .build();

            imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(this),
                    new ImageCapture.OnImageSavedCallback() {
                        @Override
                        public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                            String msg = "Photo capture succeeded:" + outputFileResults.getSavedUri();
                            imageUri = outputFileResults.getSavedUri();
                            Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();
                            Log.d(TAG, msg);
                            binding.constraintLayout.setVisibility(View.GONE);
                            binding.linearLayout.setVisibility(View.VISIBLE);
                            binding.imageView.setImageURI(outputFileResults.getSavedUri());
//                            Intent intent = new Intent(CameraActivity.this, AddStoryActivity.class);
//                            intent.putExtra("imagePath", outputFileResults.getSavedUri().toString());
//                            startActivity(intent);
//                            binding.uploadStatus.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View view) {
//
//                                }
//                            });
                        }

                        @Override
                        public void onError(@NonNull ImageCaptureException exception) {
                            Log.e(TAG, "Photo capture failed:" + exception.getMessage(), exception);
                        }
                    });
        }

    }

    private void captureVideo() {
    }

    private void startCamera() throws ExecutionException, InterruptedException {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            ProcessCameraProvider cameraProvider = null;
            try {
                cameraProvider = cameraProviderFuture.get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Preview preview = new Preview.Builder()
                    .build();
            preview.setSurfaceProvider(binding.viewFinder.getSurfaceProvider());
            imageCapture = new ImageCapture.Builder().build();
            CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

            try {
                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, preview, imageCapture);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "USe case binding failed", e);
            }

        }, ContextCompat.getMainExecutor(CameraActivity.this));
    }

    public boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(CameraActivity.this, permission) != PackageManager.PERMISSION_GRANTED)
                return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (allPermissionsGranted()) {
                try {
                    startCamera();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this,
                        "Permissions not granted by the user.",
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OPEN_GALLERY_REQUEST_CODE && data != null) {
            binding.constraintLayout.setVisibility(View.GONE);
            binding.linearLayout.setVisibility(View.VISIBLE);
            Uri uri = data.getData();
            binding.imageView.setImageURI(uri);
        }
    }

    public final File getOutPutDirectory(Context context) {
        Context context1 = context.getApplicationContext();
        File[] file = context.getExternalMediaDirs();
        File file1 = ArraysKt.firstOrNull(file);
        if (file1 != null) {
            File file2 = new File(file1, context1.getResources().getString(R.string.app_name));
            file1.mkdirs();
        } else file1 = null;

        File mediaDir = file1;
        if (mediaDir != null && mediaDir.exists()) {
            file1 = mediaDir;
        } else {
            file1 = context1.getFilesDir();
        }
        return file1;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
    }
}