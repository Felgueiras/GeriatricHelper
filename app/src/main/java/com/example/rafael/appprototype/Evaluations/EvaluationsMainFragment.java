package com.example.rafael.appprototype.Evaluations;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.GridView;

import com.example.rafael.appprototype.Constants;
import com.example.rafael.appprototype.DataTypes.DB.Session;
import com.example.rafael.appprototype.EmptyStateFragment;
import com.example.rafael.appprototype.CGA.CGAPrivate;
import com.example.rafael.appprototype.Evaluations.EvaluationsHistory.ShowEvaluationsForDay;
import com.example.rafael.appprototype.Main.FragmentTransitions;
import com.example.rafael.appprototype.R;

import java.util.Calendar;

public class EvaluationsMainFragment extends Fragment {
    private int year;
    private int month;
    private int day;

    // TODO search by date, calendar view
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Calendar cal = Calendar.getInstance();
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        day = cal.get(Calendar.DAY_OF_MONTH);


        // Inflate the layout for this fragment
        View view = null;
        if (Constants.sessionID == null) {

            view = inflater.inflate(R.layout.content_evaluations_history, container, false);
            getActivity().setTitle(getResources().getString(R.string.tab_sessions));

            if (Session.getAllSessions().isEmpty()) {
                FragmentManager fragmentManager = getFragmentManager();
                Fragment fragment = new EmptyStateFragment();
                Bundle args = new Bundle();
                args.putString(EmptyStateFragment.MESSAGE, getResources().getString(R.string.no_sessions_history));
                fragment.setArguments(args);
                fragmentManager.beginTransaction()
                        .replace(R.id.frame, fragment)
                        .commit();

                GridView grid = (GridView) view.findViewById(R.id.gridView);
                grid.setVisibility(View.GONE);

            } else {
                // fill the GridView
                GridView gridView = (GridView) view.findViewById(R.id.gridView);
                gridView.setAdapter(new ShowEvaluationsForDay(getActivity()));

            /*
            firstDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new DatePickerDialog(getActivity(), myDateListener, year, month, day).show();
                }
            });
            */

                /**
                 * Calendar view.
                 */
        /*
            v = inflater.inflate(R.layout.content_evaluations_history_calendar, container, false);
            CalendarView calendar = (CalendarView) v.findViewById(R.id.evaluationsCalendar);
            calendar.setFirstDayOfWeek(2);
            calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                @Override
                public void onSelectedDayChange(CalendarView calendarView, int year, int month, int day) {
                    // check if we have evaluations from this date
                    Calendar cal = Calendar.getInstance();
                    cal.set(Calendar.YEAR, year);
                    cal.set(Calendar.MONTH, month);
                    cal.set(Calendar.DAY_OF_MONTH, day--);
                    cal.set(Calendar.HOUR_OF_DAY, 0);
                    cal.set(Calendar.MINUTE, 0);
                    cal.set(Calendar.SECOND, 0);
                    List<Session> sessionsFromDate = Session.getSessionsFromDate(cal.getTime());

                    // display sessions from that day
                }
            });
            */
            }



            // FAB
            FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab_add_evaluation);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog chooseGender;

                    // Strings to Show In Dialog with Radio Buttons
                    final CharSequence[] items = {getString(R.string.male), getString(R.string.female)};

                    // Creating and Building the Dialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle(getString(R.string.select_patient_gender));
                    builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int item) {
                            Bundle args = new Bundle();

                            switch (item) {
                                case 0:
                                    args.putInt(CGAPrivate.GENDER,Constants.MALE);
                                    break;
                                case 1:
                                    args.putInt(CGAPrivate.GENDER,Constants.FEMALE);
                                    break;

                            }
                            dialog.dismiss();
                            // create a new Session - switch to CreatePatient Fragment
                            FragmentTransitions.replaceFragment(getActivity(),new CGAPrivate(), args, Constants.tag_create_session);
                        }
                    });
                    chooseGender = builder.create();
                    chooseGender.show();
                }
            });
        } else {
            /**
             * Resume the ongoing session.
             */
            Bundle args = new Bundle();
            FragmentTransitions.replaceFragment(getActivity(),new CGAPrivate(), args, Constants.tag_create_session);
        }

        return view;
    }


    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            showDate(year, monthOfYear + 1, dayOfMonth);
        }
    };

    private void showDate(int year, int month, int day) {
        //dateView.setText(new StringBuilder().append(day).append("/").append(month).append("/").append(year));
        //system.out.println(year + "");
    }
}

