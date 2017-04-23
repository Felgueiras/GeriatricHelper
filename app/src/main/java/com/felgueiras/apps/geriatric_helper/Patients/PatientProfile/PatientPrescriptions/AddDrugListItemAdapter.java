package com.felgueiras.apps.geriatric_helper.Patients.PatientProfile.PatientPrescriptions;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.felgueiras.apps.geriatric_helper.Constants;
import com.felgueiras.apps.geriatric_helper.Firebase.RealtimeDatabase.PatientFirebase;
import com.felgueiras.apps.geriatric_helper.R;

import java.util.ArrayList;

public class AddDrugListItemAdapter extends RecyclerView.Adapter<AddDrugListItemAdapter.DrugViewHolder> {

    /**
     * Drug names.
     */
    private ArrayList<String> addedDrugsList = new ArrayList<>();
    /**
     * Drug notes.
     */
    private ArrayList<String> addedDrugsNotes = new ArrayList<>();
    /**
     * Adapter.
     */
    private final RecyclerView.Adapter<DrugViewHolder> adapter;
    private Activity context;


    /**
     * Get the added drugs.
     *
     * @return
     */
    public ArrayList<String> getAddedDrugsList() {
        if (addedDrugsList.get(addedDrugsList.size() - 1).equals("")) {
            addedDrugsList.remove(addedDrugsList.size() - 1);
        }
        return addedDrugsList;
    }

    /**
     * Get the notes added for each drug.
     *
     * @return
     */
    public ArrayList<String> getAddedDrugsNotes() {
        if (addedDrugsNotes.get(addedDrugsNotes.size() - 1).equals("")) {
            addedDrugsNotes.remove(addedDrugsNotes.size() - 1);
        }
        return addedDrugsList;
    }

    /**
     * Create a View
     */
    class DrugViewHolder extends RecyclerView.ViewHolder {


        private final AutoCompleteTextView autoCompleteTextView;
        private final Button addMoreButton;
        private final ImageButton cancelPrescription;
        private final EditText drugNotes;

        public DrugViewHolder(View view) {
            super(view);
            autoCompleteTextView = (AutoCompleteTextView) view.findViewById(R.id.autocompleteDrugName);
            addMoreButton = (Button) view.findViewById(R.id.addMoreItem);
            cancelPrescription = (ImageButton) view.findViewById(R.id.cancelPrescription);
            drugNotes = (EditText) view.findViewById(R.id.drugNotes);

        }
    }

    /**
     * Constructor of the SessionCardEvaluationHistory
     *
     * @param context
     * @param drugs
     * @param patient
     */
    AddDrugListItemAdapter(Activity context, ArrayList<String> drugs, PatientFirebase patient) {
        this.context = context;
        ArrayList<String> allDrugs = new ArrayList<>();
        allDrugs.addAll(drugs);
        // append first dummy item
        addedDrugsList.add("");
        addedDrugsNotes.add("");

        adapter = this;
    }

    @Override
    public DrugViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.prescription_add, parent, false);
        return new DrugViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final DrugViewHolder holder, int position) {

        holder.autoCompleteTextView.setText(addedDrugsList.get(position));
        // setup autocomplete
        holder.autoCompleteTextView.setAdapter(new AutoCompleteAdapter(
                context,
                Constants.allDrugs
        ));

        holder.autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                addedDrugsList.set(holder.getAdapterPosition(), s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        holder.drugNotes.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                addedDrugsNotes.set(holder.getAdapterPosition(), s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // add one more item to the list
        holder.addMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // add one more item to the list
                addedDrugsList.add("");
                addedDrugsNotes.add("");
                adapter.notifyDataSetChanged();
            }
        });

        // cancel the current item, removing it from the list
        holder.cancelPrescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // add one more item to the list
                addedDrugsList.remove(holder.getAdapterPosition());
                addedDrugsNotes.remove(holder.getAdapterPosition());
                adapter.notifyDataSetChanged();
            }
        });



        if (position < addedDrugsList.size() - 1) {
            holder.addMoreButton.setVisibility(View.GONE);
        } else {
            holder.addMoreButton.setVisibility(View.VISIBLE);
        }

    }


    @Override
    public int getItemCount() {
        return addedDrugsList.size();
    }


}
