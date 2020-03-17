package nomeGruppo.eathome.actions;

import java.util.Calendar;
import java.util.Date;

public class Feedback {

    private int id; //TODO rendi final
    private int vote;
    private String title;
    private String text;
    private Date date;
    private String answer;
    private final FeedbackType feedbackType;
    private final int idClient;
    private final int idPlace;

    public Feedback(int vote, FeedbackType feedbackType, int idClient, int idPlace){
        //TODO leggi l'ultimo id dal database e incrementa uno
        this.vote = vote;
        this.date = Calendar.getInstance().getTime();
        this.feedbackType = feedbackType;
        this.idClient = idClient;
        this.idPlace = idPlace;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
