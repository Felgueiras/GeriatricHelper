package com.felgueiras.apps.geriatrichelper.CGAGuide;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.felgueiras.apps.geriatrichelper.Constants;
import com.felgueiras.apps.geriatrichelper.Firebase.FirebaseRemoteConfig;
import com.felgueiras.apps.geriatrichelper.R;

import java.io.Serializable;


/**
 * Create the Card for each of the Tests available
 */
public class CGAGuideAreaAdapter extends RecyclerView.Adapter<CGAGuideAreaAdapter.CGACardHolder> {


    private Activity context;


    /**
     * Create a View
     */
    public class CGACardHolder extends RecyclerView.ViewHolder implements Serializable {
        private final RecyclerView scalesIcons;
        private final ImageView areaIcon;
        public TextView name, type, areaInfo;
        public View view;
        public EditText notes;

        public CGACardHolder(View view) {
            super(view);
            name = view.findViewById(R.id.cga_area);
            areaInfo = view.findViewById(R.id.area_info);
            scalesIcons = view.findViewById(R.id.area_scales);
            areaIcon = view.findViewById(R.id.area_icon);
            this.view = view;
        }
    }

    /**
     * Constructor of the SessionCardEvaluationHistory
     *
     * @param context current Context
     */
    public CGAGuideAreaAdapter(Activity context) {
        this.context = context;
    }

    @Override
    public CGACardHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View testCard = LayoutInflater.from(parent.getContext()).inflate(R.layout.cga_guide_area_card, parent, false);
        return new CGACardHolder(testCard);
    }


    @Override
    public void onBindViewHolder(final CGACardHolder holder, int position) {

        final String area = Constants.cga_areas[position];
        holder.name.setText(area);
        switch (area) {
            case Constants.cga_functional:
                holder.areaIcon.setImageResource(R.drawable.ic_functional);
                break;
            case Constants.cga_afective:
                holder.areaIcon.setImageResource(R.drawable.ic_afective);
                break;
            case Constants.cga_nutritional:
                holder.areaIcon.setImageResource(R.drawable.ic_nutritional);
                break;
            case Constants.cga_cognitive:
                holder.areaIcon.setImageResource(R.drawable.ic_cognitive);
                break;
            case Constants.cga_march:
                holder.areaIcon.setImageResource(R.drawable.ic_march);
                break;
        }


        // add info about this area
        String area_text = null;
        switch (area) {
            case Constants.cga_functional:
                area_text = FirebaseRemoteConfig.getString("cga_functional", "");
                break;
            case Constants.cga_afective:
                area_text = FirebaseRemoteConfig.getString("cga_afective", "");
                break;
            case Constants.cga_nutritional:
                area_text = FirebaseRemoteConfig.getString("cga_nutritional", "");
                break;
            case Constants.cga_cognitive:
                area_text = FirebaseRemoteConfig.getString("cga_cognitive", "");
                break;
            case Constants.cga_march:
                area_text = FirebaseRemoteConfig.getString("cga_march", "");
                break;
        }
        holder.areaInfo.setText(area_text);


        /*
          For when the CGA area is selected.
         */
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String selectedArea = (String) holder.name.getText().toString();

                Fragment newFragment = new CGAGuideAreaFragment();
                // add arguments
                Bundle bundle = new Bundle();
                bundle.putString(CGAGuideAreaFragment.CGA_AREA, selectedArea);
                newFragment.setArguments(bundle);
                // setup the transaction
                FragmentTransaction transaction = context.getFragmentManager().beginTransaction();
                Fragment frag = context.getFragmentManager().findFragmentById(R.id.current_fragment);
                transaction.remove(frag);
                transaction.replace(R.id.current_fragment, newFragment);
                transaction.addToBackStack(Constants.tag_guide_area).commit();


            }
        });

//        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(context);
//        boolean showScalesIcon = SP.getBoolean(context.getResources().getString(R.string.areaCardShowScalesIcon), false);
//        if (showScalesIcon) {
//            /**
//             * Display icons for the areas that exist.
//             */
//            LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
//            holder.scalesIcons.setLayoutManager(layoutManager);
//
//            AreaScalesIconsAdapter adapter = new AreaScalesIconsAdapter(context, scalesFromArea, session);
//            holder.scalesIcons.setAdapter(adapter);
//        }

    }

    @Override
    public int getItemCount() {
        return Constants.cga_areas.length;

    }

}
