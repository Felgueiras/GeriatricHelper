package com.example.rafael.appprototype.SessionsHistoryTab;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.rafael.appprototype.DataTypes.DB.Session;
import com.example.rafael.appprototype.DataTypes.Patient;
import com.example.rafael.appprototype.Main.GridSpacingItemDecoration;
import com.example.rafael.appprototype.R;

import java.util.ArrayList;
import java.util.List;


public class ShowDailyHistory extends BaseAdapter {
    private final List<Patient> patients;
    private RecyclerView recyclerView;
    private ArrayList<Patient> patientsForADate;
    private SinglePatientCard adapter;
    Context context;

    public ShowDailyHistory(Context context, List<Patient> patients) {
        this.context = context;
        this.patients = patients;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // each view is a Fragment layout that holds a Fragment with a Recycler View inside
        View gridElement = inflater.inflate(R.layout.content_patients_history, null);
        // get the date
        String currentDate = Session.getSessionDates().get(position).getDate();
        TextView dateTextView = (TextView) gridElement.findViewById(R.id.dateText);
        dateTextView.setText(currentDate);
        // get Sessions for that date
        List<Session> sessionsFromDate = Session.getSessionsFromDate(currentDate);
        patientsForADate = new ArrayList<>();
        for (Session sess : sessionsFromDate) {
            patientsForADate.add(sess.getPatient());
        }

        // fill the RecyclerView
        recyclerView = (RecyclerView) gridElement.findViewById(R.id.recycler_view);
        context = parent.getContext();
        adapter = new SinglePatientCard(context, patientsForADate);

        // create Layout
        int numbercolumns = 3;
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(context, numbercolumns);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        return gridElement;
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = context.getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    @Override
    public int getCount() {
        // get number of different dates
        return Session.getSessionDates().size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

}