package com.example.instagramclone;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

public class AddStoryActivity extends AppCompatActivity {

    private Uri mImageUri;
    String myUrl= "";
    private StorageTask uploadTask;

    StorageReference uploadStorageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_story);

        uploadStorageReference = FirebaseStorage.getInstance().getReference("story");

        CropImage.activity()
                .setAspectRatio(9,16)
                .start(AddStoryActivity.this);
    }

    private String GetFileExtension (Uri uri)
    {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void PublishStory ()
    {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Gonderiliyor");
        progressDialog.show();

        if (mImageUri != null)
        {
            final StorageReference imageStorageReference = uploadStorageReference.child(System.currentTimeMillis()
                                                    + "." + GetFileExtension(mImageUri));

            uploadTask = imageStorageReference.putFile(mImageUri);

            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot , Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful())
                    {
                        throw task.getException();
                    }
                    return imageStorageReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful())
                    {
                        Uri downloadUri = task.getResult();

                        myUrl = downloadUri.toString();

                        String myId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                        DatabaseReference storyReference = FirebaseDatabase.getInstance().getReference("Story").child(myId);

                        String storyid = storyReference.push().getKey();
                        long finishTime = System.currentTimeMillis() +  86400000; //bir gun 86400000 mili saniye

                        HashMap <String , Object> hashMap = new HashMap<>();
                        hashMap.put("imageurl" ,myUrl);
                        hashMap.put("starttime" , ServerValue.TIMESTAMP);
                        hashMap.put("endtime" ,finishTime);
                        hashMap.put("storyid" ,storyid);
                        hashMap.put("userid" ,myId);

                        storyReference.child(storyid).setValue(hashMap);
                        progressDialog.dismiss();

                        finish();
                    }
                    else
                    {
                        Toast.makeText(AddStoryActivity.this, "Hikaye ekleme basarisiz oldu!", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AddStoryActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        else 
        {
            Toast.makeText(this, "Resim secerken bir hatayla karsilasildi.\nTekrar Deneyiniz ...", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode ==  CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode ==  RESULT_OK)
        {
            CropImage.ActivityResult activityResult = CropImage.getActivityResult(data);
            mImageUri = activityResult.getUri();
            
            PublishStory();
        }
        else 
        {
            Toast.makeText(this, "Hikaye eklerken bir hatayla karsilasildi.\nTekrar Deneyiniz ...", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(AddStoryActivity.this , MainPageActivity.class));
            finish();
        }
    }
}