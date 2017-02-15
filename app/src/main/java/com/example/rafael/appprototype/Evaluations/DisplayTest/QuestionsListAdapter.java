package com.example.rafael.appprototype.Evaluations.DisplayTest;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.rafael.appprototype.Constants;
import com.example.rafael.appprototype.DataTypes.DB.Choice;
import com.example.rafael.appprototype.DataTypes.NonDB.ChoiceNonDB;
import com.example.rafael.appprototype.DataTypes.NonDB.GeriatricTestNonDB;
import com.example.rafael.appprototype.DataTypes.NonDB.GradingNonDB;
import com.example.rafael.appprototype.DataTypes.NonDB.QuestionCategory;
import com.example.rafael.appprototype.DataTypes.NonDB.QuestionNonDB;
import com.example.rafael.appprototype.DataTypes.DB.GeriatricTest;
import com.example.rafael.appprototype.DataTypes.DB.Question;
import com.example.rafael.appprototype.Evaluations.DisplayTest.SingleQuestion.MultipleChoiceHandler;
import com.example.rafael.appprototype.Evaluations.DisplayTest.SingleQuestion.RightWrongQuestionHandler;
import com.example.rafael.appprototype.Evaluations.DisplayTest.SingleQuestion.YesNoQuestionHandler;
import com.example.rafael.appprototype.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;

/**
 * Create the layout of the Questions
 */
public class QuestionsListAdapter extends BaseAdapter {
    /**
     * Questions for a Test
     */
    private final ArrayList<QuestionNonDB> questions;
    private static LayoutInflater inflater = null;
    /**
     * Current Context
     */
    private final Context context;
    /**
     * Test
     */
    private final GeriatricTest test;
    /**
     * Yes if test already opened, no otherwise
     */
    private final boolean testAlreadyOpened;
    private final GeriatricTestNonDB testNonDB;
    /**
     * HasSet that will hold the numbers of the Questions that were already answered.
     */
    private HashSet<Integer> positionsFilled = new HashSet<>();

    boolean allQuestionsAnswered = false;

    int numquestions;
    private View questionView;


    /**
     * Display all Questions for a GeriatricTest
     *
     * @param context current Context
     * @param test    GeriatricTest that is being filled up
     */
    public QuestionsListAdapter(Context context, GeriatricTestNonDB testNonDb, GeriatricTest test) {
        this.context = context;
        this.questions = testNonDb.getQuestions();
        this.test = test;
        this.testNonDB = testNonDb;


        numquestions = questions.size();
        testAlreadyOpened = test.isAlreadyOpened();
        inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        if (testNonDB.isSingleQuestion()) {
            // single question test
            numquestions = 1;
            return numquestions;
            //return testNonDB.getScoring().getValuesBoth().size();
        }
        if (testNonDB.getQuestionsCategories().size() != 0) {
            // test with multiple categories
            numquestions = testNonDB.getNumberQuestions();
            return numquestions;
        }
        return numquestions;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Signal that a given Question was already answered
     *
     * @param position position of the answered Question
     */
    public void questionAnswered(int position) {
        positionsFilled.add(position);
        int nq = numquestions;
        if (testNonDB.getScoring().isDifferentMenWomen()) {
            if (Constants.SESSION_GENDER == Constants.MALE) {
                nq = 0;
                for (QuestionNonDB questionNonDB : testNonDB.getQuestions()) {
                    if (!questionNonDB.isOnlyForWomen())
                        nq++;
                }
            }
        }

        if (positionsFilled.size() == nq) {
            if (!test.isCompleted())
                Snackbar.make(questionView, R.string.all_questions_answered, Snackbar.LENGTH_SHORT).show();
            allQuestionsAnswered = true;

            // write that to DB
            test.setCompleted(true);
            test.save();
        }
    }

    public class Holder {
        TextView question;
        ListView choicesList;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (testNonDB.isMultipleChoice()) {
            QuestionNonDB currentQuestionNonDB = questions.get(position);
            questionView = multipleChoice(currentQuestionNonDB, position);
            if (currentQuestionNonDB.isOnlyForWomen() && Constants.SESSION_GENDER == Constants.MALE) {
                return inflater.inflate(R.layout.empty, null);
            }
            return questionView;
        }

        questionView = null;
        if (!testAlreadyOpened) {
            // single question
            if (testNonDB.isSingleQuestion()) {
                questionView = singleChoice();
            } else if (testNonDB.isMultipleCategories()) {
                questionView = new QuestionMultipleCategories(inflater, testNonDB, context, test,
                        this).multipleCategoriesNotOpened();
            } else {
                QuestionNonDB currentQuestionNonDB = questions.get(position);
                System.out.println(currentQuestionNonDB.isRightWrong() + "aaaa");
                // yes/no questions
                if (currentQuestionNonDB.isYesOrNo()) {
                    questionView = yesNoNotOpened(currentQuestionNonDB, position);
                }

                // right/wrong
                if (currentQuestionNonDB.isRightWrong()) {
                    questionView = rightWrong(currentQuestionNonDB, position);
                }
            }

        }
        // Test already opened
        else {
            if (testNonDB.isSingleQuestion()) {
                questionView = singleChoice();
            } else if (testNonDB.isMultipleCategories()) {
                questionView = new QuestionMultipleCategories(inflater, testNonDB, context, test,
                        this).multipleCategoriesAlreadyOpened();
            } else {
                QuestionNonDB currentQuestionNonDB = questions.get(position);
                // right/wrong
                if (currentQuestionNonDB.isRightWrong()) {
                    questionView = rightWrong(currentQuestionNonDB, position);
                }
                // check if question is multiple choice or yes/no
                if (currentQuestionNonDB.isYesOrNo()) {
                    questionView = yesNoAlreadyOpened(currentQuestionNonDB, position);
                }
            }

        }
        System.out.println(questionView);
        return questionView;
    }

    /**
     * Right or wrong question.
     *
     * @param currentQuestionNonDB
     * @param questionIndex
     * @return
     */
    private View rightWrong(QuestionNonDB currentQuestionNonDB, int questionIndex) {
        // question in DB
        Question questionInDB;
        String dummyID = test.getGuid() + "-" + currentQuestionNonDB.getDescription();
        questionInDB = Question.getQuestionByID(dummyID);
        if (questionInDB == null) {
            // create question and add to DB
            questionInDB = new Question();
            questionInDB.setGuid(dummyID);
            questionInDB.setDescription(currentQuestionNonDB.getDescription());
            questionInDB.setTest(test);
            questionInDB.setYesOrNo(false);
            questionInDB.setRightWrong(true);
            questionInDB.save();
        }


        /**
         * Set View
         */
        LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View questionView = infalInflater.inflate(R.layout.content_question_right_wrong, null);
        TextView questionName = (TextView) questionView.findViewById(R.id.nameQuestion);
        questionName.setText((questionIndex + 1) + " - " + currentQuestionNonDB.getDescription());

        RadioGroup radioGroup = (RadioGroup) questionView.findViewById(R.id.radioGroup);

        // if question is already answered
        if (questionInDB.isAnswered()) {
            ////system.out.println(questionInDB.toString());
            if (questionInDB.getSelectedRightWrong().equals("right")) {
                radioGroup.check(R.id.rightChoice);
            } else {
                radioGroup.check(R.id.wrongChoice);
            }
        }

        // detect when choice changed
        radioGroup.setOnCheckedChangeListener(new RightWrongQuestionHandler(questionInDB, this, testNonDB, questionIndex));

        return questionView;
    }

    /**
     * Single choice test.
     *
     * @return
     */
    private View singleChoice() {
        View questionView = inflater.inflate(R.layout.content_question_single_multiple_choice, null);

        // create Choices and add to DB
        final ArrayList<GradingNonDB> gradings = testNonDB.getScoring().getValuesBoth();
        testNonDB.getScoring().getValuesBoth();
        //ArrayList<ChoiceNonDB> choicesNonDB = currentQuestionNonDB.getChoices();
        RadioGroup radioGroup = (RadioGroup) questionView.findViewById(R.id.radioGroup);

        for (int i = 0; i < gradings.size(); i++) {
            RadioButton newRadioButton = new RadioButton(context);
            GradingNonDB currentGrading = gradings.get(i);
            String grade = currentGrading.getGrade();
            String description = currentGrading.getDescription();
            newRadioButton.setText(description);

            LinearLayout.LayoutParams layoutParams = new RadioGroup.LayoutParams(
                    RadioGroup.LayoutParams.WRAP_CONTENT,
                    RadioGroup.LayoutParams.WRAP_CONTENT);

            radioGroup.addView(newRadioButton, i, layoutParams);
            if (test.isCompleted())
                if (test.getAnswer().equals(grade))
                    newRadioButton.setChecked(true);
        }


        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int radioButtonIndex) {

                radioButtonIndex = (radioButtonIndex - 1) % gradings.size();
                // get grade
                GradingNonDB grading = gradings.get(radioButtonIndex);
                test.setAlreadyOpened(true);
                test.setAnswer(grading.getGrade());
                test.setCompleted(true);
                test.save();
            }
        });

        return questionView;
    }

    /**
     * Add a RadioButton to a RadioGroup.
     *
     * @param choice
     * @param radioGroup
     * @param i
     * @return
     */
    private RadioButton addRadioButton(Choice choice, RadioGroup radioGroup, int i) {
        RadioButton newRadioButton = new RadioButton(context);
        if (Objects.equals(choice.getName(), "") || choice.getName() == null) {
            newRadioButton.setText(choice.getDescription());
        } else {
            if (!choice.getName().equals(choice.getDescription()))
                newRadioButton.setText(choice.getName() + " - " + choice.getDescription());
            else
                newRadioButton.setText(choice.getDescription());

        }

        LinearLayout.LayoutParams layoutParams = new RadioGroup.LayoutParams(
                RadioGroup.LayoutParams.WRAP_CONTENT,
                RadioGroup.LayoutParams.WRAP_CONTENT);
        radioGroup.addView(newRadioButton, i, layoutParams);
        return newRadioButton;
    }


    /**
     * Multiple Choice Qustion, already opened before
     *
     * @param currentQuestionNonDB
     * @param position
     * @return
     */
    public View multipleChoice(QuestionNonDB currentQuestionNonDB, int position) {

        View questionView = inflater.inflate(R.layout.content_question_multiple_choice, null);

        RadioGroup radioGroup;
        // check if is already in DB
        String questionID = test.getGuid() + "-" + currentQuestionNonDB.getDescription();
        Question question = Question.getQuestionByID(questionID);
        if (question == null) {
            // create question and add to DB
            question = new Question();
            question.setDescription(currentQuestionNonDB.getDescription());
            question.setGuid(questionID);
            question.setTest(test);
            question.setYesOrNo(false);
            question.save();

            // create Choices and add to DB
            ArrayList<ChoiceNonDB> choicesNonDB = currentQuestionNonDB.getChoices();
            radioGroup = (RadioGroup) questionView.findViewById(R.id.radioGroup);

            for (int i = 0; i < choicesNonDB.size(); i++) {
                ChoiceNonDB currentChoice = choicesNonDB.get(i);
                Choice choice = new Choice();
                String choiceID = test.getGuid() + "-" + currentQuestionNonDB.getDescription() + "-" + currentChoice.getDescription();
                // check if already in DB
                if (Choice.getChoiceByID(choiceID) == null) {
                    choice.setGuid(choiceID);
                    choice.setQuestion(question);
                    if (currentChoice.getName() != null)
                        choice.setName(currentChoice.getName());
                    choice.setDescription(currentChoice.getDescription());
                    choice.setScore(currentChoice.getScore());
                    choice.save();
                }

                // create RadioButton for that choice
                addRadioButton(choice, radioGroup, i);
            }

        } else {
            // get Question from DB
            question = test.getQuestionsFromTest().get(position);
            // create Radio Group from the info in DB
            radioGroup = (RadioGroup) questionView.findViewById(R.id.radioGroup);

            for (int i = 0; i < question.getChoicesForQuestion().size(); i++) {
                Choice choice = question.getChoicesForQuestion().get(i);

                // create RadioButton for that choice
                addRadioButton(choice, radioGroup, i);
            }
        }


        radioGroup.setOnCheckedChangeListener(new MultipleChoiceHandler(question, this, position));

        if (question.isAnswered()) {
            //system.out.println("answered");
            Holder holder = new Holder();
            holder.question = (TextView) questionView.findViewById(R.id.nameQuestion);
            holder.question.setText((position + 1) + " - " + currentQuestionNonDB.getDescription());
            // set the selected option
            String selectedChoice = question.getSelectedChoice();
            //system.out.println("sel choice is " + selectedChoice);
            ArrayList<Choice> choices = question.getChoicesForQuestion();
            int selectedIdx = -1;
            for (int i = 0; i < choices.size(); i++) {
                if (choices.get(i).getName().equals(selectedChoice)) {
                    selectedIdx = i;
                }
            }
            RadioButton selectedButton = (RadioButton) radioGroup.getChildAt(selectedIdx);
            selectedButton.setChecked(true);
        }

        // Test already opened, but question not answered
        else {
            //system.out.println("not answered");
            Holder holder = new Holder();
            holder.question = (TextView) questionView.findViewById(R.id.nameQuestion);
            holder.question.setText((position + 1) + " - " + currentQuestionNonDB.getDescription());
        }
        return questionView;
    }

    private View yesNoAlreadyOpened(QuestionNonDB currentQuestionNonDB, int position) {
        View questionView = inflater.inflate(R.layout.content_question_yes_no, null);
        Question questionInDB = test.getQuestionsFromTest().get(position);

        if (questionInDB.isAnswered()) {
            Holder holder = new Holder();
            holder.question = (TextView) questionView.findViewById(R.id.nameQuestion);
            holder.question.setText((position + 1) + " - " + currentQuestionNonDB.getDescription());

            RadioGroup radioGroup = (RadioGroup) questionView.findViewById(R.id.radioGroup);
            radioGroup.setOnCheckedChangeListener(new YesNoQuestionHandler(questionInDB, this, position));
            if (questionInDB.getSelectedYesNoChoice().equals("yes")) {
                radioGroup.check(R.id.yesChoice);
            } else {
                radioGroup.check(R.id.noChoice);
            }
        }
        /**
         * Test already opened, but question not answered
         */
        else {
            RadioGroup radioGroup = (RadioGroup) questionView.findViewById(R.id.radioGroup);
            radioGroup.setOnCheckedChangeListener(new YesNoQuestionHandler(questionInDB, this, position));
        }
        return questionView;
    }


    /**
     * Yes/No question, Test not opened before
     *
     * @param currentQuestionNonDB
     * @param position
     */
    private View yesNoNotOpened(QuestionNonDB currentQuestionNonDB, int position) {
        View questionView = inflater.inflate(R.layout.content_question_yes_no, null);
        // create question and add to DB
        Question question = new Question();
        String dummyID = test.getGuid() + "-" + currentQuestionNonDB.getDescription();
        question.setGuid(dummyID);
        question.setDescription(currentQuestionNonDB.getDescription());
        question.setTest(test);
        question.setYesOrNo(true);
        question.setYesValue(currentQuestionNonDB.getYesScore());
        question.setNoValue(currentQuestionNonDB.getNoScore());
        question.save();

        /**
         * Set View
         */
        Holder holder = new Holder();
        holder.question = (TextView) questionView.findViewById(R.id.nameQuestion);
        holder.question.setText((position + 1) + " - " + currentQuestionNonDB.getDescription());
        // detect when choice changed
        RadioGroup radioGroup = (RadioGroup) questionView.findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new YesNoQuestionHandler(question, this, position));
        return questionView;
    }


}