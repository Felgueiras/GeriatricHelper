package com.example.rafael.appprototype.Main;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.activeandroid.ActiveAndroid;
import com.example.rafael.appprototype.DatabaseOps;
import com.example.rafael.appprototype.Evaluations.DisplayTest.ScaleFragmentBottomButtons;
import com.example.rafael.appprototype.HelpersHandlers.BackStackHandler;
import com.example.rafael.appprototype.Constants;
import com.example.rafael.appprototype.DataTypes.DB.GeriatricScale;
import com.example.rafael.appprototype.DataTypes.DB.Patient;
import com.example.rafael.appprototype.DataTypes.NonDB.GeriatricScaleNonDB;
import com.example.rafael.appprototype.Evaluations.AllAreas.CGAPublicBottomButtons;
import com.example.rafael.appprototype.Evaluations.AllAreas.CGAPublicInfo;
import com.example.rafael.appprototype.Introduction.MyIntro;
import com.example.rafael.appprototype.Prescription.DrugPrescriptionMain;
import com.example.rafael.appprototype.R;
import com.example.rafael.appprototype.HelpersHandlers.SharedPreferencesHelper;

public class PublicArea extends AppCompatActivity {


    /**
     * Current context.
     */
    private PublicArea context;
    /**
     * SharedPreferences.
     */
    private SharedPreferences sharedPreferences;

    BackStackHandler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("Lock", "onCreate");
        Log.d("Orientation", this.getResources().getConfiguration().orientation + "");


        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation_drawer_public);


        /**
         * Views/layout.
         */
        // setup toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // display home
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        Constants.toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        Constants.toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new DrawerItemClickListener(this, getFragmentManager(), drawer));

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Toolbar", view.toString());
                if (!Constants.upButton) {
                    drawer.openDrawer(Gravity.LEFT);
                } else {
                    Log.d("Toolbar", "Up button showing");
                    onBackPressed();
                }
            }
        });

        // initialize ActiveAndroid (DB)
        ActiveAndroid.initialize(getApplication());
        // save context
        context = this;

        // access SharedPreferences
        Log.d("Preferences", getString(R.string.sharedPreferencesTag));
        sharedPreferences = getSharedPreferences(getString(R.string.sharedPreferencesTag), MODE_PRIVATE);

        // is there an ongoing public session?
        isTherePublicSession();

        // get screen size
        Constants.screenSize = getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;


        boolean alreadyLogged = sharedPreferences.getBoolean(Constants.logged_in, false);
        if (alreadyLogged) {
            Intent intent = new Intent(PublicArea.this, PrivateArea.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
            return;
        }

        // not logged in

        //  Declare a new thread to do a preference check
        final SharedPreferences finalSharedPreferences = sharedPreferences;
        Thread checkFirstStart = new Thread(new Runnable() {
            @Override
            public void run() {
                //  Create a new boolean and preference and set it to true
                boolean isFirstStart = finalSharedPreferences.getBoolean("firstStart", true);
                //  If the activity has never started before...
                if (isFirstStart) {
                    //  Launch app intro
                    Intent i = new Intent(PublicArea.this, MyIntro.class);
                    startActivity(i);
                    //  Make a new preferences editor
                    SharedPreferences.Editor e = finalSharedPreferences.edit();
                    //  Edit preference to make it false because we don'checkFirstStart want this to run again
                    e.putBoolean("firstStart", false);
                    e.apply();
                    // insert dummy data in the app
                    DatabaseOps.insertDummyData();
                }
            }
        });
        // Start the thread
        checkFirstStart.start();

        // set public area of the app
        Constants.area = Constants.area_public;


        // set sample fragment
        Fragment fragment = null;
        String defaultFragment = Constants.fragment_sessions;
        switch (defaultFragment) {
            case Constants.fragment_sessions:
                fragment = new CGAPublicInfo();
                setTitle(getResources().getString(R.string.cga));
                break;
            case Constants.fragment_drug_prescription:
                fragment = new DrugPrescriptionMain();
                setTitle(getResources().getString(R.string.tab_drug_prescription));
                break;
        }
        if (savedInstanceState == null) {
            // set handler for the Fragment stack
            handler = new BackStackHandler(getFragmentManager(), this);
            getFragmentManager().addOnBackStackChangedListener(handler);

            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.current_fragment, fragment, "initial_tag")
                    .commit();

        } else {
            Log.d("Orientation", "Saved instance state not null");
            fragment = getFragmentManager().findFragmentByTag("initial_tag");
//            handler =
            handler = (BackStackHandler) savedInstanceState.getSerializable("backStackHandler");
            Log.d("Key", handler.getFragmentManager().getBackStackEntryCount() + "");
            handler.onBackStackChanged();
        }


    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("key", "12345");
        // save fragment manager
        outState.putSerializable("backStackHandler", handler);
    }


    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getFragmentManager();
        BackStackHandler.handleBackButton(fragmentManager);
    }


//    public boolean onCreateOptionsMenu(Menu menu) {
//
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.erase_data, menu);
//
//
//        return true;
//    }

//    public boolean onOptionsItemSelected(MenuItem item) {
//
//        //respond to menu item selection
//        switch (item.getItemId()) {
//            case R.id.erase_data:
//                // erase all data
//                DatabaseOps.eraseAll();
//                DatabaseOps.insertDummyData();
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//
//    }


    @Override
    public void onStart() {
        super.onStart();
    }

    /**
     * Replace one fragment by another
     *
     * @param args
     */
    public void replaceFragment(Fragment endFragment, Bundle args, String addToBackStackTag) {

        // get current Fragment
        Fragment startFragment = getFragmentManager().findFragmentById(R.id.current_fragment);
        if (args != null) {
            endFragment.setArguments(args);
        }
        // add Exit transition

//        startFragment.setExitTransition(TransitionInflater.from(
//                this).inflateTransition(android.R.transition.fade));
//        // add Enter transition
//        endFragment.setEnterTransition(TransitionInflater.from(this).
//                inflateTransition(android.R.transition.fade));

        // Create new transaction and add to back stack
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.current_fragment, endFragment);
        if (!addToBackStackTag.equals(""))
            transaction.addToBackStack(addToBackStackTag);
        transaction.commit();

    }


    /**
     * Replace a fragment with another one using shared elements.
     *
     * @param endFragment
     * @param args
     * @param addToBackStackTag
     * @param textView
     */
    public void replaceFragmentSharedElements(Fragment endFragment, Bundle args, String addToBackStackTag, TextView textView) {
        if (args != null) {
            endFragment.setArguments(args);
        }
        // get current Fragment
        Fragment startFragment = getFragmentManager().findFragmentById(R.id.current_fragment);
        if (args != null) {
            endFragment.setArguments(args);
        }
        // add Exit transition
        /*
        startFragment.setExitTransition(TransitionInflater.from(
                this).inflateTransition(android.R.transition.fade));
        // add Enter transition
        endFragment.setEnterTransition(TransitionInflater.from(this).
                inflateTransition(android.R.transition.fade));
        */
        // Create new transaction and add to back stack
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
//        transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
        transaction.replace(R.id.current_fragment, endFragment);
        if (!addToBackStackTag.equals(""))
            transaction.addToBackStack(addToBackStackTag);
//        if (args.getString("TRANS_TEXT") != null) {
//            transaction.addSharedElement(textView, args.getString("TRANS_TEXT"));
//            //system.out.println("lol");
//        }

        transaction.commit();

    }


    public void isTherePublicSession() {
        Log.d("Session", "checking if there is public session");
        final String sessionID = SharedPreferencesHelper.isThereOngoingPublicSession(this);
        if (sessionID != null) {
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Foi Encontrada uma Sessão a decorrer");
            alertDialog.setMessage("Deseja retomar a Sessão que tinha em curso?");
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Sim",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Constants.SESSION_ID = sessionID;
                            FragmentManager fragmentManager = getFragmentManager();
                            fragmentManager.beginTransaction()
                                    .replace(R.id.current_fragment, new CGAPublicBottomButtons())
                                    .commit();
                        }
                    });
            final SharedPreferences finalSharedPreferences1 = sharedPreferences;
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Não",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // erase the sessionID
                            SharedPreferencesHelper.resetPublicSession(context, sessionID);
                        }
                    });
            alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    // erase the sessionID
                    SharedPreferencesHelper.resetPublicSession(context, sessionID);
                }
            });
            alertDialog.show();
        }

    }
}