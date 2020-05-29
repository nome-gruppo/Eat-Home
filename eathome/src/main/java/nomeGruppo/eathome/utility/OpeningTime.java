package nomeGruppo.eathome.utility;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.res.Resources;
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
    private Resources res;

    public OpeningTime(){

    }

    public void setOpeningTime(Context context, final Button edit){
        final Calendar cldr = Calendar.getInstance();
        int hour = cldr.get(Calendar.HOUR_OF_DAY);
        int minutes = cldr.get(Calendar.MINUTE);
        // time picker dialog
        picker = new TimePickerDialog(context,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker tp, int sHour, int sMinute) {
                        //String.format() aggiunge uno zero davanti al numero qualora questo avesse un unica cifra
                        edit.setText(res.getString(R.string.hourPrinted, sHour, sMinute));
                    }
                }, hour, minutes, true);
        picker.show();
        res = context.getResources();
    }

    public void setOpeningTimeClose(Context context, final Button editOpen,final Button editClosed){
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
                        editClosed.setText(res.getString(R.string.hourPrinted, sHour, sMinute));
                    }
                }, hour, minutes, true);
        picker.show();
    }

    public void setSwitch(final Button editOpen, final Button editClosed){
        editOpen.setEnabled(false);
        editClosed.setEnabled(false);
        editOpen.setText("");
        editClosed.setText("");
    }

    public void setSwitchChecked(final Button editOpen,final Button editClosed){
        editOpen.setEnabled(true);
        editClosed.setEnabled(true);
        editOpen.setText(res.getString(R.string.from));
        editClosed.setText(res.getString(R.string.to));
    }

    public String getDayOfWeek(int value) {//funzione per convertire DAY_OF_WEEK restituito da Calendar da formato numerico a String
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

    public Date getTimeOpening(String openingTime) throws ParseException {
        SimpleDateFormat parser = new SimpleDateFormat(res.getString(R.string.hourFormat), Locale.getDefault());
        String[] result=openingTime.split(DASH);
        return parser.parse(result[0]);
    }

    public Date getTimeClosed(String openingTime) throws ParseException {
        SimpleDateFormat parser = new SimpleDateFormat(res.getString(R.string.hourFormat), Locale.getDefault());
        String[] result=openingTime.split(DASH);
        return parser.parse(result[1]);
    }

    public String getOpening(String openingTime){
        String[] result=openingTime.split(DASH);
        return result[0];
    }
    public String getClosed(String openingTime){
        String[]result=openingTime.split(DASH);
        return result[1];
    }

}

