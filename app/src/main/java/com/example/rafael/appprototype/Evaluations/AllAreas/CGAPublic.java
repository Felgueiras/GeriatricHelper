package com.example.rafael.appprototype.Evaluations.AllAreas;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.rafael.appprototype.Constants;
import com.example.rafael.appprototype.DataTypes.DB.GeriatricScale;
import com.example.rafael.appprototype.DataTypes.DB.Session;
import com.example.rafael.appprototype.DataTypes.NonDB.GeriatricScaleNonDB;
import com.example.rafael.appprototype.DataTypes.Scales;
import com.example.rafael.appprototype.DatesHandler;
import com.example.rafael.appprototype.R;
import com.getbase.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;

/**
 * Create a new public CGA.
 */
public class CGAPublic extends Fragment {

    public static String GENDER = "gender";


    private Session session;

    boolean resuming = false;

    public static FloatingActionButton resetFAB;
    private static FloatingActionButton saveFAB;
    private SharedPreferences sharedPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myInflatedView = inflater.inflate(R.layout.content_new_session_public, container, false);
        getActivity().setTitle(getResources().getString(R.string.cga));

        sharedPreferences = getActivity().getSharedPreferences(getResources().getString(R.string.sharedPreferencesTag), MODE_PRIVATE);

        /**
         * Resume an Evaluation.
         */
        String sessionID = sharedPreferences.getString(getResources().getString(R.string.saved_session_public), null);
        if (sessionID != null) {
            // get session by ID
            session = Session.getSessionByID(sessionID);
        }
        /**
         * Create a new one.
         */
        else {
            // create a new session
            createNewSession();
            addTestsToSession();

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.select_patient_gender);

            //list of items
            String[] items = new String[]{"M", "F"};
            builder.setSingleChoiceItems(items, 0,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // item selected logic
                            if (which == 0)
                                Constants.SESSION_GENDER = Constants.MALE;
                            else
                                Constants.SESSION_GENDER = Constants.FEMALE;
                        }
                    });

            String positiveText = getString(android.R.string.ok);
            builder.setPositiveButton(positiveText,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // positive button logic
                        }
                    });

            String negativeText = getString(android.R.string.cancel);
            builder.setNegativeButton(negativeText,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // negative button logic
                        }
                    });

            builder.setCancelable(false);
            AlertDialog dialog = builder.create();
            // display dialog
            dialog.show();
        }


        RecyclerView recyclerView = (RecyclerView) myInflatedView.findViewById(R.id.area_scales_recycler_view);
        AreaCardPrivate adapter = new AreaCardPrivate(getActivity(), session, resuming, Constants.SESSION_GENDER);

        // create Layout
        int numbercolumns = 1;
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), numbercolumns);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        // reset the session
        resetFAB = (FloatingActionButton) myInflatedView.findViewById(R.id.session_reset);
        resetFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                alertDialog.setTitle(getResources().getString(R.string.session_reset));
                alertDialog.setMessage(getResources().getString(R.string.session_reset_question));
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getResources().getString(R.string.yes),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // remove session
                                session.delete();
                                sharedPreferences.edit().putString(getString(R.string.saved_session_public), null).apply();

                                FragmentManager fragmentManager = getFragmentManager();
                                fragmentManager.beginTransaction()
                                        .replace(R.id.current_fragment, new CGAPublicInfo())
                                        .commit();
                                dialog.dismiss();

                                // Snackbar.make(getView(), getResources().getString(R.string.session_created), Snackbar.LENGTH_SHORT).show();
                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getResources().getString(R.string.no),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        });

        /*
        saveFAB = (FloatingActionButton) myInflatedView.findViewById(R.id.session_save);
        saveFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RelativeLayout layout = (RelativeLayout) getActivity().findViewById(R.id.newSessionLayout);

                // no test selected
                if (session.getScalesFromSession().size() == 0) {
                    Snackbar.make(layout, getResources().getString(R.string.you_must_select_test), Snackbar.LENGTH_SHORT).show();
                    return;
                }

                // check how many tests were completed
                int numTestsCompleted = 0;
                List<GeriatricScale> testsFromSession = session.getScalesFromSession();
                for (GeriatricScale test : testsFromSession) {
                    if (test.isCompleted())
                        numTestsCompleted++;
                }
                if (numTestsCompleted == 0) {
                    Snackbar.make(layout, getResources().getString(R.string.not_all_tests_complete), Snackbar.LENGTH_SHORT).show();
                    return;
                }

                // check if there is an added patient or not
                // no patient selected
                if (patientForThisSession == null) {
                    AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                    //alertDialog.setTitle("Criar paciente");
                    alertDialog.setMessage("Deseja adicionar paciente a esta sessão?");
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Sim",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    /**
                                     * Open the fragment to pick an already existing Patient.
                                     */
        /*
                                    Constants.pickingPatient = true;
                                    Bundle args = new Bundle();
                                    args.putBoolean(ViewPatientsFragment.selectPatient, true);
                                    FragmentTransitions.replaceFragment(getActivity(), new ViewPatientsFragment(), args,
                                            Constants.fragment_show_patients);
                                    dialog.dismiss();
                                    Snackbar.make(getView(), getResources().getString(R.string.session_created), Snackbar.LENGTH_SHORT).show();
                                }
                            });
                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Não",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    // reset this Session
                                    Constants.SESSION_ID = null;
                                    // delete Session from DB
                                    session.delete();
                                    FragmentManager fragmentManager = getFragmentManager();
                                    fragmentManager.beginTransaction()
                                            .replace(R.id.content_fragment, new PatientsMain())
                                            .commit();
                                    Snackbar.make(getView(), getResources().getString(R.string.session_created), Snackbar.LENGTH_SHORT).show();
                                }
                            });
                    alertDialog.show();
                    return;
                }
                Constants.SESSION_ID = null;
                List<GeriatricScale> finalTests = session.getScalesFromSession();
                for (GeriatricScale test : finalTests) {
                    if (!test.isCompleted()) {
                        test.setSession(null);
                        test.delete();
                    }
                }

                Snackbar.make(getView(), getResources().getString(R.string.session_created), Snackbar.LENGTH_SHORT).show();
                BackStackHandler.goToPreviousScreen();
            }
        });
        */

        return myInflatedView;
    }

    /**
     * Add Tests to a Session
     */
    private void addTestsToSession() {
        ArrayList<GeriatricScaleNonDB> testsNonDB = Scales.getAllTests();
        for (GeriatricScaleNonDB testNonDB : testsNonDB) {
            GeriatricScale test = new GeriatricScale();
            test.setGuid(session.getGuid() + "-" + testNonDB.getTestName());
            test.setTestName(testNonDB.getTestName());
            test.setShortName(testNonDB.getShortName());
            test.setSession(session);
            test.setDescription(testNonDB.getDescription());
            if (testNonDB.isSingleQuestion())
                test.setSingleQuestion(true);
            test.setAlreadyOpened(false);
            test.save();
        }
    }

    /**
     * Generate a new Session.
     */
    private void createNewSession() {
        Calendar c = Calendar.getInstance();
        Date time = c.getTime();
        String sessionID = time.toString();
        // save to dabatase
        session = new Session();
        session.setGuid(sessionID);

        // set date
        Calendar now = Calendar.getInstance();
        int year = now.get(Calendar.YEAR);
        int month = now.get(Calendar.MONTH);
        int day = now.get(Calendar.DAY_OF_MONTH);
        int hour = now.get(Calendar.HOUR_OF_DAY);
        int minute = now.get(Calendar.MINUTE);
        session.setDate(DatesHandler.createCustomDate(year, month, day, hour, minute));
        //system.out.println("Session date is " + session.getDate());
        session.save();

        // save the ID
        sharedPreferences.edit().putString(getString(R.string.saved_session_public), sessionID).apply();
    }

}

