package com.example.rafael.appprototype.DrugPrescription.Beers;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.example.rafael.appprototype.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BeersCriteriaFragment extends Fragment {


    ArrayList<BeersCriteria> beersGeneral = new ArrayList<>();


    ExpandableListAdapterBeers listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<TherapeuticCategoryEntry>> listDataChild;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_start_criteria, container, false);

        // get the listview
        expListView = (ExpandableListView) v.findViewById(R.id.lvExp);

        // preparing list data
        beersGeneral = BeersCriteria.getBeersData();
        prepareListData();

        listAdapter = new ExpandableListAdapterBeers(getActivity(), listDataHeader, listDataChild);

        // setting list adapter
        expListView.setAdapter(listAdapter);

        // Listview Group click listener
        expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                return false;
            }
        });

        // Listview Group expanded listener
        expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {

            }
        });

        // Listview Group collasped listener
        expListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {


            }
        });

        // Listview on child click listener
        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {

                return false;
            }
        });
        // Inflate the layout for this fragment
        return v;
    }

    /*
     * Preparing the list data
     */
    private void prepareListData() {
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();

        // Adding child data
        for (int i = 0; i < beersGeneral.size(); i++) {
            BeersCriteria s = beersGeneral.get(i);
            // header
            listDataHeader.add(s.getOrganSystem());
            // child
            List<TherapeuticCategoryEntry> child = new ArrayList<>();
            for (TherapeuticCategoryEntry entry : s.getEntries()) {
                child.add(entry);
            }
            listDataChild.put(listDataHeader.get(i), child); // Header, Child data
        }
    }

}