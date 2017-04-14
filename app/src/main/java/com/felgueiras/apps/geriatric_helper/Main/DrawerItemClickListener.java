package com.felgueiras.apps.geriatric_helper.Main;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.MenuItem;

import com.felgueiras.apps.geriatric_helper.AboutFragment;
import com.felgueiras.apps.geriatric_helper.CGAGuide.CGAGuideMain;
import com.felgueiras.apps.geriatric_helper.Help_Feedback.HelpMainFragment;
import com.felgueiras.apps.geriatric_helper.Sessions.AllAreas.CGAPublic;
import com.felgueiras.apps.geriatric_helper.Sessions.SessionsHistoryMainFragment;
import com.felgueiras.apps.geriatric_helper.Sessions.AllAreas.CGAPrivate;
import com.felgueiras.apps.geriatric_helper.Sessions.AllAreas.CGAPublicInfo;
import com.felgueiras.apps.geriatric_helper.Help_Feedback.SendFeedback;
import com.felgueiras.apps.geriatric_helper.PersonalAreaAccess.LoginFragmentFirebase;
import com.felgueiras.apps.geriatric_helper.Settings;
import com.felgueiras.apps.geriatric_helper.Patients.PatientsMain;
import com.felgueiras.apps.geriatric_helper.Prescription.PrescriptionMainFragment;
import com.felgueiras.apps.geriatric_helper.R;
import com.felgueiras.apps.geriatric_helper.HelpersHandlers.SharedPreferencesHelper;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Handle the selection of an item from the NaviagtionDrawer
 */
public class DrawerItemClickListener implements NavigationView.OnNavigationItemSelectedListener {


    private final FragmentManager fragmentManager;
    private final DrawerLayout drawer;
    private final Activity context;

    public DrawerItemClickListener(Activity context, FragmentManager fragmentManager, DrawerLayout drawer) {
        this.fragmentManager = fragmentManager;
        this.drawer = drawer;
        this.context = context;

    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        // ToolbarHelper.hideBackButton(context);

//        if (id == R.id.options) {
//            Intent i = new Intent(context, Settings.class);
//            context.startActivity(i);
//            return true;
//        }


        Fragment endFragment = null;

        if (id == R.id.access_personal_area) {
            // check if there is any on-going session
            String sessionID = SharedPreferencesHelper.isThereOngoingPublicSession(context);
            if (sessionID != null) {
                SharedPreferencesHelper.resetPublicSession(context, sessionID);
            }
            endFragment = new LoginFragmentFirebase();

        } else if (id == R.id.cga_public) {
            /**
             * Check if there's an ongoing session.
             */
            String sessionID = SharedPreferencesHelper.isThereOngoingPublicSession(context);
            if (sessionID != null)
                endFragment = new CGAPublic();
            else
                endFragment = new CGAPublicInfo();
        } else if (id == R.id.prescription) {
            endFragment = new PrescriptionMainFragment();
        } else if (id == R.id.patients) {
            String sessionID = SharedPreferencesHelper.isThereOngoingPrivateSession(context);
            if (sessionID != null)
                endFragment = new CGAPrivate();
            else
                endFragment = new PatientsMain();
        } else if (id == R.id.sendFeedback) {
            endFragment = new SendFeedback();
        } else if (id == R.id.mainArea) {
            endFragment = new PrivateAreaMainFragment();
        } else if (id == R.id.help) {
            endFragment = new HelpMainFragment();
        } else if (id == R.id.sessions) {
            endFragment = new SessionsHistoryMainFragment();
        } else if (id == R.id.about) {
            endFragment = new AboutFragment();
        } else if (id == R.id.cga_guide) {
            endFragment = new CGAGuideMain();
        }
//        else if (id == R.id.transfer_db) {
//            endFragment = new TransferDB();
//        }
        else if (id == R.id.settings) {
            Intent i = new Intent(context, Settings.class);
            context.startActivity(i);
            return true;
        }

        if (id == R.id.logout) {
            final FirebaseAuth auth = FirebaseAuth.getInstance();

            // Insert the fragment by replacing any existing fragment
            Log.d("Logout", "Going to logout...");
            AlertDialog alertDialog = new AlertDialog.Builder(context).create();
            //alertDialog.setTitle(getResources().getString(R.string.session_discard));
            alertDialog.setMessage(context.getResources().getString(R.string.logout_choice));
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, context.getResources().getString(R.string.yes),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //sign out method
                            auth.signOut();
                            Intent intent = new Intent(context, PublicAreaActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            context.startActivity(intent);
                            context.finish();
                        }
                    });
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, context.getResources().getString(R.string.no),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        } else {
            // add Exit transition

//            Fragment startFragment = context.getFragmentManager().findFragmentById(R.id.current_fragment);
//            startFragment.setExitTransition(TransitionInflater.from(context).inflateTransition(android.R.transition.fade));
//            // add Enter transition
//            endFragment.setEnterTransition(TransitionInflater.from(context).
//                    inflateTransition(android.R.transition.fade));

            // empty back stack
//            BackStackHandler.clearBackStack();
            fragmentManager.beginTransaction()
                    .replace(R.id.current_fragment, endFragment, "initial_tag")
                    .commit();
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}