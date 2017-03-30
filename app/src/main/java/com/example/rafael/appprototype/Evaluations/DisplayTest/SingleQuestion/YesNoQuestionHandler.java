package com.example.rafael.appprototype.Evaluations.DisplayTest.SingleQuestion;

import android.view.View;
import android.widget.RadioGroup;

import com.example.rafael.appprototype.DataTypes.DB.Question;
import com.example.rafael.appprototype.Evaluations.DisplayTest.QuestionsListAdapter;
import com.example.rafael.appprototype.R;

/**
 * Created by rafael on 06-10-2016.
 */
public class YesNoQuestionHandler implements RadioGroup.OnCheckedChangeListener {
    /**
     * Question
     */
    private final Question question;
    private final QuestionsListAdapter adapter;
    private final int position;
    private final View questionView;


    public YesNoQuestionHandler(Question question, QuestionsListAdapter adapter, int position, View holder) {
        this.question = question;
        this.adapter = adapter;
        this.position = position;
        this.questionView = holder;
    }


    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        /**
         * Store choice in DB
         */
        if (checkedId == R.id.yesChoice) {
            question.setSelectedYesNoChoice("yes");
        } else if (checkedId == R.id.noChoice) {
            question.setSelectedYesNoChoice("no");
        }
        question.setAnswered(true);
        questionView.setBackgroundResource(R.color.question_answered);
        question.save();
        /**
         * Signal that que Question was answered
         */
        adapter.questionAnswered(position);
    }
}
