package nomeGruppo.eathome.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOpenHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "eathome.db";
    private static final Integer VERSION = 1;
    private final Context mContext;

    public static final String TABLE_NAME="myAddresses";
    public static final String TABLE_NAME_INFO="myInfo";
    public static final String _ID = "_id";
    public static final String CITY = "city";
    public static final String ADDRESS="address";
    public static final String NUM_ADDRESS="numAddress";
    public static final String NAME_PLACE="namePlace";
    public static final String DATE_TIME="date";
    public static final String[] COLUMNS = {_ID, CITY, ADDRESS, NUM_ADDRESS};
    public static final String[] COLUMNS_INFO={_ID,NAME_PLACE,DATE_TIME};

    private static final String CREATE_CMD = "CREATE TABLE " + TABLE_NAME + "("
            + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + CITY + " VARCHAR(100) NOT NULL,"
            + ADDRESS + " VARCHAR(255) NOT NULL,"
            + NUM_ADDRESS + " VARCHAR(10) NOT NULL)";

    private static final String CREATE_INFO="CREATE TABLE " + TABLE_NAME_INFO + "("
            + _ID + " VARCHAR(20) PRIMARY KEY, "
            + NAME_PLACE + " VARCHAR(50),"
            + DATE_TIME + " DATE)";

//    public final static String[]columns_credenziali={USER_NAME,PASSWORD};
//    final static String CREATE_CREDENZIALI="CREATE TABLE "+TABLE_NAME+"("+USER_NAME+" VARCHAR(10)PRIMARY KEY,"+PASSWORD+" VARCHAR(15)NOT NULL)";

    public DBOpenHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CMD);
        db.execSQL(CREATE_INFO);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // N/A
    }

    void deleteDatabase() {
        mContext.deleteDatabase(DB_NAME);
    }

    public void addAddress(SQLiteDatabase db, String address, String numAddress, String city){
        ContentValues values=new ContentValues();

        values.put(ADDRESS, address);
        values.put(NUM_ADDRESS, numAddress);
        values.put(CITY, city);
        db.insert(TABLE_NAME,null,values);
    }

    public void addInfo(SQLiteDatabase db, String idPlace, String place, String date){
        ContentValues values=new ContentValues();

        values.put(_ID,idPlace);
        values.put(NAME_PLACE, place);
        values.put(DATE_TIME, date);
        db.insert(TABLE_NAME_INFO,null,values);
    }

    public void deleteInfo(SQLiteDatabase db,String id){
        String sql = "DELETE FROM "+TABLE_NAME_INFO+" WHERE "+_ID +"= '"+id+"';";
        db.execSQL(sql);
    }
}
