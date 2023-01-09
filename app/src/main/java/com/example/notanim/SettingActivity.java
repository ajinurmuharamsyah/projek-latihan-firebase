package com.example.notanim;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.notanim.Adapter.AdapterAnimeList;
import com.example.notanim.Adapter.AdapterUser;
import com.example.notanim.Model.ModelCharacter;
import com.example.notanim.Model.ModelUser;
import com.example.notanim.Network.NetworkChangeListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class SettingActivity extends AppCompatActivity {

    private RelativeLayout emailrl,passwordrl,logoutrl,userRl;
    private FirebaseAuth mAuth;
    private ImageButton btnBack;


    NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        emailrl = findViewById(R.id.EmailRl);
        passwordrl = findViewById(R.id.passwordRl);
        logoutrl = findViewById(R.id.logoutRl);
        btnBack = findViewById(R.id.backBtn);
        userRl = findViewById(R.id.UserRl);

        mAuth = FirebaseAuth.getInstance();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        userRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingActivity.this,UserActivity.class));
                finish();
            }
        });

        emailrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingActivity.this,ChangeEmailActivity.class));
                finish();
            }
        });

        passwordrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingActivity.this,ChangePasswordActivity.class));
                finish();
            }
        });

        logoutrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialoglogout();
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(SettingActivity.this,MainActivity.class));
        finish();
    }

    private void makeMeOffline() {
        mAuth = FirebaseAuth.getInstance();
        //after logging in, make user online

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("online","false");

        //update value to db
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(mAuth.getUid()).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //update succesfully
                mAuth.signOut();
                checkuser();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //failed updating
                Toast.makeText(SettingActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkuser() {
        FirebaseUser user = mAuth.getCurrentUser();
        if(user == null){
            startActivity(new Intent(SettingActivity.this,LoginActivity.class));
            finish();
        }
    }

    private void showDialoglogout() {
        AlertDialog.Builder logout = new AlertDialog.Builder(SettingActivity.this);
        logout.setTitle("Logout")
                .setMessage("Are you sure you are logout of this account?")
                .setIcon(R.mipmap.ic_launcher)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        makeMeOffline();
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }).show();
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
