package com.example.rafael.appprototype.DrugPrescription;

/**
 * Created by rafael on 01-11-2016.
 */
public class PrescriptionGeneral {

    String drugName;

    public PrescriptionGeneral(String name) {
        this.drugName = name;
    }

    public String getDrugName() {
        return drugName;
    }

    public void setDrugName(String drugName) {
        this.drugName = drugName;
    }
}
