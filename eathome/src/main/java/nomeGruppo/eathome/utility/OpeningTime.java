package nomeGruppo.eathome.utility;

import android.app.TimePickerDialog;
import android.content.Context;
import android.widget.EditText;
import android.widget.TimePicker;

import java.sql.Time;
import java.util.Calendar;
import java.util.logging.LoggingPermission;


public class OpeningTime {
    private TimePickerDialog picker;
    private static final String SPLIT=":";
    private static final int LENGTH=5;

    public OpeningTime(){

    }

    public void setOpeningTime(Context context, final EditText edit){
        final Calendar cldr = Calendar.getInstance();
        int hour = cldr.get(Calendar.HOUR_OF_DAY);
        int minutes = cldr.get(Calendar.MINUTE);
        // time picker dialog
        picker = new TimePickerDialog(context,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker tp, int sHour, int sMinute) {
                        edit.setText(sHour + SPLIT + sMinute);

                    }
                }, hour, minutes, true);
        picker.show();
    }

    public void setOpeningTimeClose(Context context, final EditText editOpen,final EditText editClosed){
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
                        editClosed.setText(sHour + SPLIT + sMinute);
                    }
                }, hour, minutes, true);
        picker.show();
    }

    public void setSwitch(final EditText editOpen,final EditText editClosed){
        editOpen.setEnabled(false);
        editClosed.setEnabled(false);
        editOpen.setText("");
        editClosed.setText("");
    }

    public String getDayOfWeek(int value) {//funzione per convertire DAY_OF_WEEK restituito da Calendar da formato numerico a String
        String day = "";
        switch (value) {
            case 1:
                day = "MONDAY";
                break;
            case 2:
                day = "TUESDAY";
                break;
            case 3:
                day = "WEDNESDAY";
                break;
            case 4:
                day = "THURSDAY";
                break;
            case 5:
                day = "FRIDAY";
                break;
            case 6:
                day = "SATURDAY";
                break;
            case 7:
                day = "SUNDAY";
                break;
        }
        return day;
    }

    public Time getTimeOpening(String openingTime){
        return Time.valueOf(openingTime.substring(0,5)+":"+00);
    }

    public Time getTimeClosed(String openingTime){
        return Time.valueOf(openingTime.substring(6)+":"+00);
    }

    public String getOpening(String openingTime){
        return openingTime.substring(0,5);
    }
    public String getClosed(String openingTime){
        return openingTime.substring(6);
    }

}

