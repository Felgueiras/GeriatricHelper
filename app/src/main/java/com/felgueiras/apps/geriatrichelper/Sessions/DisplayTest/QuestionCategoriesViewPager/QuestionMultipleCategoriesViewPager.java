package com.felgueiras.apps.geriatrichelper.Sessions.DisplayTest.QuestionCategoriesViewPager;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.support.design.widget.TabLayout;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;

import com.felgueiras.apps.geriatrichelper.DataTypes.NonDB.GeriatricScaleNonDB;
import com.felgueiras.apps.geriatrichelper.Sessions.DisplayTest.QuestionsListAdapter;
import com.felgueiras.apps.geriatrichelper.Firebase.RealtimeDatabase.GeriatricScaleFirebase;
import com.felgueiras.apps.geriatrichelper.R;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by rafael on 08-01-2017.
 */
public class QuestionMultipleCategoriesViewPager implements Serializable{


    private final LayoutInflater inflater;
    private final GeriatricScaleNonDB scaleNonDB;
    GeriatricScaleFirebase scaleDB;
    private final QuestionsListAdapter questionsListAdapter;


    public QuestionMultipleCategoriesViewPager(LayoutInflater inflater,
                                               GeriatricScaleNonDB testNonDB,
                                               Activity context,
                                               GeriatricScaleFirebase test,
                                               QuestionsListAdapter adapter) {
        this.inflater = inflater;
        this.scaleNonDB = testNonDB;
        this.scaleDB = test;
        this.questionsListAdapter = adapter;
    }

    /**
     * Multiple question categories.
     *
     * @return
     */
    public View getView() {
        View questionView = inflater.inflate(R.layout.view_pager, null);

        ViewPager viewPager = questionView.findViewById(R.id.container);
        MyPageAdapter pageAdapter = new MyPageAdapter(questionsListAdapter.getChildFragmentManager());
        setupViewPager(viewPager, pageAdapter);
        viewPager.setAdapter(pageAdapter);

        questionsListAdapter.setViewPagerAux(viewPager);

        TabLayout tabLayout = questionView.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        return questionView;
    }


    public void setupViewPager(ViewPager viewPager, MyPageAdapter adapter) {
        for (int i = 0; i < scaleNonDB.getQuestionsCategories().size(); i++) {
            adapter.addFragment(QuestionCategoryQuestionsAdapter.newInstance(i, scaleNonDB, scaleDB,
                    questionsListAdapter,
                    viewPager));
        }
        viewPager.setAdapter(adapter);

    }

    private class MyPageAdapter extends FragmentPagerAdapter implements Serializable {
        private ArrayList<Fragment> mFragmentList = new ArrayList<>();

        MyPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return this.mFragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return scaleNonDB.getQuestionsCategories().get(position).getName();
        }

        public void addFragment(Fragment fragment) {
            mFragmentList.add(fragment);
        }
    }


}
