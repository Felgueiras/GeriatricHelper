package com.felgueiras.apps.geriatric_helper.Sessions.ReviewSession.ReviewSingleTest;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.view.ViewStub;
import android.widget.ImageButton;
import android.widget.TextView;

import com.felgueiras.apps.geriatric_helper.Constants;
import com.felgueiras.apps.geriatric_helper.DataTypes.NonDB.GeriatricScaleNonDB;
import com.felgueiras.apps.geriatric_helper.DataTypes.NonDB.GradingNonDB;
import com.felgueiras.apps.geriatric_helper.DataTypes.Scales;
import com.felgueiras.apps.geriatric_helper.Sessions.SingleArea.ScaleCard;
import com.felgueiras.apps.geriatric_helper.Sessions.SingleArea.ScaleInfoHelper;
import com.felgueiras.apps.geriatric_helper.Sessions.SingleArea.ScaleHandlerNotes;
import com.felgueiras.apps.geriatric_helper.Firebase.FirebaseHelper;
import com.felgueiras.apps.geriatric_helper.Firebase.GeriatricScaleFirebase;
import com.felgueiras.apps.geriatric_helper.Firebase.PatientFirebase;
import com.felgueiras.apps.geriatric_helper.Firebase.SessionFirebase;
import com.felgueiras.apps.geriatric_helper.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;


/**
 * Create the Card for each of the Tests of a Session
 */
public class ReviewScaleCard extends RecyclerView.Adapter<ScaleCard.ScaleCardHolder> {

    /**
     * Session for the Tests.
     */
    private final SessionFirebase session;
    /**
     * Patient for this Session
     */
    private final PatientFirebase patient;
    private final String area;
    private final boolean comparePrevious;
    /**
     * Context.
     */
    private Activity context;
    /**
     * Name of Test being displayed.
     */
    private ArrayList<GeriatricScaleFirebase> scalesForArea;


    /**
     * Default constructor for the RV adapter.
     * @param context
     * @param session
     * @param area
     * @param comparePrevious
     */
    public ReviewScaleCard(Activity context, SessionFirebase session, String area, boolean comparePrevious) {
        this.context = context;
        this.session = session;
        this.patient = FirebaseHelper.getPatientFromSession(session);
        this.area = area;
        this.comparePrevious = comparePrevious;
    }


    /**
     * Create a CGACardHolder from a View
     *
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public ScaleCard.ScaleCardHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // get the Test CardView
        final View testCard = LayoutInflater.from(parent.getContext()).inflate(R.layout.scale_card_review, parent, false);
        return new ScaleCard.ScaleCardHolder(testCard);
    }

    /**
     * Display the data at a certain position
     *
     * @param holder   data to be displayed
     * @param position position to "put" the data
     */
    @Override
    public void onBindViewHolder(final ScaleCard.ScaleCardHolder holder, int position) {

        // get current test
        final GeriatricScaleFirebase currentScale = scalesForArea.get(position);

        // access a given Test from the DB
        holder.name.setText(currentScale.getShortName());

        // display subcategory for functional area scales
        if (area.equals(Constants.cga_functional)) {
            holder.subCategory.setVisibility(View.VISIBLE);
            holder.subCategory.setText(currentScale.getSubCategory());
        }

        // display Scoring to the user
        GradingNonDB match;
        if (patient != null) {
            match = Scales.getGradingForScale(
                    currentScale,
                    patient.getGender());
        } else {
            match = Scales.getGradingForScale(currentScale, Constants.SESSION_GENDER);
        }
        if (match != null)
            holder.result_qualitative.setText(match.getGrade());

        if (comparePrevious && patient != null) {
            comparePreviousSessions(currentScale, holder);
        }

        ViewManager parentView = (ViewManager) holder.result_qualitative.getParent();

        if (currentScale.hasNotes()) {
            parentView.removeView(holder.addNotesButton);
            holder.notes.setText(currentScale.getNotes());
        } else {
            // parentView.removeView(holder.notes);
        }

        holder.addNotesButton.setOnClickListener(new ScaleHandlerNotes(context, currentScale, holder, parentView));
        holder.notes.setOnClickListener(new ScaleHandlerNotes(context, currentScale, holder, parentView));

        // quantitative result
        String quantitative = "";
        quantitative += currentScale.getResult();
        GeriatricScaleNonDB testNonDB = Scales.getScaleByName(currentScale.getScaleName());
        if (testNonDB != null) {
            if (!testNonDB.getScoring().isDifferentMenWomen()) {
                quantitative += " (" + testNonDB.getScoring().getMinScore();
                quantitative += "-" + testNonDB.getScoring().getMaxScore() + ")";
            } else {
                if ((patient != null ? patient.getGender() : 0) == Constants.MALE) {
                    quantitative += " (" + testNonDB.getScoring().getMinMen();
                    quantitative += "-" + testNonDB.getScoring().getMaxMen() + ")";
                }
            }
        }
        holder.result_quantitative.setText(quantitative);

        if (currentScale.hasNotes()) {
            holder.notes.setText(currentScale.getNotes());
        }


        /**
         * Add a listener for when a note is added.
         */
        holder.notes.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                currentScale.setNotes(charSequence.toString());

                FirebaseHelper.updateScale(currentScale);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        // for when the test is selected
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create new fragment and transaction
                Fragment newFragment = new ReviewScaleFragment();
                // add arguments
                Bundle bundle = new Bundle();
                bundle.putSerializable(ReviewScaleFragment.SCALE, currentScale);
                newFragment.setArguments(bundle);
                // setup the transaction
                FragmentTransaction transaction = context.getFragmentManager().beginTransaction();
                transaction.replace(R.id.current_fragment, newFragment);
                transaction.addToBackStack(Constants.tag_review_test).commit();
            }
        });



        holder.description.setOnClickListener(new ScaleInfoHelper(context, Scales.getScaleByName(currentScale.getScaleName())));
    }

    /**
     * Compare the current result with the results from previous sessions.
     *
     * @param currentScale
     * @param holder
     */
    private void comparePreviousSessions(GeriatricScaleFirebase currentScale, ScaleCard.ScaleCardHolder holder) {
        double currentScaleResult = currentScale.getResult();
        // access this test from previous session that had this test
        String scaleName = currentScale.getScaleName();
        SessionFirebase currentSession = FirebaseHelper.getSessionByID(currentScale.getSessionID());

        // get all the instances of that Scale for this Patient
        ArrayList<GeriatricScaleFirebase> scaleInstances = new ArrayList<>();

        ArrayList<SessionFirebase> patientSessions = FirebaseHelper.getSessionsFromPatient(patient);
        ;
        //patientSessions.remove(currentSession);

        // sort by date ascending
        Collections.sort(patientSessions, new Comparator<SessionFirebase>() {
            public int compare(SessionFirebase o1, SessionFirebase o2) {
                return new Date(o1.getDate()).compareTo(new Date(o2.getDate()));
            }
        });

        // get index current session
        int sessionIndex = patientSessions.indexOf(currentSession);
        // discard sessions with higher indexes, consider only the ones with smaller


        // get instances for that test
        for (int i = sessionIndex - 1; i >= 0; i--) {
            SessionFirebase previousSession = patientSessions.get(i);
            List<GeriatricScaleFirebase> scalesfromSession = FirebaseHelper.getScalesFromSession(previousSession);
            for (GeriatricScaleFirebase previousScale : scalesfromSession) {
                if (previousScale.getScaleName().equals(scaleName)) {
                    scaleInstances.add(previousScale);
                    System.out.println("FOUND match");
                    // compare grade from previous session to current session
                    double previousScaleResult = previousScale.getResult();
                    System.out.println(previousScaleResult + "-" + currentScaleResult);
                    GradingNonDB previousGrading = Scales.getGradingForScale(
                            previousScale,
                            patient.getGender());
                    GradingNonDB currentGrading = Scales.getGradingForScale(
                            currentScale,
                            patient.getGender());
                    System.out.println(previousGrading.getGrade() + "-" + currentGrading.getGrade());
                    // check if has gotten worse or not
                    int index1 = Scales.getGradingIndex(previousScale,
                            patient.getGender());
                    int index2 = Scales.getGradingIndex(currentScale,
                            patient.getGender());
                    System.out.println(index1 + "-" + index2);
                    if (index2 > index1) {
                        // PATIENT got worse
                        System.out.println("WORSE");
                        ViewStub stubInfo = ((ViewStub) holder.itemView.findViewById(R.id.area_info_stub)); // get the reference of ViewStub
                        if (stubInfo != null) {
                            // only inflate once
                            View inflated = stubInfo.inflate();
                            TextView patientProgress = (TextView) inflated.findViewById(R.id.patient_progress);
                            ImageButton moreInfo = (ImageButton) inflated.findViewById(R.id.more_info);
                            patientProgress.setText(R.string.evolution_negative);
                            // display more info
                            moreInfo.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                                    alertDialog.setTitle("Informação");
                                    alertDialog.setMessage(context.getResources().getString(R.string.info_procedure_patient_worse));
                                    alertDialog.show();
                                }
                            });
                        }

                    } else if (index1 > index2) {
                        // PATIENT got better
                        //holder.patientProgress.setText(R.string.evolution_positive);
                    } else {
                        //holder.patientProgress.setText(R.string.evolution_neutral);
                    }

                    return;
                }
            }
        }
    }


    @Override
    public int getItemCount() {

        List<GeriatricScaleFirebase> sessionScales = FirebaseHelper.getScalesFromSession(session);
        // get scales for this area
        scalesForArea =  FirebaseHelper.getScalesForArea(sessionScales, area);
        return scalesForArea.size();
    }

}