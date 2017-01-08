package com.example.rafael.appprototype.Evaluations.NewEvaluation.DisplayTest.SingleQuestion;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.rafael.appprototype.DataTypes.DB.Question;
import com.example.rafael.appprototype.Evaluations.NewEvaluation.DisplayTest.SingleTest.ViewQuestionsListAdapter;

import java.util.ArrayList;

/**
 * Create the layout of the Questions
 */
public class MultipleChoiceHandler implements RadioGroup.OnCheckedChangeListener {

    private static LayoutInflater inflater = null;
    private final Question question;
    private final ViewQuestionsListAdapter adapter;
    private int position;


    public MultipleChoiceHandler(Question question, ViewQuestionsListAdapter adapter, int position) {
        this.question = question;
        this.adapter = adapter;
        this.position = position;
    }


    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
        // check which button was selected
        int count = radioGroup.getChildCount();
        for (int i = 0; i < count; i++) {
            View o = radioGroup.getChildAt(i);
            if (o instanceof RadioButton) {
                if (((RadioButton) o).isChecked()) {
                    // save the text of the option
                    question.setSelectedChoice(((RadioButton) o).getText().toString());
                    question.setSelectedIndex(i);
                }
            }
        }
        question.setAnswered(true);
        question.save();
        /**
         * Signal that que Question was answered
         */
        adapter.questionAnswered(position);
    }


}