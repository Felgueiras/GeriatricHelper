package com.felgueiras.apps.geriatrichelper.Prescription.Beers;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.felgueiras.apps.geriatrichelper.DataTypes.Criteria.RecommendationInfo;
import com.felgueiras.apps.geriatrichelper.R;

import java.util.ArrayList;

public class BeersSingleDrugInfo extends RecyclerView.Adapter<BeersSingleDrugInfo.MyViewHolder>{

    private Activity context;
    /**
     * Data to be displayed.
     */
    private ArrayList<RecommendationInfo> infos;



    /**
     * Create a View
     */
    public class MyViewHolder extends RecyclerView.ViewHolder {


        private final TextView recommendation;
        private final TextView rationale;
        private final TextView qualityOfEvidence;
        private final TextView strengthOfRecommendation;

        public MyViewHolder(View view) {
            super(view);
            // get the views
            recommendation = view.findViewById(R.id.recommendation);
            rationale = view.findViewById(R.id.rationale);
            qualityOfEvidence = view.findViewById(R.id.qualityOfEvidence);
            strengthOfRecommendation = view.findViewById(R.id.strengthOfRecommendation);
        }
    }

    /**
     * Constructor of the SessionCardEvaluationHistory
     *  @param context current context
     * @param infos RecommendationInfos for the drug
     */
    public BeersSingleDrugInfo(Activity context, ArrayList<RecommendationInfo> infos) {
        this.context = context;
        this.infos = infos;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.drug_info_beers_single, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final RecommendationInfo drugInfo = infos.get(position);

        // set the views
        holder.recommendation.setText(context.getString(R.string.beers_recommendation) + " " + drugInfo.getRecommendation());
        holder.rationale.setText(context.getString(R.string.beers_rationale) + " " + drugInfo.getRationale());
        holder.qualityOfEvidence.setText(context.getString(R.string.beers_quality_evidence) + " " + drugInfo.getQualityOfEvidence());
        holder.strengthOfRecommendation.setText(context.getString(R.string.beers_strength_recommendation) + " " + drugInfo.getStrengthOfRecommendation());

    }


    @Override
    public int getItemCount() {
        return infos.size();
    }


}
