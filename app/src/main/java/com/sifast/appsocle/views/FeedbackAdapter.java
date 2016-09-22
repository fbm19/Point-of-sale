package com.sifast.appsocle.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.sifast.appsocle.R;
import com.sifast.appsocle.models.Feedback;

import java.util.List;

/**
 * Created by Asus on 18/07/2016.
 */
public class FeedbackAdapter extends ArrayAdapter<Feedback> {

    private Context context;

    public FeedbackAdapter(Context context, List<Feedback> feedbackList) {

        super(context, R.layout.cardlayout, feedbackList);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View view = layoutInflater.inflate(R.layout.cardlayout, parent, false);
        String feedbackText = getItem(position).getMessageDeclared();
        String feedbackDate = getItem(position).getDeclartionDate();
        TextView txtFeedbackContent = (TextView) view.findViewById(R.id.txtFeedbackContent);
        TextView txtFeedbackDate = (TextView) view.findViewById(R.id.txtFeedbackDate);
        txtFeedbackContent.setText("Feedback :" + feedbackText);
        txtFeedbackDate.setText("Date :" + feedbackDate);
        return view;
    }
}
