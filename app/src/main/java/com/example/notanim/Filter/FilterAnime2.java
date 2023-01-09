package com.example.notanim.Filter;

import android.widget.Filter;

import com.example.notanim.Adapter.AdapterAnimeList;
import com.example.notanim.Adapter.AdapterAnimeList2;
import com.example.notanim.Model.ModelAnimeList;
import com.example.notanim.Model.ModelAnimeList2;

import java.util.ArrayList;

public class FilterAnime2 extends Filter {
    private ArrayList<ModelAnimeList2> filterList;
    private AdapterAnimeList2 adapterAnimeList;

    public FilterAnime2(ArrayList<ModelAnimeList2> filterList, AdapterAnimeList2 adapterAnimeList) {
        this.filterList = filterList;
        this.adapterAnimeList = adapterAnimeList;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();
        //validate data for search query
        if (constraint !=null && constraint.length()>0){
            //change to upper case, to make case insensitive
            constraint = constraint.toString().toUpperCase();
            //store our filtered list
            ArrayList<ModelAnimeList2> filteredModels = new ArrayList<>();

            for (int i=0; i<filterList.size(); i++){
                //check, search by title and catagory
                if (filterList.get(i).getAnimeName().toUpperCase().contains(constraint)||
                        filterList.get(i).getAnimeName().toLowerCase().contains(constraint)){
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
        adapterAnimeList.animeListArrayList = (ArrayList<ModelAnimeList2>) results.values;
        //refersh adapter
        adapterAnimeList.notifyDataSetChanged();
    }
}
