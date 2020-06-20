package nomeGruppo.eathome.utility;

import androidx.annotation.Nullable;

/**
 * La classe serve per creare un timer di durata pari al parametro timer
 * al termine del quale si solleva un'eccezione
 */
public class TimerThread extends Thread {

    private static final String NAME = TimerThread.class.getName();
    private final long timer;       //durata del parametro
    private boolean running;        //flag per non sollevare l'eccezione al termine del timer
    private final MyExceptions myException;

    public TimerThread(long timer) {
        super(NAME);
        this.timer = timer;
        this.running = true;
        this.myException = new MyExceptions(MyExceptions.TIMEOUT, MyExceptions.TIMEOUT_MESSAGE, timer);
    }

    @Override
    public void setUncaughtExceptionHandler(@Nullable UncaughtExceptionHandler eh) {
        super.setUncaughtExceptionHandler(eh);
    }

    public void stopTimer() {
        this.running = false;
    }

    @Override
    public void run() throws MyExceptions {
        super.run();
        this.running = true;
        //non solleva l'eccezione se il flag è stato impostato
        try {

            Thread.sleep(timer);
            if (this.running) {
                throw myException;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
