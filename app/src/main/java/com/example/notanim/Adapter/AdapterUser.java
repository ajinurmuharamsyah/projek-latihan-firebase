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
import com.example.notanim.Filter.FilterUser;
import com.example.notanim.Model.ModelAnimeList;
import com.example.notanim.Model.ModelUser;
import com.example.notanim.ProfilActivity;
import com.example.notanim.R;
import com.example.notanim.UpdateAnimeActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterUser extends RecyclerView.Adapter<AdapterUser.HolderUser> implements Filterable {
    private Context context;
    public ArrayList<ModelUser> userArrayList, filterList;
    private FilterUser filter;

    public AdapterUser(Context context, ArrayList<ModelUser> userArrayList) {
        this.context = context;
        this.userArrayList = userArrayList;
        this.filterList = userArrayList;
    }

    @NonNull
    @Override
    public HolderUser onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout
        View view = LayoutInflater.from(context).inflate(R.layout.row_user,parent,false);
        return new AdapterUser.HolderUser(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderUser holder, int position) {

        //get data
        final ModelUser modelUser = userArrayList.get(position);
        String nameuser = modelUser.getName();
        String emailuser = modelUser.getEmail();
        String timestamp = modelUser.getTimestamp();
        String online1 = modelUser.getOnline();
        String uid = modelUser.getUid();
        String usertype = modelUser.getUsertype();

        holder.Name.setText(nameuser);

        if (online1.equals("true")){
            holder.Online.setVisibility(View.VISIBLE);
            holder.offline.setVisibility(View.INVISIBLE);
        }else {
            holder.offline.setVisibility(View.VISIBLE);
            holder.Online.setVisibility(View.INVISIBLE);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProfilActivity.class);
                intent.putExtra("uid",uid);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userArrayList.size();
    }

    @Override
    public Filter getFilter() {
        if (filter==null){
            filter = new FilterUser(filterList, this);
        }
        return filter;
    }

    public class HolderUser extends RecyclerView.ViewHolder {
        TextView Name,Online,offline;
        public HolderUser(@NonNull View itemView) {
            super(itemView);
            Name = itemView.findViewById(R.id.username);
            Online = itemView.findViewById(R.id.onltv);
            offline = itemView.findViewById(R.id.offtv);
        }
    }
}
