package nomeGruppo.eathome.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbInternalConnection extends SQLiteOpenHelper {

    final private static String DBNAME = "eathome.db";
    final private static Integer VERSION = 1;
    final private Context mContext;

    final static String TABLE_NAME="login";
    final static String USER_NAME="username";
    final static String PASSWORD="password";

    public final static String[]columns_credenziali={USER_NAME,PASSWORD};

    final static String CREATE_CREDENZIALI="CREATE TABLE "+TABLE_NAME+"("+USER_NAME+" VARCHAR(10)PRIMARY KEY,"+PASSWORD+" VARCHAR(15)NOT NULL)";

    public DbInternalConnection(Context context) {
        super(context, DBNAME, null, VERSION);
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CREDENZIALI);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // N/A
    }

    void deleteDatabase() {
        mContext.deleteDatabase(DBNAME);
    }

}
