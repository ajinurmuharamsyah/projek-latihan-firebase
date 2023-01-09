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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class UpdateCharacterActivity extends AppCompatActivity {
    private TextView judulAnimeTv,genderTv;
    private EditText nameTv,ageTv,dayTv,monthTv;
    private ImageButton btnBack;
    private ImageView sampul;
    private Button submitBtn;

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

    private StorageReference stReff;
    private StorageTask strTask;

    //progress dialog
    private ProgressDialog progressDialog;

    private String characterId, charaterName;

    //arraylist to hold season,animetype,aired
    private ArrayList<String> modelAnime,modelIdAnime,modelGender,modelIdGender;

    NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_character);
        judulAnimeTv = findViewById(R.id.animeTv);
        genderTv = findViewById(R.id.genderTv);
        ageTv = findViewById(R.id.ageEt);
        dayTv = findViewById(R.id.dayTv);
        monthTv = findViewById(R.id.monthTv);
        nameTv = findViewById(R.id.animeEt);
        btnBack = findViewById(R.id.backBtn);
        submitBtn = findViewById(R.id.submitBtn);
        sampul = findViewById(R.id.sampul);

        //init firebase auth
        mAuth = FirebaseAuth.getInstance();

        stReff = FirebaseStorage.getInstance().getReference("Character");

        //get data from intent
        Intent intent = getIntent();
        characterId = intent.getStringExtra("characterid");
        charaterName = intent.getStringExtra("charactername");

        loadjudulAnime();
        loadGender();

        loadInfoCharacter();

        //setup progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        judulAnimeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickTitleAnime();
            }
        });

        genderTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickGender();
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();
            }
        });
        sampul.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePickDialog();
            }
        });
    }

    private void loadInfoCharacter() {
        Log.d(TAG, "loadInfoCharacter: Loading Character Anime info");

        //db reference to load book info
        DatabaseReference refbooks = FirebaseDatabase.getInstance().getReference("Character");
        refbooks.child(characterId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //get book info
                        String age = ""+snapshot.child("age").getValue();
                        String animeName = ""+snapshot.child("animeName").getValue();
                        String charactername = ""+snapshot.child("characterName").getValue();
                        String day = ""+snapshot.child("day").getValue();
                        String gender = ""+snapshot.child("gender").getValue();
                        String month = ""+snapshot.child("month").getValue();
                        String sampul1 = ""+snapshot.child("image_uri").getValue();

                        //set to view
                        judulAnimeTv.setText(animeName);
                        genderTv.setText(gender);
                        ageTv.setText(age);
                        dayTv.setText(day);
                        monthTv.setText(month);
                        nameTv.setText(charactername);

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

    private String selectedAnime,selectedIdAnime,selectedGender,selectedIdGender;

    private void pickTitleAnime() {
        Log.d(TAG, "pickAnime: showing pick Anime");
        //get string array of categories from arraylist
        String[] animeArray = new String[modelAnime.size()];
        for (int i = 0; i< modelAnime.size(); i++){
            animeArray[i] = modelAnime.get(i);
        }


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Title Anime")
                .setItems(animeArray, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //handle item click
                        //get clicked item from list
                        selectedAnime = modelAnime.get(which);
                        selectedIdAnime = modelIdAnime.get(which);

                        //set to category textview
                        judulAnimeTv.setText(selectedAnime);

                        Log.d(TAG,"onClick: Select Anime: "+selectedIdAnime+" "+selectedAnime);
                    }
                })
                .show();
    }

    private void pickGender() {
        Log.d(TAG, "pickGender: showing pick Gender");
        //get string array of categories from arraylist
        String[] genderArray = new String[modelGender.size()];
        for (int i = 0; i< modelGender.size(); i++){
            genderArray[i] = modelGender.get(i);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Gender")
                .setItems(genderArray, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //handle item click
                        //get clicked item from list
                        selectedGender = modelGender.get(which);
                        selectedIdGender = modelIdGender.get(which);
                        //set to category textview
                        genderTv.setText(selectedGender);

                        Log.d(TAG,"onClick: Select Gender: "+selectedIdGender+" "+selectedGender);
                    }
                })
                .show();
    }

    private void loadjudulAnime() {
        Log.d(TAG, "loadjudulAnime: Loading Anime");
        modelAnime = new ArrayList<>();
        modelIdAnime = new ArrayList<>();

        //db reference to load categories... db > categories
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Anime");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                modelAnime.clear();//clear before adding data
                modelIdAnime.clear();
                for (DataSnapshot ds: snapshot.getChildren()){
                    //get id and title of category
                    String AnimeId = ""+ds.child("id").getValue();
                    String judulAnime = ""+ ds.child("animeName").getValue();

                    Collections.sort(modelAnime, new Comparator<String>() {
                        @Override
                        public int compare(String o1, String o2) {
                            return o1.compareTo(o2);
                        }
                    });

                    //add to respective arraylist
                    modelIdAnime.add(AnimeId);
                    modelAnime.add(judulAnime);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadGender() {
        Log.d(TAG, "loadGender: Loading Gender");
        modelGender = new ArrayList<>();
        modelIdGender = new ArrayList<>();

        //db reference to load categories... db > categories
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Gender");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                modelGender.clear();//clear before adding data
                modelIdGender.clear();
                for (DataSnapshot ds: snapshot.getChildren()){
                    //get id and title of category
                    String GenderId = ""+ds.child("id").getValue();
                    String Gender = ""+ ds.child("gender").getValue();

                    //add to respective arraylist
                    modelIdGender.add(GenderId);
                    modelGender.add(Gender);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private String NameTv,AgeTv,DayTv,MonthTv;

    private void validateData() {
        NameTv = nameTv.getText().toString().trim();
        AgeTv = ageTv.getText().toString().trim();
        DayTv = dayTv.getText().toString().trim();
        MonthTv = monthTv.getText().toString().trim();

        if (TextUtils.isEmpty(NameTv)){
            Toast.makeText(this, "Enter Title Anime...!", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(selectedAnime)){
            Toast.makeText(this, "Please selected title anime", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(selectedGender)){
            Toast.makeText(this, "Please selected gender", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(AgeTv)){
            Toast.makeText(this, "Enter Age...!", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(DayTv)){
            Toast.makeText(this, "Enter Day...!", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(MonthTv)){
            Toast.makeText(this, "Enter Month...!", Toast.LENGTH_SHORT).show();
        }else {
            if (image_uri == null){
                //upload data without image
                updateCharacter("");
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
                                Toast.makeText(UpdateCharacterActivity.this, "update", Toast.LENGTH_SHORT).show();
                            }
                        },500);

                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful());
                        String uploadedImageUrl = ""+uriTask.getResult();

                        Log.d(TAG, "onSuccess: Update Image URL: "+uploadedImageUrl);

                        updateCharacter(uploadedImageUrl);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UpdateCharacterActivity.this, "Failed uploding "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateCharacter(String uploadedImageUrl) {
        Log.d(TAG, "updateCharacter: Upload anime info");
        progressDialog.setMessage("Update Character anime info...");
        progressDialog.show();


        //setup data to add in db
        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put("characterName",""+NameTv);
        hashMap.put("animeName",""+selectedAnime);
        hashMap.put("gender",""+selectedGender);
        hashMap.put("age",""+AgeTv);
        hashMap.put("day",""+DayTv);
        hashMap.put("month",""+MonthTv);
        if (image_uri == null){
            hashMap.put("image_uri",""+uploadedImageUrl);
        }else {
            hashMap.put("image_uri","");
        }

        //update data to db
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Character");
        reference.child(characterId)
                .updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressDialog.dismiss();
                        Log.d(TAG,"onSuccess: Info Anime Uploaded...");
                        Toast.makeText(UpdateCharacterActivity.this, "Info Anime Uploaded...", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Log.d(TAG,"onFailure: Failed to upload db due to "+e.getMessage());
                        Toast.makeText(UpdateCharacterActivity.this, "Failed to upload db due to "+e.getMessage(), Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(UpdateCharacterActivity.this, "Failed upload image", Toast.LENGTH_SHORT).show();
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
