package com.felgueiras.apps.geriatrichelper.Patients.PatientProfile.PatientTimeline;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.felgueiras.apps.geriatrichelper.Firebase.FirebaseHelper;
import com.felgueiras.apps.geriatrichelper.Firebase.RealtimeDatabase.PatientFirebase;
import com.felgueiras.apps.geriatrichelper.Firebase.RealtimeDatabase.PrescriptionFirebase;
import com.felgueiras.apps.geriatrichelper.Firebase.RealtimeDatabase.SessionFirebase;
import com.felgueiras.apps.geriatrichelper.HelpersHandlers.DatesHandler;
import com.felgueiras.apps.geriatrichelper.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;


public class PatientTimelineFragmentOriginal extends Fragment {

    public static final String PATIENT = "PATIENT";
    private PatientFirebase patient;
    ArrayList<Object> patientSessionsPrescriptions = new ArrayList<>();


    private RecyclerView mRecyclerView;
    private TimeLineAdapterGeneralOriginal mTimeLineAdapter;
    private Orientation mOrientation;
    private boolean mWithLinePadding;
    private ArrayList<SessionFirebase> patientSessions = new ArrayList<>();
    private ArrayList<PrescriptionFirebase> patientsPrescriptions = new ArrayList<>();


    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        Bundle bundle = getArguments();
        patient = (PatientFirebase) bundle.getSerializable(PATIENT);

        mOrientation = Orientation.VERTICAL;
        mWithLinePadding = false;
    }


    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.timeline_general, container, false);


        mRecyclerView = view.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(getLinearLayoutManager());
        mRecyclerView.setHasFixedSize(true);

//        /**
//         * Setup FABS
//         */
//        FloatingActionButton fabAddSession = (FloatingActionButton) view.findViewById(R.id.patient_createSession);
//        fabAddSession.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Bundle args = new Bundle();
//                args.putSerializable(CGAPrivate.PATIENT, patient);
//                // pass the previous session
//                SharedPreferencesHelper.unlockSessionCreation(getActivity());
//                FragmentTransitions.replaceFragment(getActivity(), new CGAPrivate(), args, Constants.tag_create_session_with_patient);
//                getActivity().setTitle(getResources().getString(R.string.cga));
//            }
//        });

        // create timeline
        initView();
        return view;
    }

    private LinearLayoutManager getLinearLayoutManager() {
        if (mOrientation == Orientation.HORIZONTAL) {
            return new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        } else {
            return new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        }
    }

    /**
     * Create timeline.
     */
    private void initView() {
        retrievePatientSessions();
    }


    /**
     * Retrieve the patient's sessions.
     */
    private void retrievePatientSessions() {

        FirebaseHelper.firebaseTableSessions.orderByChild("patientID").equalTo(patient.getGuid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                patientSessions.clear();
                patientSessionsPrescriptions.clear();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    SessionFirebase sessions = postSnapshot.getValue(SessionFirebase.class);
                    sessions.setKey(postSnapshot.getKey());
                    patientSessions.add(sessions);
                }

                // get prescriptions
                FirebaseHelper.firebaseTablePrescriptions.orderByChild("patientID")
                        .equalTo(patient.getGuid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                patientsPrescriptions.clear();
                                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                    PrescriptionFirebase prescription = postSnapshot.getValue(PrescriptionFirebase.class);
                                    prescription.setKey(postSnapshot.getKey());
                                    patientsPrescriptions.add(prescription);
                                }
                                patientSessionsPrescriptions.addAll(patientSessions);


                                // decompose prescriptions per day
                                HashSet<Date> days = new HashSet<>();
                                for (PrescriptionFirebase prescriptionFirebase : patientsPrescriptions) {
                                    Date dateWithoutHour = DatesHandler.getDateWithoutHour(prescriptionFirebase.getDate().getTime());
                                    days.add(dateWithoutHour);
                                }

                                ArrayList<Date> differentDates = new ArrayList<>();
                                differentDates.addAll(days);
                                // order by date (descending)
                                Collections.sort(differentDates, new Comparator<Date>() {
                                    @Override
                                    public int compare(Date first, Date second) {
                                        return second.compareTo(first);
                                    }
                                });

                                // add prescriptions by date
                                for (Date currentDate : differentDates) {
                                    // get prescriptions from this date
                                    ArrayList<PrescriptionFirebase> prescriptionsForDate = new ArrayList<>();
                                    for (PrescriptionFirebase prescription : patientsPrescriptions) {
                                        if (DatesHandler.getDateWithoutHour(prescription.getDate().getTime()).compareTo(currentDate) == 0) {
                                            prescriptionsForDate.add(prescription);
                                        }
                                    }
                                    // add to array
                                    patientSessionsPrescriptions.add(prescriptionsForDate);
                                }

//                                 sort all by date
                                Collections.sort(patientSessionsPrescriptions, new Comparator<Object>() {
                                    public int compare(Object o1, Object o2) {
                                        Date d1, d2;
                                        if (o1 instanceof ArrayList<?>) {
                                            d1 = ((ArrayList<PrescriptionFirebase>) o1).get(0).getDate();
                                        } else {
                                            d1 = new Date(((SessionFirebase) o1).getDate());
                                        }
                                        if (o2 instanceof ArrayList<?>) {
                                            d2 = ((ArrayList<PrescriptionFirebase>) o2).get(0).getDate();
                                        } else {
                                            d2 = new Date(((SessionFirebase) o2).getDate());
                                        }
                                        return d2.compareTo(d1);
                                    }
                                });


                                mTimeLineAdapter = new TimeLineAdapterGeneralOriginal(patientSessionsPrescriptions,
                                        mOrientation, mWithLinePadding, getActivity(), false);
                                mRecyclerView.setAdapter(mTimeLineAdapter);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }


                        });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
            }
        });
    }


}