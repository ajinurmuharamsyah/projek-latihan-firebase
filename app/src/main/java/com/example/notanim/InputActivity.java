package com.example.notanim;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class InputActivity extends AppCompatActivity {

    private EditText title;
    private Button submit;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);

        title = findViewById(R.id.input);
        submit = findViewById(R.id.submitBtn);
        mAuth = FirebaseAuth.getInstance();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate();
            }
        });

    }

    private String Title;
    private void validate() {
        Title = title.getText().toString().trim();
        if (Title.isEmpty()){
            Toast.makeText(this, "not null", Toast.LENGTH_SHORT).show();
        }
        else {
            inputDate();
        }
    }

    private void inputDate() {
        //timestamp
        long timestamp = System.currentTimeMillis();

        String uid = mAuth.getUid();

        //setup data to upload, also add view count, download count while adding pdf/book
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("id",""+timestamp);
        hashMap.put("animeType",""+Title);
        hashMap.put("timestamp",timestamp);

        //db reference: DB > Books
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("AnimeType");
        reference.child(""+timestamp)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(InputActivity.this, "Successfully uploading...", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(InputActivity.this, "Failed to upload to db due to "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
