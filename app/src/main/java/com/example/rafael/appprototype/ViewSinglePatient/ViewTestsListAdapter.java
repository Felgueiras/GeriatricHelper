package com.example.rafael.appprototype.ViewSinglePatient;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.rafael.appprototype.Constants;
import com.example.rafael.appprototype.DataTypes.DB.GeriatricTest;
import com.example.rafael.appprototype.R;

import java.util.List;

public class ViewTestsListAdapter extends BaseAdapter {
    private final List<GeriatricTest> geriatricTests;
    private static LayoutInflater inflater = null;


    public ViewTestsListAdapter(DisplayRecordFragment viewTests, List<GeriatricTest> geriatricTests) {
        this.geriatricTests = geriatricTests;
        inflater = (LayoutInflater) viewTests.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return geriatricTests.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class Holder {
        TextView name;
        TextView result;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder = new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.list_tests, null);
        holder.name = (TextView) rowView.findViewById(R.id.testName);
        holder.result = (TextView) rowView.findViewById(R.id.testResult);
        holder.name.setText(geriatricTests.get(position).getType());
        holder.result.setText(geriatricTests.get(position).getResult()+"");
        rowView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent i = new Intent(context.getActivity(),ViewRecord.class);
                Bundle b = new Bundle();
                b.putSerializable(Constants.test, geriatricTests.get(position));
                //i.putExtras(b);
                //ViewTest viewTest = (ViewTest) context;
                // viewTest.startActivity(i);
            }
        });
        return rowView;
    }

}