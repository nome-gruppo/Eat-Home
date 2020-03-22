package nomeGruppo.eathome.actions;

import java.util.Calendar;
import java.util.Date;

public class Feedback {

    private int idFeedback; //TODO rendi final
    private int voteFeedback;
    private String titleFeedback;
    private String textFeedback;
    private Date dateFeedback;
    private String answerFeedback;
    private final FeedbackType feedbackTypeFeedback;
    private final int idClientFeedback;
    private final int idPlaceFeedback;

    public Feedback(int vote, FeedbackType feedbackType, int idClient, int idPlace){
        //TODO leggi l'ultimo id dal database e incrementa uno
        this.voteFeedback = vote;
        this.dateFeedback = Calendar.getInstance().getTime();
        this.feedbackTypeFeedback = feedbackType;
        this.idClientFeedback = idClient;
        this.idPlaceFeedback = idPlace;
    }

    public void setText(String text) {
        this.textFeedback = text;
    }

    public void setTitle(String title) {
        this.titleFeedback = title;
    }

    public void setAnswer(String answer) {
        this.answerFeedback = answer;
    }
}
