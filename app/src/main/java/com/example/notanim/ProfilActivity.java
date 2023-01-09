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
import android.widget.TextView;
import android.widget.Toast;

import com.example.notanim.Adapter.AdapterAnimeList;
import com.example.notanim.Adapter.AdapterAnimeList2;
import com.example.notanim.Convert.MyAplication;
import com.example.notanim.Model.ModelAnimeList;
import com.example.notanim.Model.ModelAnimeList2;
import com.example.notanim.Model.ModelCharacter;
import com.example.notanim.Network.NetworkChangeListener;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProfilActivity extends AppCompatActivity {
    private TextView username,emailtv,usertype1,member,totalanime;
    private ShimmerFrameLayout shimmerFrameLayout;
    private RecyclerView animelistRl;
    private FirebaseAuth mAuth;
    private String useruid;
    private ImageButton btnback;
    private EditText searchanime;

    private ArrayList<ModelAnimeList2> modelAnimeList2s;
    //adapter
    private AdapterAnimeList2 adapterAnimeList;

    Handler handler;

    final static String TAG = "ANIME_LIST_TAG";

    NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);
        
        username = findViewById(R.id.nameTv);
        emailtv = findViewById(R.id.emailTv);
        usertype1 = findViewById(R.id.accountTypeTv);
        member = findViewById(R.id.memberDateTv);
        totalanime = findViewById(R.id.animeCountTv);
        shimmerFrameLayout = findViewById(R.id.shimmer_anime);
        animelistRl = findViewById(R.id.animelistRl);
        btnback = findViewById(R.id.backBtn);
        searchanime = findViewById(R.id.searchEt2);

        mAuth = FirebaseAuth.getInstance();
        //get data from intent
        Intent intent = getIntent();
        useruid = intent.getStringExtra("uid");
        
        shimmerFrameLayout.startShimmer();
        
        loadinfo();
        loadanimeall();

        searchanime.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //called as and when user type each letter
                try {
                    adapterAnimeList.getFilter().filter(s);
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

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ProfilActivity.this,UserActivity.class));
        fileList();
    }

    private void loadanimeall() {
        //init arraylist
        modelAnimeList2s = new ArrayList<>();

        //get all categories form firebase > Categories
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Anime");
        reference.orderByChild("uid").equalTo(useruid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //clear arraylist before adding data into it
                modelAnimeList2s.clear();
                for (DataSnapshot ds:snapshot.getChildren()){
                    //get data
                    ModelAnimeList2 modelAnimeList = ds.getValue(ModelAnimeList2.class);

                    //add to arraylist
                    modelAnimeList2s.add(modelAnimeList);
                    Log.d(TAG, "onDataChange: "+modelAnimeList2s);
                }
                //setup adapter
                handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        shimmerFrameLayout.stopShimmer();
                        shimmerFrameLayout.setVisibility(View.INVISIBLE);
                        animelistRl.setVisibility(View.VISIBLE);
                    }
                },3000);

                totalanime.setText(""+modelAnimeList2s.size());

                adapterAnimeList = new AdapterAnimeList2(ProfilActivity.this, modelAnimeList2s);

                //set adapter to recylerview
                animelistRl.setAdapter(adapterAnimeList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadinfo() {
        //db reference: DB > Books
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.orderByChild("uid").equalTo(useruid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    //get data from db
                    String name = "" + ds.child("name").getValue();
                    String accountType = "" + ds.child("usertype").getValue();
                    String email = "" + ds.child("email").getValue();
                    String timestamp = ""+ds.child("timestamp").getValue();

                    //format date to dd//MM/yyyy
                    String formattedDate = MyAplication.formatTimestamp(Long.parseLong(timestamp));

                    //set data to ui
                    username.setText(name);
                    emailtv.setText(email);
                    usertype1.setText(accountType);
                    member.setText(formattedDate);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
