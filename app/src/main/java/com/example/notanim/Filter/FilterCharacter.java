package com.example.notanim.Filter;

import android.widget.Filter;

import com.example.notanim.Adapter.AdapterAnimeList;
import com.example.notanim.Adapter.AdapterCharacterList;
import com.example.notanim.Model.ModelAnimeList;
import com.example.notanim.Model.ModelCharacter;

import java.util.ArrayList;

public class FilterCharacter extends Filter {
    private ArrayList<ModelCharacter> filterList;
    private AdapterCharacterList adapterCharacterList;

    public FilterCharacter(ArrayList<ModelCharacter> filterList, AdapterCharacterList adapterCharacterList) {
        this.filterList = filterList;
        this.adapterCharacterList = adapterCharacterList;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();
        //validate data for search query
        if (constraint !=null && constraint.length()>0){
            //change to upper case, to make case insensitive
            constraint = constraint.toString().toUpperCase();
            //store our filtered list
            ArrayList<ModelCharacter> filteredModels = new ArrayList<>();

            for (int i=0; i<filterList.size(); i++){
                //check, search by title and catagory
                if (filterList.get(i).getCharacterName().toUpperCase().contains(constraint)||
                        filterList.get(i).getCharacterName().toLowerCase().contains(constraint)){
                    //add filtered data to list
                    filteredModels.add(filterList.get(i));
                }

            }
            results.count = filteredModels.size();
            results.values = filteredModels;

        } else {
            //search filed empty, not searching, return orginal/all/complate list
            results.count = filterList.size();
            results.values = filterList;
        }

        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        adapterCharacterList.characterArrayList = (ArrayList<ModelCharacter>) results.values;
        //refersh adapter
        adapterCharacterList.notifyDataSetChanged();
    }
}
