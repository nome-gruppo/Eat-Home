package nomeGruppo.eathome.actions;

import java.util.Calendar;
import java.util.Date;

public class Feedback {

    public String idFeedback;
    public float voteFeedback;
    public String textFeedback;
    public String dateFeedback;
    public String idClientFeedback;
    public String idPlaceFeedback;
    public String clientNameFeedback;


    public Feedback() {
    }

    public Feedback(String idFeedback, float voteFeedback, String textFeedback, String dateFeedback, String idClientFeedback, String idPlaceFeedback) {
        this.idFeedback = idFeedback;
        this.voteFeedback = voteFeedback;
        this.textFeedback = textFeedback;
        this.dateFeedback = dateFeedback;
        this.idClientFeedback = idClientFeedback;
        this.idPlaceFeedback = idPlaceFeedback;
    }

    public void setIdFeedback(String idFeedback) {
        this.idFeedback = idFeedback;
    }

    public void setVoteFeedback(float voteFeedback) {
        this.voteFeedback = voteFeedback;
    }

    public void setTextFeedback(String textFeedback) {
        this.textFeedback = textFeedback;
    }

    public void setDateFeedback(String dateFeedback) {
        this.dateFeedback = dateFeedback;
    }

    public void setIdClientFeedback(String idClientFeedback) {
        this.idClientFeedback = idClientFeedback;
    }

    public void setIdPlaceFeedback(String idPlaceFeedback) {
        this.idPlaceFeedback = idPlaceFeedback;
    }

    public void setClientNameFeedback(String clientNameFeedback) {
        this.clientNameFeedback = clientNameFeedback;
    }
}
