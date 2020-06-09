package nomeGruppo.eathome;

/**La classe MyException contiene le eccezioni personalizzate sollevate a runtime
 *
 * La classe è stata creata in modo da poter essere utilizzata per più eccezioni grazie al campo exceptionType
 */
public class MyExceptions extends RuntimeException {

    public static final int FIREBASE_NOT_FOUND = 1;     //se non è stato trovato alcun oggetto nel database
    private int exceptionType;

    public MyExceptions(int exceptionType, String errorMessage){
        super(errorMessage);
        this.exceptionType = exceptionType;
    }

    public int getExceptionType(){
        return exceptionType;
    }

}
