package nomeGruppo.eathome.utility;

import android.app.TimePickerDialog;
import android.content.Context;
import android.widget.EditText;
import android.widget.TimePicker;
import java.util.Calendar;


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

}

