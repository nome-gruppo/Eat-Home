package nomeGruppo.eathome.utility;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

/**
 * la classe implementa l'interfaccia location listener
 * e implementa un timer entro cui effettuare la ricerca della posizione
 */
public abstract class MyLocationListenerAbstract implements LocationListener {

    protected final TimerThread timerThread;

    protected MyLocationListenerAbstract(long timer) {
        this.timerThread = new TimerThread(timer);
    }

    /**
     * attiva il timer se non è già stato attivato
     */
    protected void startTimer() {
        if (!timerThread.isRunning()) {
            this.timerThread.start();
        }
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    //deprecated da api 29
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    //location trovata e timer bloccato
    protected void setLocationFound() {
        this.timerThread.stopTimer();
    }
}
