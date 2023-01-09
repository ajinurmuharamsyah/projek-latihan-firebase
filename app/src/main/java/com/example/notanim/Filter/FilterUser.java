package com.example.notanim.Filter;

import android.widget.Filter;

import com.example.notanim.Adapter.AdapterAnimeList;
import com.example.notanim.Adapter.AdapterUser;
import com.example.notanim.Model.ModelAnimeList;
import com.example.notanim.Model.ModelUser;

import java.util.ArrayList;

public class FilterUser extends Filter {
    private ArrayList<ModelUser> filterList;
    private AdapterUser adapterUser;

    public FilterUser(ArrayList<ModelUser> filterList, AdapterUser adapterUser) {
        this.filterList = filterList;
        this.adapterUser = adapterUser;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();
        //validate data for search query
        if (constraint !=null && constraint.length()>0){
            //change to upper case, to make case insensitive
            constraint = constraint.toString().toUpperCase();
            //store our filtered list
            ArrayList<ModelUser> filteredModels = new ArrayList<>();

            for (int i=0; i<filterList.size(); i++){
                //check, search by title and catagory
                if (filterList.get(i).getName().toUpperCase().contains(constraint)||
                        filterList.get(i).getName().toLowerCase().contains(constraint)){
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
        adapterUser.userArrayList = (ArrayList<ModelUser>) results.values;
        //refersh adapter
        adapterUser.notifyDataSetChanged();
    }
}
