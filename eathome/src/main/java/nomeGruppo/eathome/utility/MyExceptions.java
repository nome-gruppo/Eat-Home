package nomeGruppo.eathome.utility;

import java.util.Locale;

/**
 * La classe MyException contiene le eccezioni personalizzate sollevate a runtime
 * <p>
 * La classe è stata creata in modo da poter essere utilizzata per più eccezioni grazie al campo exceptionType
 */
public class MyExceptions extends RuntimeException {

    public static final int TIMEOUT = 1;

    public static final String TIMEOUT_MESSAGE = "Timeout";
    private final int exceptionType;

    public MyExceptions(int exceptionType, String errorMessage) {
        super(errorMessage);
        this.exceptionType = exceptionType;
    }

    public MyExceptions(int exceptionType, String errorMessage, long timeout) {
        this(exceptionType, errorMessage + String.format(Locale.getDefault(), " %d msec", timeout));
    }

    public int getExceptionType() {
        return exceptionType;
    }

}
