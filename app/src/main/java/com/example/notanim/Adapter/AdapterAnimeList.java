package com.example.notanim.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notanim.Convert.MyAplication;
import com.example.notanim.Filter.FilterAnime;
import com.example.notanim.Model.ModelAnimeList;
import com.example.notanim.R;
import com.example.notanim.UpdateAnimeActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterAnimeList extends RecyclerView.Adapter<AdapterAnimeList.HolderAnimeList> implements Filterable {
    private Context context;
    public ArrayList<ModelAnimeList> animeListArrayList, filterList;
    private FilterAnime filter;

    public AdapterAnimeList(Context context, ArrayList<ModelAnimeList> animeListArrayList) {
        this.context = context;
        this.animeListArrayList = animeListArrayList;
        this.filterList = animeListArrayList;
    }

    @NonNull
    @Override
    public HolderAnimeList onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout
        View view = LayoutInflater.from(context).inflate(R.layout.row_anime_list,parent,false);
        return new AdapterAnimeList.HolderAnimeList(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderAnimeList holder, int position) {

        //get data
        final ModelAnimeList modelAnimeList = animeListArrayList.get(position);
        final String Id = modelAnimeList.getId();
        final String animeName = modelAnimeList.getAnimeName();
        final String seasonAnime = modelAnimeList.getSeasonsAnime();
        String typeAnime = modelAnimeList.getTypeAnime();
        String sampulAnime = modelAnimeList.getSampul();
        String ratingAnime = modelAnimeList.getRating();
        String yearAnime = modelAnimeList.getYear();
        String airedAnime = modelAnimeList.getAiredAnime();
        long timestamp = modelAnimeList.getTimestamp();
        String uid = modelAnimeList.getUid();

        holder.Name.setText(animeName);
        holder.Rating.setText(ratingAnime);
        holder.Season.setText(seasonAnime+" "+yearAnime);
        holder.Type.setText(typeAnime);
        holder.Aired.setText(airedAnime);

        try {
            Picasso.get().load(sampulAnime).placeholder(R.drawable.ic_account_circle_black_24dp).into(holder.Sampul);
        } catch (Exception e){
            holder.Sampul.setImageResource(R.drawable.ic_account_circle_black_24dp);
        }

        holder.moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moreOptionsDialog(modelAnimeList, holder);
            }
        });

    }

    private void moreOptionsDialog(ModelAnimeList modelAnimeList, HolderAnimeList holder) {
        String animeId = modelAnimeList.getId();
        String urlImage = modelAnimeList.getSampul();
        String animeName = modelAnimeList.getAnimeName();
        //options to show in dialog
        String[] options = {"Edit","Delete"};

        //alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose Options")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which==0){
                            //Edit clicked, Open new activity to edit the book info
                            Intent intent = new Intent(context, UpdateAnimeActivity.class);
                            intent.putExtra("animeId",animeId);
                            intent.putExtra("animeName",animeName);
                            context.startActivity(intent);
                        } else {
                            //Delete clicked
                            MyAplication.deleteAnimes(context,
                                    ""+animeId,
                                    ""+animeName
                            );
                        }
                    }
                })
                .show();
    }

    @Override
    public int getItemCount() {
        return animeListArrayList.size();
    }

    @Override
    public Filter getFilter() {
        if (filter==null){
            filter = new FilterAnime(filterList, this);
        }
        return filter;
    }

    public class HolderAnimeList extends RecyclerView.ViewHolder {
        TextView Name,Season,Rating,Aired,Type;
        ImageView Sampul;
        ImageButton moreBtn;
        ProgressBar progressBar;
        public HolderAnimeList(@NonNull View itemView) {
            super(itemView);
            Name = itemView.findViewById(R.id.titleTv);
            Season = itemView.findViewById(R.id.seasonTv);
            Aired = itemView.findViewById(R.id.airedTv);
            Rating = itemView.findViewById(R.id.ratingTv);
            moreBtn = itemView.findViewById(R.id.moreBtn);
            Sampul = itemView.findViewById(R.id.sampulTv);
            Type = itemView.findViewById(R.id.typeTv);
        }
    }
}
