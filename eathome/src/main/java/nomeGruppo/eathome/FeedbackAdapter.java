package nomeGruppo.eathome;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

import nomeGruppo.eathome.actions.Feedback;

public class FeedbackAdapter  extends ArrayAdapter<Feedback> {


    public FeedbackAdapter(@NonNull Context context, int resource, List<Feedback> list) {
        super(context, resource, list);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listitem_feedback, null);

            final RatingBar ratingBar = convertView.findViewById(R.id.listitem_feedback_ratingBar);
            final TextView nameTW = convertView.findViewById(R.id.listitem_feedback_name);
            final TextView dateTW = convertView.findViewById(R.id.listitem_feedback_date);
            final TextView textTW = convertView.findViewById(R.id.listitem_feedback_tw_text);

            final Feedback mFeedback = getItem(position);

            ratingBar.setRating(mFeedback.voteFeedback);
            nameTW.setText(mFeedback.clientNameFeedback);
            dateTW.setText(mFeedback.dateFeedback);
            textTW.setText(mFeedback.textFeedback);


        }


        return convertView;
    }

}
