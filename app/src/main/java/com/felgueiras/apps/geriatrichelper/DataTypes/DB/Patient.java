package com.felgueiras.apps.geriatrichelper.DataTypes.DB;


import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.felgueiras.apps.geriatrichelper.HelpersHandlers.StringHelper;
import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by rafael on 15-09-2016.
 */
@Table(name = "Patients")
public class Patient extends Model implements Serializable {

    @Expose
    @Column(name = "guid", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    public String guid;

    @Expose
    @Column(name = "patientName")
    private String name;

    @Expose
    @Column(name = "birthDate")
    private Date birthDate;

    @Expose
    @Column(name = "gender")
    private int gender;

    @Expose
    @Column(name = "picture")
    private int picture;

    @Expose
    @Column(name = "address")
    private String address;

    @Expose
    @Column(name = "favorite")
    private boolean favorite;

    @Expose
    @Column(name = "notes")
    private String notes;

    @Expose
    @Column(name = "processNumber")
    private String processNumber;

    public Patient(String patientsName, int gender, int image) {
        super();
        this.name = patientsName;
        this.gender = gender;
        this.picture = image;
    }

    public Patient() {
        super();

    }


    public static String[] getPatientsNames(ArrayList<Patient> patients) {
        String[] patientsNames = new String[patients.size()];
        for (int i = 0; i < patientsNames.length; i++) {
            patientsNames[i] = patients.get(i).name;
        }
        return patientsNames;
    }

    /*
    public static int[] getPatientsAges(ArrayList<Patient> patients) {
        int[] patientsAges = new int[patients.size()];
        for (int i = 0; i < patientsAges.length; i++) {
            patientsAges[i] = patients.get(i).age;
        }
        return patientsAges;
    }
    */


    public static int[] getPatientsPhotos(ArrayList<Patient> patients) {
        int[] patientsPhotos = new int[patients.size()];
        for (int i = 0; i < patientsPhotos.length; i++) {
            patientsPhotos[i] = patients.get(i).picture;
        }
        return patientsPhotos;
    }


    public String getName() {
        return name;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public int getPicture() {
        return picture;
    }

    public void setPicture(int picture) {
        this.picture = picture;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    /**
     * Get a list of all the Patients
     *
     * @return
     */
    public static ArrayList<Patient> getAllPatients() {
        List<Patient> list = new Select().from(Patient.class).orderBy("patientName ASC").execute();
        // order by first name - exclude accents

        ArrayList<Patient> patients = new ArrayList<>();
        patients.addAll(list);


        Collections.sort(patients, new Comparator<Patient>() {
            public int compare(Patient o1, Patient o2) {
                String first = StringHelper.removeAccents(o1.getName());
                String second = StringHelper.removeAccents(o2.getName());
                return first.compareTo(second);
            }
        });
        return patients;
    }

    /**
     * Get a list of the favorite Patients
     *
     * @return
     */
    public static ArrayList<Patient> getFavoritePatients() {
        List<Patient> list = new Select().from(Patient.class).where("favorite = ?", true).orderBy("patientName ASC").execute();
        ArrayList<Patient> patients = new ArrayList<>();
        patients.addAll(list);
        return patients;
    }

    /**
     * Get records from PATIENT
     *
     * @return
     */
    public ArrayList<Session> getSessionsFromPatient() {
        List<Session> recordsList = new Select()
                .from(Session.class)
                .where("PATIENT = ?", this.getId())
                .orderBy("date DESC")
                .execute();
        ArrayList<Session> sessions = new ArrayList<>();
        sessions.addAll(recordsList);
        return sessions;
    }

    @Override
    public String toString() {
        return name + " - " + birthDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public boolean isFirstSession() {
        // get all sessions
        List<Session> sessions = getMany(Session.class, "PATIENT");
        return sessions.size() == 1;
    }

    public int getAge() {
        Calendar a = getCalendar(birthDate);
        Calendar b = getCalendar(Calendar.getInstance().getTime());
        int diff = b.get(Calendar.YEAR) - a.get(Calendar.YEAR);
        if (a.get(Calendar.MONTH) > b.get(Calendar.MONTH) ||
                (a.get(Calendar.MONTH) == b.get(Calendar.MONTH) && a.get(Calendar.DATE) > b.get(Calendar.DATE))) {
            diff--;
        }
        return diff;
    }

    public static Calendar getCalendar(Date date) {
        Calendar cal = Calendar.getInstance(Locale.UK);
        cal.setTime(date);
        return cal;
    }

    public void setProcessNumber(String processNumber) {
        this.processNumber = processNumber;
    }

    public String getProcessNumber() {
        return processNumber;
    }
}
