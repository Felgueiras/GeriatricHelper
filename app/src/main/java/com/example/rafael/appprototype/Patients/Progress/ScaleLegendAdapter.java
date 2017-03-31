package com.example.rafael.appprototype.Patients.Progress;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.rafael.appprototype.Constants;
import com.example.rafael.appprototype.DataTypes.DB.GeriatricScale;
import com.example.rafael.appprototype.DataTypes.DB.Patient;
import com.example.rafael.appprototype.DataTypes.DB.Session;
import com.example.rafael.appprototype.DataTypes.NonDB.GeriatricScaleNonDB;
import com.example.rafael.appprototype.DataTypes.NonDB.GradingNonDB;
import com.example.rafael.appprototype.Evaluations.EvaluationsHistory.SessionScalesAdapterRecycler;
import com.example.rafael.appprototype.Evaluations.EvaluationsHistory.SessionScalesAdapterRecyclerIcons;
import com.example.rafael.appprototype.Evaluations.ReviewEvaluation.ReviewSingleSessionWithPatient;
import com.example.rafael.appprototype.HelpersHandlers.DatesHandler;
import com.example.rafael.appprototype.HelpersHandlers.SessionCardHelper;
import com.example.rafael.appprototype.Main.FragmentTransitions;
import com.example.rafael.appprototype.R;

import java.util.ArrayList;
import java.util.List;


public class ScaleLegendAdapter extends RecyclerView.Adapter<ScaleLegendAdapter.MyViewHolder> {

    private Patient patient;
    private GeriatricScaleNonDB scale;
    private Activity context;
    ArrayList<GradingNonDB> toConsider = new ArrayList<>();
    /**
     * Data to be displayed.
     */
    private List<Session> sessionsList;

    public ScaleLegendAdapter(Patient patient, GeriatricScaleNonDB scaleByName) {
        this.patient = patient;
        this.scale = scaleByName;
    }

    /**
     * Create a View
     */
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private final Button labelColor;
        public TextView label;

        public MyViewHolder(View view) {
            super(view);
            label = (TextView) view.findViewById(R.id.label);
            labelColor = (Button) view.findViewById(R.id.labelColor);
        }
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.label_item, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        // current grade

        GradingNonDB currentGrading = toConsider.get(position);

        holder.label.setText(currentGrading.getGrade());
        holder.labelColor.setBackgroundColor(GraphViewHelper.findColorForGrade(scale.getScaleName(), currentGrading, patient, toConsider));

    }


    @Override
    public int getItemCount() {
        if (scale.getScoring().isDifferentMenWomen()) {
            if (patient.getGender() == Constants.MALE) {
                toConsider = scale.getScoring().getValuesMen();
            } else {
                toConsider = scale.getScoring().getValuesWomen();

            }
        } else {
            toConsider = scale.getScoring().getValuesBoth();

        }
        return toConsider.size();
    }
}