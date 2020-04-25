package nomeGruppo.eathome.actions;

import java.util.Calendar;
import java.util.Date;

public class Feedback {

    public String idFeedback;
    public int voteFeedback;
    public String textFeedback;
    public String dateFeedback;
    public String idClientFeedback;
    public String idPlaceFeedback;


    public Feedback() {
    }

    public Feedback(String idFeedback, int voteFeedback, String textFeedback, String dateFeedback, String idClientFeedback, String idPlaceFeedback) {
        this.idFeedback = idFeedback;
        this.voteFeedback = voteFeedback;
        this.textFeedback = textFeedback;
        this.dateFeedback = dateFeedback;
        this.idClientFeedback = idClientFeedback;
        this.idPlaceFeedback = idPlaceFeedback;
    }
}
