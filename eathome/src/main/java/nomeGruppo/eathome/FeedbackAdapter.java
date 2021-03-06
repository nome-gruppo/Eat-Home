package nomeGruppo.eathome;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.List;

import nomeGruppo.eathome.actions.Feedback;
import nomeGruppo.eathome.db.FirebaseConnection;
import nomeGruppo.eathome.placeSide.profile.PlaceMyFeedbackActivity;

/*
adapter per le recensioni
 */

public class FeedbackAdapter extends ArrayAdapter<Feedback> {

    public FeedbackAdapter(@NonNull Context context, int resource, List<Feedback> list) {
        super(context, resource, list);
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (inflater != null) {
            convertView = inflater.inflate(R.layout.listitem_feedback, parent, false);

            final Button btnReply = convertView.findViewById(R.id.btnReplyFeedback);
            final RatingBar ratingBar = convertView.findViewById(R.id.listitem_feedback_ratingBar);
            final TextView nameTW = convertView.findViewById(R.id.listitem_feedback_name);
            final TextView dateTW = convertView.findViewById(R.id.listitem_feedback_date);
            final TextView textTW = convertView.findViewById(R.id.listitem_feedback_tw_text);

            final Feedback mFeedback = getItem(position);

            if (mFeedback != null) {
                ratingBar.setRating(mFeedback.voteFeedback);
                nameTW.setText(mFeedback.clientNameFeedback);
                dateTW.setText(mFeedback.dateFeedback);
                textTW.setText(mFeedback.textFeedback);

                if (getContext().getClass() == PlaceMyFeedbackActivity.class) {//se è un place che visualizza le recensioni rendo visibile il bottone per rispondere
                    btnReply.setVisibility(View.VISIBLE);
                }

                btnReply.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openDialogReplyFeedback(mFeedback);
                    }
                });
            }
        }

        return convertView;
    }

    private void openDialogReplyFeedback(final Feedback mFeedback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        Activity activity = (Activity) getContext();
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_reply_feedback, (ViewGroup) ((Activity) getContext()).getCurrentFocus(), false);

        final EditText editReply = view.findViewById(R.id.editReplyFeedback);

        builder.setView(view).setTitle(getContext().getResources().getString(R.string.reply)).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        }).setPositiveButton(getContext().getResources().getString(R.string.reply), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String reply = editReply.getText().toString();
                mFeedback.setReplyPlace(reply);
                addReplyOnFirebase(reply, mFeedback.idFeedback);
                Toast.makeText(getContext(), getContext().getResources().getString(R.string.answer_added), Toast.LENGTH_SHORT).show();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void addReplyOnFirebase(String reply, String id) {
        FirebaseConnection firebaseConnection = new FirebaseConnection();
        firebaseConnection.getmDatabase().child(FirebaseConnection.FEEDBACK_NODE).child(id).child("replyPlace").setValue(reply);
    }

}
