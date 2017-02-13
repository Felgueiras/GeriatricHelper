package com.example.rafael.appprototype.DataTypes.NonDB;


import com.example.rafael.appprototype.DataTypes.DB.Session;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by rafael on 15-09-2016.
 */
public class GeriatricTestNonDB implements Serializable {

    /**
     * Name of the test
     */
    String testName;
    /**
     * Type of test
     */
    String area;
    /**
     * Subcategory if applied
     */
    String subCategory;
    /**
     * Textual field of the procedure.
     */
    String description;
    /**
     * Scoring definition, min and man score and categories of scores.
     */
    ScoringNonDB scoring;
    /**
     * Numerical result of the GeriatricTest
     */
    int result;
    /**
     * Time it takes to be completed.
     */
    int time;
    /**
     * Short area for display purposes.
     */
    String shortName;

    Session session;

    boolean singleQuestion;
    boolean multipleChoice;

    ArrayList<QuestionNonDB> questions = new ArrayList<>();
    private ArrayList<QuestionCategory> questionsCategories;
    private boolean multipleCategories;
    private int numberQuestions;

    public ArrayList<QuestionCategory> getQuestionsCategories() {
        return questionsCategories;
    }

    /**
     * Create a new GenerecTest
     *
     * @param testName    date of the test
     * @param area    area of the test
     * @param subCategory subcategory of the test
     * @param description field of the test
     */
    public GeriatricTestNonDB(String testName, String area, String subCategory, String description) {
        this.area = area;
        this.testName = testName;
        this.subCategory = subCategory;
        this.description = description;
        this.questionsCategories = new ArrayList<>();
    }

    /**
     * Get all questions from a GeriatricTest object.
     *
     * @return
     */
    public ArrayList<QuestionNonDB> getQuestions() {
        if (questionsCategories.size() != 0) {
            // return all questions from the categories
            ArrayList<QuestionNonDB> allQuestions = new ArrayList<>();
            for (QuestionCategory cat : questionsCategories) {
                allQuestions.addAll(cat.getQuestions());
            }
            return allQuestions;
        }
        return questions;
    }

    public void setQuestions(ArrayList<QuestionNonDB> questions) {
        this.questions = questions;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getType() {
        return area;
    }

    public void setType(String type) {
        this.area = type;
    }


    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(String subCategory) {
        this.subCategory = subCategory;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ScoringNonDB getScoring() {
        return scoring;
    }

    public void setScoring(ScoringNonDB scoring) {
        this.scoring = scoring;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    @Override
    public String toString() {
        String ret = "";
        ret += area + " - " + testName + " - " + result;
        return ret;
    }

    /**
     * Add a Question to a Test
     *
     * @param question
     */
    public void addQuestion(QuestionNonDB question) {
        questions.add(question);
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public void addQuestionCategory(QuestionCategory category) {
        this.questionsCategories.add(category);
    }

    public boolean isSingleQuestion() {
        return singleQuestion;
    }

    public void setSingleQuestion(boolean singleQuestion) {
        this.singleQuestion = singleQuestion;
    }

    public void setMultipleCategories(boolean multipleCategories) {
        this.multipleCategories = multipleCategories;
    }

    public boolean isMultipleCategories() {
        return multipleCategories;
    }

    /**
     * Get the number of question
     *
     * @return
     */
    public int getNumberQuestions() {
        int numQuestions = 0;
        for (QuestionCategory category : questionsCategories) {
            numQuestions += category.getQuestions().size();
        }
        return numQuestions;
    }

    public boolean isMultipleChoice() {
        return multipleChoice;
    }

    public void setMultipleChoice(boolean multipleChoice) {
        this.multipleChoice = multipleChoice;
    }
}
