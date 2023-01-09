package com.example.notanim;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.example.notanim.Adapter.AdapterAnimeList;
import com.example.notanim.Adapter.AdapterUser;
import com.example.notanim.Model.ModelUser;
import com.example.notanim.Network.NetworkChangeListener;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UserActivity extends AppCompatActivity {

    private ImageButton btnback;
    private RecyclerView userRl;
    private ShimmerFrameLayout shimmerFrameLayout;
    private EditText searchus;
    private FirebaseAuth mAuth;

    final static String TAG = "USER_LIST_TAG";

    private ArrayList<ModelUser> modelUsers;
    //adapter
    private AdapterUser adapterUser;

    Handler handler;

    NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        btnback = findViewById(R.id.backBtn);
        userRl = findViewById(R.id.usercontetRl);
        shimmerFrameLayout = findViewById(R.id.shimmer_profil);
        searchus = findViewById(R.id.searchEt2);

        mAuth = FirebaseAuth.getInstance();

        shimmerFrameLayout.startShimmer();

        loaduserall();

        searchus.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    adapterUser.getFilter().filter(s);
                }catch (Exception e){

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void loaduserall() {
        modelUsers = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //clear list before starting adding data into it
                modelUsers.clear();
                for (DataSnapshot ds: snapshot.getChildren()){
                    //get data
                    ModelUser model = ds.getValue(ModelUser.class);
                    String uid = ""+ds.child("uid").getValue();
                    if (uid.equals(mAuth.getUid())){

                    }else {
                        //add to list
                        modelUsers.add(model);
                    }

                    Log.d(TAG, "onDataChange: "+model.getUid()+" "+model.getName());
                }
                //setup adapter
                handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        shimmerFrameLayout.stopShimmer();
                        shimmerFrameLayout.setVisibility(View.INVISIBLE);
                        userRl.setVisibility(View.VISIBLE);
                    }
                },3000);
                adapterUser = new AdapterUser(UserActivity.this, modelUsers);

                //set adapter to recylerview
                userRl.setAdapter(adapterUser);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(UserActivity.this,SettingActivity.class));
        finish();
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
