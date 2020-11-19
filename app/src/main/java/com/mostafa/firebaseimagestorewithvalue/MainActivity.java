package com.mostafa.firebaseimagestorewithvalue;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 71;
    private Uri uri ;
    //Firebase
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private Bitmap bitmap;
    private ImageView imageView;
    private TextView textName, textCourse,textDuration,textRoll;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button submit,select;

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        database = FirebaseDatabase.getInstance();
        databaseReference= database.getReference();
       submit = findViewById(R.id.btnSubmit);
        select = findViewById(R.id.btnImageSelect);

        textName = findViewById(R.id.editTextName);
        textCourse= findViewById(R.id.editTextCourse);
        textDuration = findViewById(R.id.editTextDuration);
        textRoll = findViewById(R.id.editTextRoll);
        imageView = findViewById(R.id.imageView);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                String name, course, duration,image;
//                int roll;
//                roll= Integer.parseInt(textRoll.getText().toString()) ;
//                course = textCourse.getText().toString();
//                duration = textDuration.getText().toString();
//                name = textName.getText().toString();
//               //image = imageView.getText().toString();
//                Student student = new Student(course,duration,name,"image");
//                database.getReference().child(String.valueOf(roll)).setValue(student);
//                // myRef.setValue(name);
//
//                textName.setText("");
//                Toast.makeText(MainActivity.this, "insert data", Toast.LENGTH_SHORT).show();
                uploadImage();
            }
        });

        select.setOnClickListener(view -> {
            //select image from local storage
            Dexter.withActivity(MainActivity.this)
                    .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    .withListener(new PermissionListener() {
                        @Override
                        public void onPermissionGranted(PermissionGrantedResponse response) {
//                            // permission is granted, open the camera
//
                            chooseImage();

                        }

                        @Override
                        public void onPermissionDenied(PermissionDeniedResponse response) {
                            // check for permanent denial of permission
                            if (response.isPermanentlyDenied()) {
                                // navigate user to app settings
                            }
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                            token.continuePermissionRequest();
                        }
                    })
                    .withErrorListener(new PermissionRequestErrorListener() {
                        @Override
                        public void onError(DexterError error) {
                            Toast.makeText(MainActivity.this, "error !"+error, Toast.LENGTH_SHORT).show();
                        }
                    })
                    .check();

        });

    }
    private void chooseImage() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);

    }
    //now set the selected image into imageview field
    /*
     * this method for selelct function
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK  && data != null && data.getData() != null){
            uri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                imageView.setImageBitmap(bitmap);//url niy inputStream k bitmap e decode kore imageView t set kore dilam showkorar jonno ImageVIew tey
            }catch (Exception e){
                Log.d("abc", String.valueOf(e));
            }
        }
    }


    private void uploadImage() {

        if(uri != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

             //storageReference = this.storageReference.child("images/"+ UUID.randomUUID().toString());
//            storage = FirebaseStorage.getInstance();
//            storageReference = storage.getReference();
             storageReference = this.storageReference.child("images/"+ UUID.randomUUID().toString());
             storageReference.putFile(uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                   // databaseReference =databaseReference.child("student");
                                    String name, course, duration,image;
                                    int roll;
                                    roll= Integer.parseInt(textRoll.getText().toString()) ;
                                    course = textCourse.getText().toString();
                                    duration = textDuration.getText().toString();
                                    name = textName.getText().toString();
                                    image = uri.toString();
                                    Student student = new Student(course,duration,name,image);
                                    databaseReference.child("student").child(String.valueOf(roll)).setValue(student);
                                    textRoll.setText("");
                                    textCourse.setText("");
                                    textDuration.setText("");
                                    textName.setText("");
                                    textName.setText("");
                                    imageView.setImageResource(R.drawable.ic_launcher_background);
                                }
                            });
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploading Progress .... "+(int)progress+"%");
                        }
                    });
        }else {
            Toast.makeText(this, "path empty", Toast.LENGTH_SHORT).show();
        }
    }

}