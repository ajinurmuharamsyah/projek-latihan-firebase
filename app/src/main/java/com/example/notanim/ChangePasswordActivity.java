package com.example.notanim;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.notanim.Network.NetworkChangeListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class ChangePasswordActivity extends AppCompatActivity {
    private ImageButton backBtn;
    private EditText passwordold,passwordnew,confpassword;
    private Button submit;

    NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        passwordold = findViewById(R.id.passwordold);
        passwordnew = findViewById(R.id.passwordnew);
        confpassword = findViewById(R.id.passwordconfirm);
        backBtn = findViewById(R.id.backBtn);
        submit = findViewById(R.id.submitBtn);

        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Mohon Di tunggu");
        progressDialog.setCanceledOnTouchOutside(false);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputPassword();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChangePasswordActivity.this,SettingActivity.class));
                finish();
            }
        });
    }

    private String oldPassword,newPassword,confirmPassword;

    private void inputPassword() {
        oldPassword = passwordold.getText().toString().trim();
        newPassword = passwordnew.getText().toString().trim();
        confirmPassword = confpassword.getText().toString().trim();

        if (TextUtils.isEmpty(oldPassword)){
            Toast.makeText(ChangePasswordActivity.this, "Masukan Password lama", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(newPassword)){
            Toast.makeText(ChangePasswordActivity.this, "Masukan Password Baru", Toast.LENGTH_SHORT).show();
            return;
        }
        if (newPassword.length()<6){
            Toast.makeText(ChangePasswordActivity.this, "Password Minimal 6 karakter", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!newPassword.equals(confirmPassword)){
            Toast.makeText(ChangePasswordActivity.this, "Password tidak sama", Toast.LENGTH_SHORT).show();
            return;
        }

        updatePassword(oldPassword,newPassword);
    }

    private void updatePassword(final String oldPassword, final String newPassword) {
        progressDialog.setMessage("Update Password Account...");
        progressDialog.show();

        //get current user
        final FirebaseUser user = mAuth.getCurrentUser();

        //before changing password re-authenticate the user
        AuthCredential authCredential = EmailAuthProvider.getCredential(user.getEmail(),oldPassword);
        user.reauthenticate(authCredential).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //successfully authenticated, begin update
                user.updatePassword(newPassword).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressDialog.dismiss();
                        HashMap<String ,Object> hashMap = new HashMap<>();
                        hashMap.put("password",""+newPassword);
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                        reference.child(mAuth.getUid()).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(ChangePasswordActivity.this, "Password Updated...", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent (ChangePasswordActivity.this,SettingActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ChangePasswordActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(ChangePasswordActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //authenticated failed, show reason
                progressDialog.dismiss();
                Toast.makeText(ChangePasswordActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStart() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListener, filter);
        super.onStart();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(networkChangeListener);
        super.onStop();
    }
}
