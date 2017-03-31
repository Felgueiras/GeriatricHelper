package com.example.rafael.appprototype.Evaluations;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.DatePicker;
import android.widget.GridView;
import android.widget.ListAdapter;

import com.example.rafael.appprototype.Constants;
import com.example.rafael.appprototype.DataTypes.DB.Session;
import com.example.rafael.appprototype.Evaluations.EvaluationsHistory.ShowEvaluationsAllDays;
import com.example.rafael.appprototype.Evaluations.EvaluationsHistory.SessionsSingleDay;
import com.example.rafael.appprototype.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;


public class EvaluationsAll extends Fragment {

    private static final String BUNDLE_RECYCLER_LAYOUT = "abc";
    private ListAdapter adapter;
    private GridView gridView;

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sessions_history_grid, container, false);
        // fill the GridView
        gridView = (GridView) view.findViewById(R.id.gridView);
        adapter = new ShowEvaluationsAllDays(getActivity(), this);
        gridView.setAdapter(adapter);

        /**
         * On scroll, hide FAB.
         */

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_evaluations, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.calendar:
                DialogFragment picker = new DatePickerFragment(gridView, this);
                picker.show(getFragmentManager(), "datePicker");
        }
        return true;

    }

    /**
     * Erase a session from the PATIENT.
     *
     * @param index Session index
     */
    public void removeSession(int index) {
//        sessionsFromPatient.remove(index);
//        recyclerView.removeViewAt(index);
//        adapter.notifyItemRemoved(index);
//        adapter.notifyItemRangeChanged(index, sessionsFromPatient.size());
//        adapter.notifyDataSetChanged();
        adapter = new ShowEvaluationsAllDays(getActivity(), this);
        gridView.setAdapter(adapter);

        gridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int newState) {
                Log.d("Scroll", newState+"");
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                Log.d("Scroll", i + "-" + i1 + "-" + i2);
            }
        });
    }

    /**
     * This is a method for Fragment.
     * You can do the same in onCreate or onRestoreInstanceState
     */
//    @Override
//    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
//        super.onViewStateRestored(savedInstanceState);
//
//        if (savedInstanceState != null) {
//            Parcelable savedRecyclerLayoutState = savedInstanceState.getParcelable(BUNDLE_RECYCLER_LAYOUT);
//            gridView..onRestoreInstanceState(savedRecyclerLayoutState);
//        }
//    }
//
//    @Override
//    public void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        outState.putParcelable(BUNDLE_RECYCLER_LAYOUT, patientsRecyclerView.getLayoutManager().onSaveInstanceState());
//    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if (Constants.sessionsGridViewIndex != 0) {
            gridView.smoothScrollToPosition(Constants.sessionsGridViewIndex);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Constants.sessionsGridViewIndex = gridView.getFirstVisiblePosition();
        Log.d("Grid", Constants.sessionsGridViewIndex + "");
    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {


        private final GridView gridView;
        private final EvaluationsAll fragment;

        public DatePickerFragment(GridView gridView, EvaluationsAll evaluationsHistoryGrid) {
            this.gridView = gridView;
            this.fragment = evaluationsHistoryGrid;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }


        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            Calendar c = Calendar.getInstance();
            c.set(year, month, day);

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            String formattedDate = sdf.format(c.getTime());

            // get Sessions from that date
            List<Session> sessionsFromDate = Session.getSessionsFromDate(c.getTime());
            Log.d("Date", sessionsFromDate.size() + "");
            /**
             * Filter by date.
             */
            ListAdapter adapter = new SessionsSingleDay(getActivity(), fragment, c.getTime());
            gridView.setAdapter(adapter);

//                sessionsFromPatient.remove(index);
//                recyclerView.removeViewAt(index);
//                adapter.notifyItemRemoved(index);
//                adapter.notifyItemRangeChanged(index, sessionsFromPatient.size());
//                adapter.notifyDataSetChanged();
        }
    }
}