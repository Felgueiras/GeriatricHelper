package com.example.rafael.appprototype.Patients.Recent;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.example.rafael.appprototype.R;

/**
 * Display the list of Patients to view them or select one of them.
 */
public class PatientsRecent extends Fragment {

    private final ViewPager viewPager;
    private final int page;

    public PatientsRecent(ViewPager viewPager, int position) {
        this.viewPager = viewPager;
        this.page = position;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (viewPager.getCurrentItem() == page) {
            menu.clear();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.content_patients_recent, container, false);

        // fill the GridView
        GridView gridView = (GridView) view.findViewById(R.id.patients_grid_view);

        PatientsByDayAdapter adapter = new PatientsByDayAdapter(getActivity(), this);
        gridView.setAdapter(adapter);

        return view;
    }

//    @Override
//    public void onPause() {
//        super.onPause();
//        // save the patients recylcer view state
//        RecyclerView.LayoutManager layoutManager = patientsRecyclerView.getLayoutManager();
//        if (layoutManager != null && layoutManager instanceof LinearLayoutManager) {
//            int position = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
//            if (position != -1) {
//                Constants.patientsListPosition = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
//                Log.d("Patients", "Storing position " + Constants.patientsListPosition);
//            }
//        }
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        RecyclerView.LayoutManager layoutManager = patientsRecyclerView.getLayoutManager();
//        int count = layoutManager.getChildCount();
//        Log.d("Patients", "Restoring position " + Constants.patientsListPosition);
//        if (Constants.patientsListPosition != RecyclerView.NO_POSITION && Constants.patientsListPosition < count) {
//            layoutManager.scrollToPosition(Constants.patientsListPosition);
//        }
//    }
}

