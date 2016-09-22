package com.sifast.appsocle.views;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.sifast.appsocle.R;
import com.sifast.appsocle.models.Feedback;

import java.util.Collections;
import java.util.List;

/**
 * Created by Asus on 15/07/2016.
 */
public class FeedbackListFragment extends Fragment {

    private List<Feedback> feedbackList;

    public FeedbackListFragment(List<Feedback> feedbackList) {
        this.feedbackList = feedbackList;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragfeedback, container, false);
        feedbackList = this.getFeedbackList();
        //link the adapter to the list view
        Collections.reverse(feedbackList);
        ArrayAdapter<Feedback> adapter = new FeedbackAdapter(getActivity(), feedbackList);
        ListView listView = (ListView) view.findViewById(R.id.list);
        listView.setAdapter(adapter);
        return view;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    public List<Feedback> getFeedbackList() {
        return feedbackList;
    }

    public void setFeedbackList(List<Feedback> feedbackList) {
        this.feedbackList = feedbackList;
    }
}
