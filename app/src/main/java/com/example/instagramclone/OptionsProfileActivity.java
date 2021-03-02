package com.example.instagramclone;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.instagramclone.Model.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

public class OptionsProfileActivity extends AppCompatActivity {

    ImageView img_Shutdown , profileImage;
    TextView txt_Save , txt_Photo;
    MaterialEditText name , username , bio;

    FirebaseUser currentUser;
    private StorageTask storageTask;
    private Uri uri;
    StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options_profile);
        //
        img_Shutdown = findViewById(R.id.shutdown_Options_profile);
        profileImage = findViewById(R.id.profile_image_Options_profile);

        txt_Save = findViewById(R.id.txt_save_Options_profile);
        txt_Photo = findViewById(R.id.txt_profil_image_Options_profile);

        name = findViewById(R.id.txt_name_Options_profile);
        username = findViewById(R.id.txt_username_Options_profile);
        bio = findViewById(R.id.txt_bio_Options_profile);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference("Downloads");

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

                name.setText(user.getFullname());
                username.setText(user.getUserName());
                bio.setText(user.getBio());

                Glide.with(getApplicationContext()).load(user.getImageurl()).into(profileImage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        img_Shutdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        txt_Photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity().setAspectRatio(1,1)
                        .setCropShape(CropImageView.CropShape.OVAL)
                        .start(OptionsProfileActivity.this);
            }
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity().setAspectRatio(1,1)
                        .setCropShape(CropImageView.CropShape.OVAL)
                        .start(OptionsProfileActivity.this);
            }
        });

        txt_Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateProfile(name.getText().toString() , username.getText().toString() , bio.getText().toString());
                startActivity(new Intent(OptionsProfileActivity.this, MainPageActivity.class));

            }
        });

    }

    private void UpdateProfile(String name, String username, String bio)
    {
        DatabaseReference updateReference = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());

        HashMap <String , Object> hashMap = new HashMap<>();
        hashMap.put("fullname" , name);
        hashMap.put("userName" , username);
        hashMap.put("bio" , bio);

        updateReference.updateChildren(hashMap);
    }

    private String GetFileExtension(Uri uri)
    {
        ContentResolver resolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        return mimeTypeMap.getExtensionFromMimeType(resolver.getType(uri));
    }

    private void DownloadImage()
    {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Yukleniyor");
        dialog.show();

        if (uri != null)
        {
            StorageReference fileReference = storageReference.child(System.currentTimeMillis()+"."+GetFileExtension(uri));

            storageTask = fileReference.putFile(uri);
            storageTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful())
                    {
                        throw  task.getException();
                    }
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful())
                    {
                        Uri downloadUri = task.getResult();
                        String myUri = downloadUri.toString();

                        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());

                        HashMap <String , Object> hashMap = new HashMap<>();
                        hashMap.put("imageurl" , ""+myUri);

                        userReference.updateChildren(hashMap);
                        dialog.dismiss();
                    }
                    else
                    {
                        Toast.makeText(OptionsProfileActivity.this, "Sectiginiz resim yuklenemedi !!!", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(OptionsProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }
        else
        {
            Toast.makeText(this, "Resim secilemedi . Tekrar deneyiniz !", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            uri = result.getUri();

            DownloadImage();
        }
        else 
        {
            Toast.makeText(OptionsProfileActivity.this, "Bir seyler yanlis gitti . Tekrar deneyiniz :(", Toast.LENGTH_SHORT).show();
        }
    }
}