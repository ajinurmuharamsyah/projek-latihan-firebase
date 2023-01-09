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
import com.example.notanim.Filter.FilterAnime2;
import com.example.notanim.Model.ModelAnimeList;
import com.example.notanim.Model.ModelAnimeList2;
import com.example.notanim.R;
import com.example.notanim.UpdateAnimeActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterAnimeList2 extends RecyclerView.Adapter<AdapterAnimeList2.HolderAnimeList> implements Filterable {
    private Context context;
    public ArrayList<ModelAnimeList2> animeListArrayList, filterList;
    private FilterAnime2 filter;

    public AdapterAnimeList2(Context context, ArrayList<ModelAnimeList2> animeListArrayList) {
        this.context = context;
        this.animeListArrayList = animeListArrayList;
        this.filterList = animeListArrayList;
    }

    @NonNull
    @Override
    public HolderAnimeList onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout
        View view = LayoutInflater.from(context).inflate(R.layout.row_anime_list2,parent,false);
        return new AdapterAnimeList2.HolderAnimeList(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderAnimeList holder, int position) {

        //get data
        final ModelAnimeList2 modelAnimeList = animeListArrayList.get(position);
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

    }


    @Override
    public int getItemCount() {
        return animeListArrayList.size();
    }

    @Override
    public Filter getFilter() {
        if (filter==null){
            filter = new FilterAnime2(filterList, this);
        }
        return filter;
    }

    public class HolderAnimeList extends RecyclerView.ViewHolder {
        TextView Name,Season,Rating,Aired,Type;
        ImageView Sampul;
        public HolderAnimeList(@NonNull View itemView) {
            super(itemView);
            Name = itemView.findViewById(R.id.titleTv);
            Season = itemView.findViewById(R.id.seasonTv);
            Aired = itemView.findViewById(R.id.airedTv);
            Rating = itemView.findViewById(R.id.ratingTv);
            Sampul = itemView.findViewById(R.id.sampulTv);
            Type = itemView.findViewById(R.id.typeTv);
        }
    }
}
