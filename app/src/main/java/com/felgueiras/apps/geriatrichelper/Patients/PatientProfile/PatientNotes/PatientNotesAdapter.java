package com.felgueiras.apps.geriatrichelper.Patients.PatientProfile.PatientNotes;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.felgueiras.apps.geriatrichelper.Constants;
import com.felgueiras.apps.geriatrichelper.Firebase.RealtimeDatabase.FirebaseDatabaseHelper;
import com.felgueiras.apps.geriatrichelper.Firebase.RealtimeDatabase.GeriatricScaleFirebase;
import com.felgueiras.apps.geriatrichelper.Firebase.RealtimeDatabase.PatientFirebase;
import com.felgueiras.apps.geriatrichelper.Firebase.RealtimeDatabase.SessionFirebase;
import com.felgueiras.apps.geriatrichelper.HelpersHandlers.DatesHandler;
import com.felgueiras.apps.geriatrichelper.Main.FragmentTransitions;
import com.felgueiras.apps.geriatrichelper.R;
import com.felgueiras.apps.geriatrichelper.Sessions.ReviewSession.ReviewSingleSessionWithPatient;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PatientNotesAdapter extends RecyclerView.Adapter<PatientNotesAdapter.MyViewHolder> {

    private Activity context;
    /**
     * Records from that PATIENT
     */
    private ArrayList<SessionFirebase> sessions;

    /**
     * Create a View
     */
    public class MyViewHolder extends RecyclerView.ViewHolder {
        private final View view;
        public TextView date, notes;
        public ImageView overflow;

        public MyViewHolder(View view) {
            super(view);
            this.view = view;
            date = view.findViewById(R.id.recordDate);
            notes = view.findViewById(R.id.sessionNotes);
        }
    }


    public PatientNotesAdapter(Activity context, ArrayList<SessionFirebase> sessions, PatientFirebase patient) {
        this.context = context;
        this.sessions = sessions;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_session_notes, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        SessionFirebase session = sessions.get(position);
        holder.date.setText(DatesHandler.dateToStringWithHour(new Date(session.getDate()),true));
        List<GeriatricScaleFirebase> sessionScales = FirebaseDatabaseHelper.getScalesFromSession(sessions.get(position));


        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);


        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(context,
                layoutManager.getOrientation());
        final SessionFirebase currentSession = sessions.get(position);

        View.OnClickListener cardSelected = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                Constants.bottomNavigationReviewSession = 0;
                args.putSerializable(ReviewSingleSessionWithPatient.SESSION, currentSession);
                FragmentTransitions.replaceFragment(context,
                        new ReviewSingleSessionWithPatient(),
                        args,
                        Constants.tag_review_session_from_patient_profile);
            }
        };
        // add on click listener for the Session
        holder.view.setOnClickListener(cardSelected);

        holder.notes.setText(session.getNotes());

//        holder.overflow.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                AlertDialog alertDialog = new AlertDialog.Builder(context).create();
////                    alertDialog.setTitle("Foi Encontrada uma Sessão a decorrer");
//                alertDialog.setMessage("Deseja eliminar esta Sessão? " + currentSession.getDate());
//                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Sim",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//                                Snackbar.make(holder.view, "Sessão eliminada.", Snackbar.LENGTH_SHORT).show();
//                                FirebaseDatabaseHelper.deleteSession(currentSession);
//
//                            }
//                        });
//                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Não",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//                                Snackbar.make(holder.view, "Ação cancelada", Snackbar.LENGTH_SHORT).show();
//
//                            }
//                        });
//                alertDialog.show();
//            }
//        });
    }


    @Override
    public int getItemCount() {

        return sessions.size();
    }
}
