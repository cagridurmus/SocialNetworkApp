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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    EditText edt_email_login,edt_password_login;

    Button btn_login;

    TextView txt_signupPage;

    FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edt_email_login=findViewById(R.id.edt_email_login);
        edt_password_login=findViewById(R.id.edt_password_login);

        btn_login=findViewById(R.id.btn_login_activity);

        //Firebase
        mAuth=FirebaseAuth.getInstance();

        txt_signupPage=findViewById(R.id.txt_signupPage);
        txt_signupPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this , SignupActivity.class));
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog dialog =new ProgressDialog(LoginActivity.this);
                dialog.setMessage("Giris yapiliyor.Lutfen bekleyiniz...");
                dialog.show();

                String emailLogin = edt_email_login.getText().toString();
                String passwordLogin = edt_password_login.getText().toString();
                
                if (TextUtils.isEmpty(emailLogin) || TextUtils.isEmpty(passwordLogin))
                {
                    Toast.makeText(LoginActivity.this, "Gerekli alanlar bos birakilamaz!!! ", Toast.LENGTH_LONG).show();
                }
                else
                {
                    //Giris yapma kismi
                    mAuth.signInWithEmailAndPassword(emailLogin,passwordLogin)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful())
                                    {
                                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());

                                        reference.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                dialog.dismiss();
                                                //Eger giris basariliysa anasayfaya yonlendirme kismi
                                                Toast.makeText(LoginActivity.this, "Hesabiniz basariyla olusturuldu...", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(LoginActivity.this ,  MainPageActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);
                                                finish();
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                dialog.dismiss();
                                            }
                                        });
                                    }
                                    else
                                    {
                                        dialog.dismiss();

                                        Toast.makeText(LoginActivity.this, "Giris basarisiz oldu.Tekrar deneyiniz...", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
            }
        });
    }
}