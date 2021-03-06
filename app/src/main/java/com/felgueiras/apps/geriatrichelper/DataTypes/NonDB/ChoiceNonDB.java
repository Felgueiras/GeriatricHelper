package com.felgueiras.apps.geriatrichelper.DataTypes.NonDB;


import com.felgueiras.apps.geriatrichelper.DataTypes.DB.Question;

import java.io.Serializable;

/**
 * Created by rafael on 30-09-2016.
 */
public class ChoiceNonDB implements Serializable{
    String guid;
    /**
     * Name of the choice.
     */
    String name;
    /**
     * Description of the choice.
     */
    String description;
    /**
     * Score for that choice for that question;
     */
    double score;
    /**
     * Score if yes answer.
     */
    int yes;
    /**
     * Score if no question.
     */
    int no;

    Question question;

    /**
     * Create a new Choice for a Question
     *
     * @param name        date of the choice
     * @param description field
     * @param score       score for that Choice
     */
    public ChoiceNonDB(String name, String description, double score) {
        super();
        this.name = name;
        this.description = description;
        this.score = score;
    }

    public ChoiceNonDB(String description, double score) {
        super();
        this.name = description;
        this.description = description;
        this.score = score;
    }

    public ChoiceNonDB(int yes, int no) {
        super();
        this.yes = yes;
        this.no = no;
    }

    public ChoiceNonDB() {
        super();
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getYes() {
        return yes;
    }

    public void setYes(int yes) {
        this.yes = yes;
    }

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }
}
