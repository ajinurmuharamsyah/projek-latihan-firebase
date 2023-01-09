package com.example.notanim.Convert;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;
import java.util.Locale;

public class MyAplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
    }

    //created a static method to convert timestamp to proper date format, so we can use it everywhere in project, no need to rewrite again
    public static final String formatTimestamp(long timestamp){
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(timestamp);
        //format: timestamp to dd/MM/yyyy
        String date = DateFormat.format("dd/MM/yyyy", cal).toString();

        return date;
    }

    public static void deleteAnimes(Context context, String animeId, String animeName) {
        String TAG = "DELETE_ANIME_TAG";

        Log.d(TAG, "deleteAnimes: Deleting...");
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Please Waiting");
        progressDialog.setMessage("Deleting "+animeName+" ...");//e.g. Deleting Book ABC...
        progressDialog.show();

        Log.d(TAG, "deleteAnimes: Deleting from storage...");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Anime");
        reference.child(animeId)
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: Delete from db too");
                        progressDialog.dismiss();
                        Toast.makeText(context, "Anime Deleted Successfully...", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: Failed to delete from db due to ");
                        progressDialog.dismiss();
                        Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public static void deleteCharacters(Context context, String characterId, String characterAnimeName) {
        String TAG = "DELETE_CHARACTER_TAG";

        Log.d(TAG, "deleteCharacters: Deleting...");
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Please Waiting");
        progressDialog.setMessage("Deleting "+characterAnimeName+" ...");//e.g. Deleting Book ABC...
        progressDialog.show();

        Log.d(TAG, "deleteCharacters: Deleting from storage...");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Character");
        reference.child(characterId)
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: Delete from db too");
                        progressDialog.dismiss();
                        Toast.makeText(context, "Character Deleted Successfully...", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: Failed to delete from db due to ");
                        progressDialog.dismiss();
                        Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public static void loadSeason(String seasonAnime, TextView Season) {
        //get category using categoryId

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Seasons");
        reference.child(seasonAnime)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //get category
                        String season = ""+snapshot.child("season").getValue();

                        //set to category text view
                        Season.setText(season);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public static void loadType(String typeAnime, TextView Type) {
        //get category using categoryId

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("AnimeType");
        reference.child(typeAnime)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //get category
                        String animeType = ""+snapshot.child("animeType").getValue();

                        //set to category text view
                        Type.setText(animeType);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public static void loadAired(String airedAnime, TextView Aired) {
        //get category using categoryId

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Aired");
        reference.child(airedAnime)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //get category
                        String aired1 = ""+snapshot.child("aired").getValue();

                        //set to category text view
                        Aired.setText(aired1);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}
