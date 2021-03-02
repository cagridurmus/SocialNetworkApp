package com.example.instagramclone;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SignupActivity extends AppCompatActivity {

    EditText edt_username,edt_name,edt_email,edt_password;
    Button btn_signup;
    TextView txt_loginPage;

    FirebaseAuth mAuth;
    DatabaseReference reference;

    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        edt_username = findViewById(R.id.edt_username);
        edt_name = findViewById(R.id.edt_name);
        edt_email = findViewById(R.id.edt_email_signup);
        edt_password = findViewById(R.id.edt_password_signup);

        btn_signup = findViewById(R.id.btn_signup_activity);

        txt_loginPage = findViewById(R.id.txt_loginPage);

        mAuth = FirebaseAuth.getInstance();

        txt_loginPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignupActivity.this , LoginActivity.class));
            }
        });

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new ProgressDialog(SignupActivity.this);
                dialog.setMessage("Lutfen bekleyiniz...");
                dialog.show();

                //Degiskenler
                String username = edt_username.getText().toString();
                String name = edt_name.getText().toString();
                String email = edt_email.getText().toString();
                String password = edt_password.getText().toString();

                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) )
                {
                    Toast.makeText(SignupActivity.this, "Lutfen butun alanlari doldurugunuza emin olunuz.", Toast.LENGTH_SHORT).show();
                }
                else if(password.length()<6)
                {
                    Toast.makeText(SignupActivity.this, "Sifreniz minimum 6 karakter olmalidir!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    //Yeni kullanici kaydetme kismi
                    SignUp(username,name,email,password);

                }



            }
        });

    }

    private void SignUp(final String userName,final String fullname,String email,String password){
        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            FirebaseUser user = mAuth.getCurrentUser();

                            String userId = user.getUid();

                            reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);

                            HashMap <String,Object> hashMap = new HashMap<>();
                            hashMap.put("id",userId);
                            hashMap.put("userName",userName.toLowerCase());
                            hashMap.put("fullname",fullname);
                            hashMap.put("bio","");
                            hashMap.put("imageurl","https://firebasestorage.googleapis.com/v0/b/instagramclone-d6c8d.appspot.com/o/placeholder.jpg?alt=media&token=b02edf06-71b5-47f9-898c-a884347760fb");

                            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful())
                                    {
                                        dialog.dismiss();

                                        Intent intent =  new Intent(SignupActivity.this , LoginActivity.class) ;
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    }

                                }
                            });
                        }
                        else
                        {
                            //Eger kullanici acma basarili degilse
                            dialog.dismiss();

                            Toast.makeText(SignupActivity.this, "Kaydiniz basarisiz olmustur. Lutfen tekrar deneyiniz...", Toast.LENGTH_LONG).show();

                        }

                    }
                });

    }
}