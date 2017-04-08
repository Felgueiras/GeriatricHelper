package com.felgueiras.apps.geriatric_helper.Patients.SinglePatient;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.felgueiras.apps.geriatric_helper.Constants;
import com.felgueiras.apps.geriatric_helper.Firebase.PatientFirebase;
import com.felgueiras.apps.geriatric_helper.Firebase.SessionFirebase;
import com.felgueiras.apps.geriatric_helper.FirebaseHelper;
import com.felgueiras.apps.geriatric_helper.HelpersHandlers.BackStackHandler;
import com.felgueiras.apps.geriatric_helper.HelpersHandlers.DatesHandler;
import com.felgueiras.apps.geriatric_helper.Main.FragmentTransitions;
import com.felgueiras.apps.geriatric_helper.Patients.Progress.ProgressFragment;
import com.felgueiras.apps.geriatric_helper.Patients.SinglePatient.ViewPatientSessions.PatientNotesFragment;
import com.felgueiras.apps.geriatric_helper.Patients.SinglePatient.ViewPatientSessions.PatientSessionsFragment;
import com.felgueiras.apps.geriatric_helper.R;

import java.util.ArrayList;

/**
 * Created by rafael on 05-10-2016.
 */
public class PatientProfileFragment extends Fragment {

    public static final String PATIENT = "PATIENT";
    /**
     * Patient to be displayed
     */
    private PatientFirebase patient;

    private Menu menu;

    Fragment defaultFragment;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.bottom_navigation_patient_profile, container, false);
        System.out.println("VIEW SINGLE PATIENT INFO");

        Bundle bundle = getArguments();
        if (bundle != null) {
            String actionTitle = bundle.getString("ACTION");
            String transText = bundle.getString("TRANS_TEXT");
            //view.findViewById(R.id.label).setTransitionName(transText);
            //system.out.println("lol 2");
        }


        // get PATIENT
        patient = (PatientFirebase) bundle.getSerializable(PATIENT);
//        ((PrivateAreaActivity)getActivity()).changeTitle(PATIENT.getName());

        getActivity().setTitle(patient.getName());

        // access Views
        //TextView label = (TextView) view.findViewById(R.id.label);
        TextView patientBirthDate = (TextView) view.findViewById(R.id.patientAge);
        TextView patientAddress = (TextView) view.findViewById(R.id.patientAddress);
        ImageView patientPhoto = (ImageView) view.findViewById(R.id.patientPhoto);
        Button patientProgress = (Button) view.findViewById(R.id.patientEvolution);
//        Button erasePatient = (Button) view.findViewById(R.id.erasePatient);
        TextView processNumber = (TextView) view.findViewById(R.id.processNumber);

        // set Patient infos
        //label.setText(PATIENT.getName());
        patientBirthDate.setText(DatesHandler.dateToStringWithoutHour(patient.getBirthDate()) + " - " + patient.getAge() + " anos");
        patientAddress.setText("Morada: " + patient.getAddress());
        processNumber.setText("Processo nº " + patient.getProcessNumber());
        //patientPhoto.setImageResource(PATIENT.getPicture());
        switch (patient.getGender()) {
            case Constants.MALE:
                patientPhoto.setImageResource(R.drawable.male);
                break;
            case Constants.FEMALE:
                patientPhoto.setImageResource(R.drawable.female);
                break;
        }


        // consult patient's progress
        patientProgress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // reset the page
                Constants.bottomNavigationPatientProgress = 0;
                Bundle args = new Bundle();
                args.putSerializable(ProgressFragment.PATIENT, patient);
                FragmentTransitions.replaceFragment(getActivity(), new ProgressFragment(), args, Constants.tag_patient_progress);
            }
        });


        /**
         * Setup bottom navigation.
         */
        BottomNavigationView bottomNavigationView = (BottomNavigationView) view.findViewById(R.id.bottom_navigation);

        /**
         * Default fragment.
         */
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        Fragment currentFragment = fragmentManager.findFragmentById(R.id.frame_layout_bottom_navigation);
        if (currentFragment != null)
            transaction.remove(currentFragment);


        ArrayList<SessionFirebase> sessionsFromPatient = FirebaseHelper.getSessionsFromPatient(patient);

        if (sessionsFromPatient.isEmpty()) {
            defaultFragment = new PatientSessionsEmpty();
            Bundle args = new Bundle();
            args.putSerializable(PatientSessionsEmpty.PATIENT, patient);
            args.putString(PatientSessionsEmpty.MESSAGE, getResources().getString(R.string.no_sessions_for_patient));
            defaultFragment.setArguments(args);
        } else {
            defaultFragment = new PatientSessionsFragment();
            Bundle args = new Bundle();
            args.putSerializable(PatientSessionsFragment.PATIENT, patient);
            defaultFragment.setArguments(args);
        }


        transaction.replace(R.id.frame_layout_bottom_navigation, defaultFragment);
        transaction.commit();

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Fragment fragment = null;
                        switch (item.getItemId()) {
                            case R.id.patient_sessions:
                                fragment = defaultFragment;
                                break;
                            case R.id.patient_notes:
                                fragment = new PatientNotesFragment();
                                Bundle args = new Bundle();
                                args.putSerializable(PatientNotesFragment.PATIENT, patient);
                                fragment.setArguments(args);
                                break;
                        }

                        FragmentManager fragmentManager = getChildFragmentManager();
                        FragmentTransaction transaction = fragmentManager.beginTransaction();

                        Fragment currentFragment = fragmentManager.findFragmentById(R.id.frame_layout_bottom_navigation);
                        if (currentFragment != null)
                            transaction.remove(currentFragment);
                        transaction.replace(R.id.frame_layout_bottom_navigation, fragment);
                        transaction.commit();


                        return true;
                    }
                });


        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_patient_profile, menu);
        this.menu = menu;
        checkFavorite();
    }

    private void checkFavorite() {
        MenuItem favoriteItem = menu.findItem(R.id.favorite);
        if (patient.isFavorite())
            favoriteItem.setIcon(R.drawable.ic_star_white_24dp);
        else
            favoriteItem.setIcon(R.drawable.ic_star_border_black_24dp);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.favorite:
                patient.setFavorite(!patient.isFavorite());
                FirebaseHelper.firebaseTablePatients.child(patient.getKey()).child("favorite").setValue(patient.isFavorite());

                if (patient.isFavorite()) {
                    Snackbar.make(getView(), R.string.patient_favorite_add, Snackbar.LENGTH_LONG).show();
                    item.setIcon(R.drawable.ic_star_white_24dp);

                } else {
                    Snackbar.make(getView(), R.string.patient_favorite_remove, Snackbar.LENGTH_LONG).show();
                    item.setIcon(R.drawable.ic_star_border_black_24dp);
                }
                break;
            case R.id.delete:
                AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                alertDialog.setTitle(getResources().getString(R.string.patient_erase));
                alertDialog.setMessage(getResources().getString(R.string.patient_erase_question));
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getResources().getString(R.string.yes),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // remove sessions from PATIENT
//                                ArrayList<Session> sessionsFromPatient = patient.getSessionsFromPatient();
//                                for (Session session : sessionsFromPatient) {
//                                    session.delete();
//                                }
//                                patient.delete();
                                dialog.dismiss();

                                DrawerLayout layout = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
                                Snackbar.make(layout, getResources().getString(R.string.patient_erase_snackbar), Snackbar.LENGTH_SHORT).show();

                                BackStackHandler.getFragmentManager().popBackStack();
//                                FragmentManager fragmentManager = BackStackHandler.getFragmentManager();
//                                Fragment currentFragment = fragmentManager.findFragmentById(R.id.current_fragment);
//                                fragmentManager.beginTransaction()
//                                        .remove(currentFragment)
//                                        .replace(R.id.current_fragment, new PatientsMain())
//                                        .commit();
                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getResources().getString(R.string.no),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
                break;

        }
        return true;

    }


}

