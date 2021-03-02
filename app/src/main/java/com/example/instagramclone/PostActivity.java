package com.example.instagramclone;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

public class PostActivity extends AppCompatActivity {

    private Uri imageUri;
    String myUri = "";

    private StorageTask uploadTask;
    StorageReference uploadImageReference;

    ImageView img_Shut,img_Add;
    TextView txt_Send;
    EditText edt_Post_Info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        img_Shut = findViewById(R.id.close_post);
        img_Add = findViewById(R.id.add_image_post);
        txt_Send = findViewById(R.id.txt_send);
        edt_Post_Info = findViewById(R.id.edt_post_info);
        //Storage
        uploadImageReference = FirebaseStorage.getInstance().getReference("Posts");

        img_Shut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PostActivity.this , MainPageActivity.class ));
                finish();
            }
        });

        txt_Send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UploadImage();
            }
        });
        CropImage.activity().setAspectRatio(1,1).start(PostActivity.this);

    }

    private String GetFileExtension(Uri uri)
    {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }


    private void UploadImage() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Gonderiliyor...");
        progressDialog.show();
        //resim yukleme kismi
        if (imageUri != null){
            final StorageReference reference = uploadImageReference.child(System.currentTimeMillis()
                    + "." + GetFileExtension(imageUri));

            uploadTask = reference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return reference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful())
                    {
                        Uri downloadUri = task.getResult();
                        myUri = downloadUri.toString();

                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Posts");
                        String postId = databaseReference.push().getKey();

                        HashMap<String,Object> hashMap = new HashMap<>();
                        hashMap.put("postId",postId);
                        hashMap.put("postImage",myUri);
                        hashMap.put("postInfo",edt_Post_Info.getText().toString());
                        hashMap.put("publisher", FirebaseAuth.getInstance().getCurrentUser().getUid());

                        databaseReference.child(postId).setValue(hashMap);

                        progressDialog.dismiss();

                        startActivity(new Intent(PostActivity.this , MainPageActivity.class));
                        finish();

                    }
                    else
                    {
                        Toast.makeText(PostActivity.this, "Gonderme Basarisiz!!!", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(PostActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        else 
        {
            Toast.makeText(this, "Herhangi bir resim secilemedi!!!", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK)
        {
           CropImage.ActivityResult activityResult = CropImage.getActivityResult(data);
           imageUri = activityResult.getUri();

           img_Add.setImageURI(imageUri);
        }
        else 
        {
            Toast.makeText(this, "Resim secilemedi!!!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(PostActivity.this , MainPageActivity.class));
            finish();
        }

    }
}