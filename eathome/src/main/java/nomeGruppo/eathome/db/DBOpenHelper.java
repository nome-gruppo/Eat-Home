package nomeGruppo.eathome.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOpenHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "eathome.db";
    private static final Integer VERSION = 1;
    private final Context mContext;

    public static final String TABLE_NAME="myAddresses";
    public static final String _ID = "_id";
    public static final String ADDRESS="address";
    public static final String[] COLUMNS = {_ID,ADDRESS};

    private static final String CREATE_CMD = "CREATE TABLE " + TABLE_NAME + "("
            + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + ADDRESS + " VARCHAR(255) NOT NULL)";

//    public final static String[]columns_credenziali={USER_NAME,PASSWORD};
//    final static String CREATE_CREDENZIALI="CREATE TABLE "+TABLE_NAME+"("+USER_NAME+" VARCHAR(10)PRIMARY KEY,"+PASSWORD+" VARCHAR(15)NOT NULL)";

    public DBOpenHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CMD);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // N/A
    }

    void deleteDatabase() {
        mContext.deleteDatabase(DB_NAME);
    }

}
