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
import com.example.notanim.Filter.FilterCharacter;
import com.example.notanim.Model.ModelAnimeList;
import com.example.notanim.Model.ModelCharacter;
import com.example.notanim.R;
import com.example.notanim.UpdateAnimeActivity;
import com.example.notanim.UpdateCharacterActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterCharacterList extends RecyclerView.Adapter<AdapterCharacterList.HolderCharacter> implements Filterable {
    private Context context;
    public ArrayList<ModelCharacter> characterArrayList, filterList;
    private FilterCharacter filter;

    public AdapterCharacterList(Context context, ArrayList<ModelCharacter> characterArrayList) {
        this.context = context;
        this.characterArrayList = characterArrayList;
        this.filterList = characterArrayList;
    }

    @NonNull
    @Override
    public HolderCharacter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout
        View view = LayoutInflater.from(context).inflate(R.layout.row_character_list,parent,false);
        return new AdapterCharacterList.HolderCharacter(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderCharacter holder, int position) {

        //get data
        final ModelCharacter modelCharacter = characterArrayList.get(position);
        final String Id = modelCharacter.getId();
        final String animeName = modelCharacter.getAnimeName();
        String characterName = modelCharacter.getCharacterName();
        String characterGender = modelCharacter.getGender();
        String sampulAnime = modelCharacter.getImage_uri();
        String characterAge = modelCharacter.getAge();
        String characterMonth = modelCharacter.getMonth();
        String characterday = modelCharacter.getDay();
        long timestamp = modelCharacter.getTimestamp();
        String uid = modelCharacter.getUid();

        holder.Name.setText(characterName);
        holder.Anime.setText(animeName);
        holder.Gender.setText(characterGender);
        holder.Birthday.setText(characterMonth+" "+characterday);
        holder.Age.setText(characterAge+" Year");

        try {
            Picasso.get().load(sampulAnime).placeholder(R.drawable.ic_image_blackw_24dp).into(holder.Sampul);
        } catch (Exception e){

            holder.Sampul.setImageResource(R.drawable.ic_image_blackw_24dp);
        }
        holder.moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moreDialogOptions(modelCharacter,holder);
            }
        });
    }

    private void moreDialogOptions(ModelCharacter modelCharacter, HolderCharacter holder) {
        String characterid = modelCharacter.getId();
        String urlImage = modelCharacter.getImage_uri();
        String characterAnimeName = modelCharacter.getCharacterName();
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
                            Intent intent = new Intent(context, UpdateCharacterActivity.class);
                            intent.putExtra("characterid",characterid);
                            intent.putExtra("charactername",characterAnimeName);
                            context.startActivity(intent);
                        } else {
                            //Delete clicked
                            MyAplication.deleteCharacters(context,
                                    ""+characterid,
                                    ""+characterAnimeName
                            );
                        }
                    }
                })
                .show();
    }

    @Override
    public int getItemCount() {
        return characterArrayList.size();
    }

    @Override
    public Filter getFilter() {
        if (filter==null){
            filter = new FilterCharacter(filterList, this);
        }
        return filter;
    }

    public class HolderCharacter extends RecyclerView.ViewHolder {
        TextView Name,Anime,Gender,Birthday,Age;
        ImageView Sampul;
        ImageButton moreBtn;
        public HolderCharacter(@NonNull View itemView) {
            super(itemView);
            Name = itemView.findViewById(R.id.nameTv);
            Age = itemView.findViewById(R.id.ageTv);
            Anime = itemView.findViewById(R.id.animeTv);
            Gender = itemView.findViewById(R.id.genderTv);
            Birthday = itemView.findViewById(R.id.birthdayTv);
            moreBtn = itemView.findViewById(R.id.moreBtn);
            Sampul = itemView.findViewById(R.id.sampulRl);
        }
    }
}
