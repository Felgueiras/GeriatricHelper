package com.felgueiras.apps.geriatrichelper.Sessions.ReviewSession;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.felgueiras.apps.geriatrichelper.Constants;
import com.felgueiras.apps.geriatrichelper.Firebase.RealtimeDatabase.FirebaseDatabaseHelper;
import com.felgueiras.apps.geriatrichelper.PatientsManagement;
import com.felgueiras.apps.geriatrichelper.Sessions.SessionsHistoryMainFragment;
import com.felgueiras.apps.geriatrichelper.Firebase.RealtimeDatabase.GeriatricScaleFirebase;
import com.felgueiras.apps.geriatrichelper.Firebase.RealtimeDatabase.PatientFirebase;
import com.felgueiras.apps.geriatrichelper.Firebase.RealtimeDatabase.SessionFirebase;
import com.felgueiras.apps.geriatrichelper.HelpersHandlers.DatesHandler;
import com.felgueiras.apps.geriatrichelper.HelpersHandlers.SessionPDF;
import com.felgueiras.apps.geriatrichelper.Patients.PatientProfile.PatientProfileFragment;
import com.felgueiras.apps.geriatrichelper.R;
import com.felgueiras.apps.geriatrichelper.Sessions.SingleArea.SessionNoteshandler;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class ReviewSingleSessionWithPatient extends Fragment {

    public static String COMPARE_PREVIOUS;
    /**
     * Session object
     */
    private SessionFirebase session;
    /**
     * String that identifies the Session to be passed as argument.
     */
    public static String SESSION = "session";
    private TabLayout tabLayout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

//        SharedPreferencesHelper.unlockSessionCreation(getActivity());

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.bottom_navigation_review_areas, container, false);
        Bundle args = getArguments();
        // get Session and Patient
        session = (SessionFirebase) args.getSerializable(SESSION);

        if (PatientsManagement.getInstance().getPatientFromSession(session, getActivity()) != null) {
            getActivity().setTitle(PatientsManagement.getInstance().getPatientFromSession(session, getActivity()).getName() + " - " +
                    DatesHandler.dateToStringWithoutHour(new Date(session.getDate())));
        } else {
            getActivity().setTitle(DatesHandler.dateToStringWithoutHour(new Date(session.getDate())));
        }


        /*
          Setup bottom navigation.
         */
        BottomNavigationView bottomNavigationView = view.findViewById(R.id.bottom_navigation);

        // disable areas that don't have any scale
        Menu menuNav = bottomNavigationView.getMenu();

        int defaultIndex = -1;

        // check if this session contains scales from this area
        ArrayList<GeriatricScaleFirebase> scalesFromSession = FirebaseDatabaseHelper.getScalesFromSession(session);
        for (int i = 0; i < Constants.cga_areas.length; i++) {
            // current area
            String currentArea = Constants.cga_areas[i];
            // disable area
            menuNav.getItem(i).setEnabled(false);
            for (GeriatricScaleFirebase scale : scalesFromSession) {
                if (scale.getArea().equals(currentArea)) {
                    menuNav.getItem(i).setEnabled(true);
                    if (defaultIndex == -1) {
                        defaultIndex = i;
                        menuNav.getItem(i).setChecked(true);
                    }
                }
            }

        }
        /*
          Default fragment.
         */
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        Fragment currentFragment = fragmentManager.findFragmentById(R.id.frame_layout_cga_area);
        if (currentFragment != null)
            transaction.remove(currentFragment);

        bottomNavigationView.getMenu().getItem(defaultIndex).setChecked(true);
        final ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(ReviewAreaFragment.newInstance(Constants.cga_mental, session));
        fragments.add(ReviewAreaFragment.newInstance(Constants.cga_functional, session));
        fragments.add(ReviewAreaFragment.newInstance(Constants.cga_nutritional, session));
        fragments.add(ReviewAreaFragment.newInstance(Constants.cga_social, session));

        Constants.bottomNavigationReviewSession = defaultIndex;
        transaction.replace(R.id.frame_layout_cga_area, fragments.get(defaultIndex));
        transaction.commit();


        final Map<Integer, Integer> fragmentMapping = new HashMap<>();
//        fragmentMapping.put(R.id.cga_mental, 0);
//        fragmentMapping.put(R.id.cga_functional, 1);
//        fragmentMapping.put(R.id.cga_nutritional, 2);
//        fragmentMapping.put(R.id.cga_social, 3);


        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Fragment fragment = null;

                        Integer selectedIndex = fragmentMapping.get(item.getItemId());

                        if (Constants.bottomNavigationReviewSession != selectedIndex) {
                            Constants.bottomNavigationReviewSession = selectedIndex;
                            fragment = fragments.get(selectedIndex);
                        } else {
                            return true;
                        }


                        FragmentManager fragmentManager = getChildFragmentManager();
                        FragmentTransaction transaction = fragmentManager.beginTransaction();

                        Fragment currentFragment = fragmentManager.findFragmentById(R.id.frame_layout_cga_area);
                        if (currentFragment != null)
                            transaction.remove(currentFragment);
                        transaction.replace(R.id.frame_layout_cga_area, fragment);
                        transaction.commit();

                        return true;
                    }
                });

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_review_session_with_patient, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete:
                AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                alertDialog.setTitle(getResources().getString(R.string.session_erase));
                alertDialog.setMessage(getResources().getString(R.string.session_erase_question));
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getResources().getString(R.string.yes),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // erase session
                                FragmentManager fragmentManager = getActivity().getFragmentManager();
                                int index = fragmentManager.getBackStackEntryCount() - 1;
                                FragmentManager.BackStackEntry backEntry = fragmentManager.getBackStackEntryAt(index);
                                String tag = backEntry.getName();

                                fragmentManager.popBackStack();
                                PatientFirebase patient = PatientsManagement.getInstance().getPatientFromSession(session,
                                        getActivity());
                                dialog.dismiss();

                                DrawerLayout layout = getActivity().findViewById(R.id.drawer_layout);
                                Snackbar.make(layout, getResources().getString(R.string.session_erase_snackbar), Snackbar.LENGTH_SHORT).show();

                                Fragment fragment = null;
                                if (tag.equals(Constants.tag_review_session_from_sessions_list)) {
                                    // go back to sessions list
                                    fragment = new SessionsHistoryMainFragment();
                                } else if (tag.equals(Constants.tag_review_session_from_patient_profile)) {
                                    // go back to PATIENT profile
                                    Bundle args = new Bundle();
                                    args.putSerializable(PatientProfileFragment.PATIENT, patient);
                                    fragment = new PatientProfileFragment();
                                    fragment.setArguments(args);
                                }


//                                Fragment currentFragment = fragmentManager.findFragmentById(R.id.current_fragment);
//                                fragmentManager.beginTransaction()
//                                        .remove(currentFragment)
//                                        .replace(R.id.current_fragment, fragment)
//                                        .commit();

                                FirebaseDatabaseHelper.deleteSession(session, getActivity());
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
            case R.id.createPDF:
                // prompt if user information is to be added to the PDF
                alertDialog = new AlertDialog.Builder(getActivity()).create();
//                alertDialog.setTitle(getResources().getString(R.string.session_erase));
                alertDialog.setMessage("Deseja incluir dados do paciente no PDF?");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getResources().getString(R.string.yes),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                new SessionPDF(session).createSessionPdf(getActivity(), true);
                                dialog.dismiss();
                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getResources().getString(R.string.no),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                new SessionPDF(session).createSessionPdf(getActivity(), false);

                                dialog.dismiss();
                            }
                        });
                alertDialog.show();

                break;
            case R.id.addNotes:
                // edit notes for this session
                new SessionNoteshandler(getActivity(), session).editNotes();

        }
        return true;

    }


}

