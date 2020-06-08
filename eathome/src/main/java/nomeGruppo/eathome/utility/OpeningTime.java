package nomeGruppo.eathome.utility;

import android.app.TimePickerDialog;
import android.content.Context;
import android.widget.Button;
import android.widget.TimePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import nomeGruppo.eathome.R;


public class OpeningTime {
    private TimePickerDialog picker;
    private static final int LENGTH=5;
    private static final String DASH="-";


    public OpeningTime(){

    }

    /**
     * metodo per impostare l'ora di apertura di place
     * @param context contesto dell'activity chiamante
     * @param edit bottone cliccato dall'utente per la modifica dell'orario di apertura
     */
    public void setOpeningTime(final Context context, final Button edit){
        final Calendar cldr = Calendar.getInstance();
        int hour = cldr.get(Calendar.HOUR_OF_DAY);
        int minutes = cldr.get(Calendar.MINUTE);
        // time picker dialog
        picker = new TimePickerDialog(context,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker tp, int sHour, int sMinute) {
                        //String.format() aggiunge uno zero davanti al numero qualora questo avesse un unica cifra
                        edit.setText(context.getResources().getString(R.string.hourPrinted, sHour, sMinute));
                    }
                }, hour, minutes, true);
        picker.show();

    }

    /**
     * metodo per impostare l'ora di chiusura di place
     * @param context contesto dell'activity chiamante
     * @param editOpen bottone che visualizza l'ora di apertura
     * @param editClosed bottone cliccato dall'utente per la modifica dell'ora di chiusura
     */
    public void setOpeningTimeClose(final Context context, final Button editOpen, final Button editClosed){
        String hourMonday=editOpen.getText().toString();
        final Calendar cldr = Calendar.getInstance();
        int hour = cldr.get(Calendar.HOUR_OF_DAY);
        int minutes = cldr.get(Calendar.MINUTE);
        if(hourMonday.length()==LENGTH){//se l'orario di apertura è stato settato allora faccio partire l'orologio da questo orario
            hour=Integer.parseInt(hourMonday.substring(0,2));
            minutes=Integer.parseInt(hourMonday.substring(3));
        }
        // time picker dialog
        picker = new TimePickerDialog(context,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker tp, int sHour, int sMinute) {
                        //String.format() aggiunge uno zero davanti al numero qualora questo avesse un unica cifra
                        editClosed.setText(context.getResources().getString(R.string.hourPrinted, sHour, sMinute));
                    }
                }, hour, minutes, true);
        picker.show();
    }

    /**
     *metodo per gestire lo switch in caso di place chiuso
     * @param editOpen bottone per impostare l'ora di apertura
     * @param editClosed bottone per impostare l'ora di chiusura
     */
    public void setSwitch(final Button editOpen, final Button editClosed){
        editOpen.setEnabled(false);
        editClosed.setEnabled(false);
        editOpen.setText("");
        editClosed.setText("");
    }

    /**
     * metodo per gestire lo switch in caso di place aperto
     * @param context contesto dell'activity chiamante
     * @param editOpen bottone per impostare l'ora di apertura
     * @param editClosed bottone per impostare l'ora di chiusura
     */
    public void setSwitchChecked(Context context, final Button editOpen,final Button editClosed){
        editOpen.setEnabled(true);
        editClosed.setEnabled(true);
        editOpen.setText(context.getResources().getString(R.string.from));
        editClosed.setText(context.getResources().getString(R.string.to));
    }

    /**
     * funzione per convertire DAY_OF_WEEK restituito da Calendar da formato numerico a String
     * @param value valore numerico restituito da Calendar
     * @return la stringa con il giorno corrispondente
     */
    public String getDayOfWeek(int value) {
        String day = "";
        switch (value) {
            case 1:
                day = "SUNDAY";
                break;
            case 2:
                day = "MONDAY";
                break;
            case 3:
                day = "TUESDAY";
                break;
            case 4:
                day = "WEDNESDAY";
                break;
            case 5:
                day = "THURSDAY";
                break;
            case 6:
                day = "FRIDAY";
                break;
            case 7:
                day = "SATURDAY";
                break;
        }
        return day;
    }

    /**
     * metodo per recuperare l'orario di apertura in formato Date
     * @param context
     * @param openingTime orario di apertura+chiusura
     * @return orario di apertura in formato Date
     * @throws ParseException
     */
    public Date getTimeOpening(Context context, String openingTime) throws ParseException {
        SimpleDateFormat parser = new SimpleDateFormat(context.getResources().getString(R.string.hourFormat), Locale.getDefault());
        String[] result=openingTime.split(DASH);
        return parser.parse(result[0]);
    }

    /**
     * metodo per recuperare l'orario di chiusura in formato Date
     * @param context
     * @param openingTime orario di apertura+chiusura
     * @return orario di chisura in formato Date
     * @throws ParseException
     */
    public Date getTimeClosed(Context context, String openingTime) throws ParseException {
        SimpleDateFormat parser = new SimpleDateFormat(context.getResources().getString(R.string.hourFormat), Locale.getDefault());
        String[] result=openingTime.split(DASH);
        return parser.parse(result[1]);
    }

    /**
     *  metodo per recuperare l'orario di apertura in formato String
     * @param openingTime orario di apertura+chiusura
     * @return orario di apertura in formato String
     */
    public String getOpening(String openingTime){
        String[] result=openingTime.split(DASH);
        return result[0];
    }

    /**
     *  metodo per recuperare l'orario di chiusura in formato String
     * @param openingTime orario di apertura+chiusura
     * @return orario di chisura in formato String
     */
    public String getClosed(String openingTime){
        String[]result=openingTime.split(DASH);
        return result[1];
    }

}

