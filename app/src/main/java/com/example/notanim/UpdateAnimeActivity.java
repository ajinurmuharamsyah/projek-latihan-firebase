package com.example.notanim;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.notanim.Network.NetworkChangeListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class UpdateAnimeActivity extends AppCompatActivity {

    private ImageButton backBtn;
    private TextView seasonTv,typeTv,airedTv;
    private ImageView sampul;
    private Button submit;
    private EditText judulTv,ratingTv,yearTv;

    NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    //TAG for debugging
    private static final String TAG = "TESTING_INPUT_TAG";

    private static final int STORAGE_REQUEST_CODE = 200;

    //images pick constants
    private static final int IMAGE_PICK_GALLERY_CODE = 400;

    //permission arrays
    private String [] storagePermission;

    //images picked uri
    private Uri image_uri;

    //firebase auth
    private FirebaseAuth mAuth;

    //progress dialog
    private ProgressDialog progressDialog;

    private StorageReference stReff;
    private StorageTask strTask;

    private String animeId, animeName;

    //arraylist to hold season,animetype,aired
    private ArrayList<String> modelSeasons,modelIdSeason,modelAnimeType,modelIdAnimeType,modelAired,modelIdAired;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_anime);
        backBtn = findViewById(R.id.backBtn);
        seasonTv = findViewById(R.id.seasonTv);
        typeTv = findViewById(R.id.typeTv);
        airedTv = findViewById(R.id.airedTv);
        sampul = findViewById(R.id.sampul);
        judulTv = findViewById(R.id.animeEt);
        yearTv = findViewById(R.id.yearEt);
        ratingTv = findViewById(R.id.ratingEt);
        submit = findViewById(R.id.submitBtn);

        //init firebase auth
        mAuth = FirebaseAuth.getInstance();

        //get data from intent
        Intent intent = getIntent();
        animeId = intent.getStringExtra("animeId");
        animeName = intent.getStringExtra("animeName");

        stReff = FirebaseStorage.getInstance().getReference("Anime");

        storagePermission = new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE};

        //setup progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        loadSeasonCategory();
        loadAnimeType();
        loadAired();
        loadallAnime();
//        validateData();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        seasonTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickSeason();
            }
        });

        typeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                picktype();
            }
        });

        airedTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickAired();
            }
        });

        sampul.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //pick image
                showImagePickDialog();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();
            }
        });
    }

    private void loadallAnime() {
        Log.d(TAG, "loadallAnime: Loading Anime info");

        //db reference to load book info
        DatabaseReference refbooks = FirebaseDatabase.getInstance().getReference("Anime");
        refbooks.child(animeId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //get book info
                        String Aired = ""+snapshot.child("airedAnime").getValue();
                        String seasons = ""+snapshot.child("seasonsAnime").getValue();
                        String typeAnime = ""+snapshot.child("typeAnime").getValue();
                        String animeName = ""+snapshot.child("animeName").getValue();
                        String rating = ""+snapshot.child("rating").getValue();
                        String year = ""+snapshot.child("year").getValue();
                        String sampul1 = ""+snapshot.child("sampul").getValue();

                        //set to view
                        seasonTv.setText(seasons);
                        typeTv.setText(typeAnime);
                        airedTv.setText(Aired);
                        judulTv.setText(animeName);
                        yearTv.setText(year);
                        ratingTv.setText(rating);

                        try {
                            Picasso.get().load(sampul1).placeholder(R.drawable.ic_image_black_24dp).into(sampul);

                        } catch (Exception e){
                            sampul.setImageResource(R.drawable.ic_image_black_24dp);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private String selectedSeason="",selectedIdSeason="",selectedAired="",selectedIdAired="",selectedType="",selectedIdType="";

    private void pickSeason() {
        Log.d(TAG, "pickSeason: showing pick season");
        //get string array of categories from arraylist
        String[] seasonArray = new String[modelSeasons.size()];
        for (int i = 0; i< modelSeasons.size(); i++){
            seasonArray[i] = modelSeasons.get(i);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Seasons")
                .setItems(seasonArray, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //handle item click
                        //get clicked item from list
                        selectedSeason = modelSeasons.get(which);
                        selectedIdSeason = modelIdSeason.get(which);
                        //set to category textview
                        seasonTv.setText(selectedSeason);

                        Log.d(TAG,"onClick: Select Season: "+selectedIdSeason+" "+selectedSeason);
                    }
                })
                .show();
    }

    private void picktype() {
        Log.d(TAG, "picktype: showing pick type");
        //get string array of categories from arraylist
        String[] typeArray = new String[modelAnimeType.size()];
        for (int i = 0; i< modelAnimeType.size(); i++){
            typeArray[i] = modelAnimeType.get(i);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Type")
                .setItems(typeArray, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //handle item click
                        //get clicked item from list
                        selectedType = modelAnimeType.get(which);
                        selectedIdType = modelIdAnimeType.get(which);
                        //set to category textview
                        typeTv.setText(selectedType);

                        Log.d(TAG,"onClick: Select Type: "+selectedIdType+" "+selectedType);
                    }
                })
                .show();
    }

    private void pickAired() {
        Log.d(TAG, "pickAired: showing pick aired");
        //get string array of categories from arraylist
        String[] airedArray = new String[modelAired.size()];
        for (int i = 0; i< modelAired.size(); i++){
            airedArray[i] = modelAired.get(i);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Aired")
                .setItems(airedArray, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //handle item click
                        //get clicked item from list
                        selectedAired = modelAired.get(which);
                        selectedIdAired = modelIdAired.get(which);
                        //set to category textview
                        airedTv.setText(selectedAired);

                        Log.d(TAG,"onClick: Select Aired "+selectedIdAired+" "+selectedAired);
                    }
                })
                .show();
    }

    private void loadAired() {
        Log.d(TAG, "loadAired: Loading aired");
        modelAired = new ArrayList<>();
        modelIdAired = new ArrayList<>();

        //db reference to load categories... db > categories
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Aired");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                modelAired.clear();//clear before adding data
                modelIdAired.clear();
                for (DataSnapshot ds: snapshot.getChildren()){
                    //get id and title of category
                    String airedId = ""+ds.child("id").getValue();
                    String aired = ""+ ds.child("aired").getValue();

                    //add to respective arraylist
                    modelIdAired.add(airedId);
                    modelAired.add(aired);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadAnimeType() {
        Log.d(TAG, "loadAnimeType: Loading anime type");
        modelAnimeType = new ArrayList<>();
        modelIdAnimeType = new ArrayList<>();

        //db reference to load categories... db > categories
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("AnimeType");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                modelAnimeType.clear();//clear before adding data
                modelIdAnimeType.clear();
                for (DataSnapshot ds: snapshot.getChildren()){
                    //get id and title of category
                    String typeId = ""+ds.child("id").getValue();
                    String animeType = ""+ ds.child("animeType").getValue();

                    //add to respective arraylist
                    modelIdAnimeType.add(typeId);
                    modelAnimeType.add(animeType);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadSeasonCategory() {
        Log.d(TAG,"loadSeasonCategory: Loading season categories");
        modelSeasons = new ArrayList<>();
        modelIdSeason = new ArrayList<>();

        //db reference to load categories... db > categories
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Seasons");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                modelSeasons.clear();//clear before adding data
                modelIdSeason.clear();
                for (DataSnapshot ds: snapshot.getChildren()){
                    //get id and title of category
                    String seasonId = ""+ds.child("id").getValue();
                    String seasons = ""+ ds.child("season").getValue();

                    //add to respective arraylist
                    modelIdSeason.add(seasonId);
                    modelSeasons.add(seasons);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private String Anime,Rating,Year,season;

    private void validateData() {
        Anime = judulTv.getText().toString().trim();
        Rating = ratingTv.getText().toString().trim();
        Year = yearTv.getText().toString().trim();

        if (TextUtils.isEmpty(Anime)){
            Toast.makeText(this, "Enter Title Anime...!", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(selectedSeason)){
            Toast.makeText(this, "Please selected season anime", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(Year)){
            Toast.makeText(this, "Enter Year anime...!", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(selectedType)){
            Toast.makeText(this, "Pelese selected Type Anime", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(selectedAired)){
            Toast.makeText(this, "Pelese selected Aired", Toast.LENGTH_SHORT).show();
        }else if (Rating.isEmpty()){
            Toast.makeText(this, "Enter Rating Anime...!", Toast.LENGTH_SHORT).show();
        }else {
            if (image_uri == null){
                //upload data without image
                updateAnime("");
            }else {
                // upload data with image
                updateImage();
            }
        }
    }

    private void updateImage() {
        Log.d(TAG, "uploadImage: Uploading Image...");
        //show progress
        progressDialog.setMessage("Updating Image");
        progressDialog.show();

        StorageReference reference =  stReff.child(System.currentTimeMillis()+ " "+ MimeTypeMap.getFileExtensionFromUrl(image_uri.toString()));

        strTask = reference.putFile(image_uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(UpdateAnimeActivity.this, "update", Toast.LENGTH_SHORT).show();
                            }
                        },500);

                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful());
                        String uploadedImageUrl = ""+uriTask.getResult();

                        Log.d(TAG, "onSuccess: Update Image URL: "+uploadedImageUrl);

                        updateAnime(uploadedImageUrl);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UpdateAnimeActivity.this, "Failed uploding "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        //timestamp
//        long timestamp = System.currentTimeMillis();
//
//        //image path and name, use uid to replace previous
//        String filePathAndName = "Sampul/"+mAuth.getUid();
//
//        //storage reference
//        StorageReference reference = FirebaseStorage.getInstance().getReference(filePathAndName);
//        reference.putFile(image_uri)
//                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                        Log.d(TAG, "onSuccess: image uploaded");
//                        Log.d(TAG, "onSuccess: Getting url of uploaded image");
//                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
//                        while (!uriTask.isSuccessful());
//                        String uploadedImageUrl = ""+uriTask.getResult();
//
//                        Log.d(TAG, "onSuccess: Uploaded Image URL: "+uploadedImageUrl);
//
//                        uploadAnime(uploadedImageUrl);
//
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Log.d(TAG, "onFailure: Failed to upload image due to "+e.getMessage());
//                progressDialog.dismiss();
//                Toast.makeText(InputAnimeActivity.this, "Failed to upload image due to "+e.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    private void updateAnime(String uploadedImageUrl) {
        Log.d(TAG, "updateAnime: Upload anime info");
        progressDialog.setMessage("Update anime info...");
        progressDialog.show();

        //setup data to add in db
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("animeName",""+Anime);
        hashMap.put("seasonsAnime",""+selectedSeason);
        hashMap.put("year",""+Year);
        hashMap.put("typeAnime",""+selectedType);
        hashMap.put("airedAnime",""+selectedAired);
        hashMap.put("rating",""+Rating);
        if (image_uri == null){
            hashMap.put("image_uri","");
        }else {
            hashMap.put("image_uri",""+uploadedImageUrl);
        }

        //update data to db
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Anime");
        reference.child(animeId)
                .updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressDialog.dismiss();
                        Log.d(TAG,"onSuccess: Info Anime Updated...");
                        Toast.makeText(UpdateAnimeActivity.this, "Info Anime Updated...", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Log.d(TAG,"onFailure: Failed to update db due to "+e.getMessage());
                        Toast.makeText(UpdateAnimeActivity.this, "Failed to update db due to "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showImagePickDialog() {
        //options to display in dialog
        String[] options = {"Gallery"};
        //dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Image")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //handle click
                        if (which == 0){
                            //gallery clicked
                            if (checkStoragePermission()){
                                //storage permissions allowed
                                pickFromGallery();

                            }else {
                                //not allowed, request
                                requestStoragePermission();
                            }
                        } else {
                            Toast.makeText(UpdateAnimeActivity.this, "Failed upload image", Toast.LENGTH_SHORT).show();
                        }

                    }
                })
                .show();
    }

    private void pickFromGallery(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    private boolean checkStoragePermission(){
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestStoragePermission(){
        ActivityCompat.requestPermissions(this, storagePermission, STORAGE_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case STORAGE_REQUEST_CODE:{
                if (grantResults.length>0){
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (storageAccepted){
                        //permission allowed
                        pickFromGallery();
                    }
                } else {
                    Toast.makeText(this, "Storage Permission are necessary", Toast.LENGTH_SHORT).show();
                }
            }
            break;
        }
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            if (requestCode == IMAGE_PICK_GALLERY_CODE && data != null && data.getData() != null){
                //get picked Image
                image_uri = data.getData();

                //set on ImageView
                sampul.setImageURI(image_uri);

            }
        }
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
