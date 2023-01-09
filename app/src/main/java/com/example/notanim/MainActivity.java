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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.notanim.Adapter.AdapterAnimeList;
import com.example.notanim.Adapter.AdapterCharacterList;
import com.example.notanim.Model.ModelAnimeList;
import com.example.notanim.Model.ModelCharacter;
import com.example.notanim.Network.NetworkChangeListener;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity {

    private Button addAnimeBtn,addCharacterBtn,addBoyBtn,animetab,charactertab,boytab,addseason;
    private RelativeLayout animeRl,characterRl;
    private EditText animeSe,characterSe;
    private RecyclerView animeRv,characterRv;
    private ImageButton setting;
    private TextView charactertot,animetot;

    //firebase auth
    private FirebaseAuth mAuth;
    ShimmerFrameLayout shimmerFrameLayout1,shimmerFrameLayout2;
    //arrayList to store category
    private ArrayList<ModelAnimeList> animeArrayList;
    private ArrayList<ModelCharacter> characterArrayList;
    //adapter
    private AdapterAnimeList adapterAnimeList;
    private AdapterCharacterList adapterCharacterList;

    private long backPressedTime;
    private Toast backToast;

    Handler handler;

    final static String TAG = "ANIME_LIST_TAG";

    NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addAnimeBtn = findViewById(R.id.addAnimeFavorite);
        addCharacterBtn = findViewById(R.id.addChacterFavorite);
        shimmerFrameLayout1 = findViewById(R.id.shimmer_character);
        shimmerFrameLayout2 = findViewById(R.id.shimmer_anime);
        animetab = findViewById(R.id.animeTab);
        charactertab = findViewById(R.id.characterTab);
        animeRl = findViewById(R.id.content_anime);
        characterRl = findViewById(R.id.content_characater);
        animeSe = findViewById(R.id.searchEt);
        characterSe = findViewById(R.id.searchEt2);
        animeRv = findViewById(R.id.animeRv);
        characterRv = findViewById(R.id.characaterRv);
        setting = findViewById(R.id.setting);
        charactertot = findViewById(R.id.numbercharacter);
        animetot =  findViewById(R.id.numberanime);
//        addseason = findViewById(R.id.addSeasonBtn);
//
//        addseason.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(MainActivity.this,InputActivity.class));
//                finish();
//            }
//        });

        shimmerFrameLayout2.startShimmer();
        //init firebase auth
        mAuth = FirebaseAuth.getInstance();

        //check type user
        checktypeuser();

        showUiBeranda();
        loadCategory();

        animetab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showUiBeranda();
                loadCategory();
            }
        });

        charactertab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showUiCharacter();
                loadCharacter();
            }
        });

        addAnimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,InputAnimeActivity.class));
                finish();
            }
        });
        addCharacterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,InputCharacterActivity.class));
                finish();
            }
        });
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,SettingActivity.class));
                finish();
            }
        });

        animeSe.addTextChangedListener(new TextWatcher() {
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

        characterSe.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //called as and when user type each letter
                try {
                    adapterCharacterList.getFilter().filter(s);
                }catch (Exception e){

                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void checktypeuser() {
        //get current user
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser == null) {
            //not logged in, go to main screen
            startActivity(new Intent(this, MainActivity.class));
            finish();//finish this activity
        }
    }

    private void loadCharacter() {
        //init arraylist
        characterArrayList = new ArrayList<>();

        //get all categories form firebase > Categories
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Character");
        reference.orderByChild("uid").equalTo(mAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //clear arraylist before adding data into it
                characterArrayList.clear();
                for (DataSnapshot ds:snapshot.getChildren()){
                    //get data
                    ModelCharacter modelCharacter = ds.getValue(ModelCharacter.class);

                    //add to arraylist
                    characterArrayList.add(modelCharacter);
                    Log.d(TAG, "onDataChange: "+characterArrayList);
                }

                charactertot.setText("( "+""+characterArrayList.size()+" )");

                Collections.sort(characterArrayList, new Comparator<ModelCharacter>() {
                    @Override
                    public int compare(ModelCharacter o1, ModelCharacter o2) {
                        return o1.getCharacterName().compareTo(o2.getCharacterName());
                    }
                });

                //setup adapter
                handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        shimmerFrameLayout1.stopShimmer();
                        shimmerFrameLayout1.setVisibility(View.INVISIBLE);
                        characterRv.setVisibility(View.VISIBLE);
                    }
                },3000);

                adapterCharacterList = new AdapterCharacterList(MainActivity.this, characterArrayList);

                //set adapter to recylerview
                characterRv.setAdapter(adapterCharacterList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadCategory() {
        //init arraylist
        animeArrayList = new ArrayList<>();

        //get all categories form firebase > Categories
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Anime");
        reference.orderByChild("uid").equalTo(mAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //clear arraylist before adding data into it
                animeArrayList.clear();
                for (DataSnapshot ds:snapshot.getChildren()){
                    //get data
                    ModelAnimeList modelAnimeList = ds.getValue(ModelAnimeList.class);

                    //add to arraylist
                    animeArrayList.add(modelAnimeList);
                    Log.d(TAG, "onDataChange: "+animeArrayList);
                }

                animetot.setText("( "+""+animeArrayList.size()+" )");
                //setup adapter
                handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        shimmerFrameLayout2.stopShimmer();
                        shimmerFrameLayout2.setVisibility(View.INVISIBLE);
                        animeRv.setVisibility(View.VISIBLE);
                    }
                },3000);
                adapterAnimeList = new AdapterAnimeList(MainActivity.this, animeArrayList);

                //set adapter to recylerview
                animeRv.setAdapter(adapterAnimeList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showUiCharacter() {
        characterRl.setVisibility(View.VISIBLE);
        animeRl.setVisibility(View.INVISIBLE);

        charactertab.setTextColor(getResources().getColor(R.color.black));
        charactertab.setBackgroundResource(R.drawable.shape_selected03);

        animetab.setTextColor(getResources().getColor(R.color.white));
        animetab.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        shimmerFrameLayout1.startShimmer();
        shimmerFrameLayout1.setVisibility(View.VISIBLE);
        characterRv.setVisibility(View.INVISIBLE);
    }

    private void showUiBeranda() {
        animeRl.setVisibility(View.VISIBLE);
        characterRl.setVisibility(View.INVISIBLE);

        animetab.setTextColor(getResources().getColor(R.color.black));
        animetab.setBackgroundResource(R.drawable.shape_selected01);

        charactertab.setTextColor(getResources().getColor(R.color.white));
        charactertab.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        shimmerFrameLayout2.startShimmer();
        shimmerFrameLayout2.setVisibility(View.VISIBLE);
        animeRv.setVisibility(View.INVISIBLE);

    }

    @Override
    public void onBackPressed() {

        if (backPressedTime + 2000 > System.currentTimeMillis()){
            backToast.cancel();
            super.onBackPressed();
            return;
        }else{
            backToast = Toast.makeText(getBaseContext(), "Tekan Kembali untuk Keluar Aplikasi", Toast.LENGTH_SHORT);
            backToast.show();
        }
        backPressedTime = System.currentTimeMillis();
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
