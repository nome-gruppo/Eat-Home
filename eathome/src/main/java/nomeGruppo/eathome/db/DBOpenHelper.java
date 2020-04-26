package nomeGruppo.eathome.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOpenHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "eathome.db";
    private static final Integer VERSION = 1;
    private final Context mContext;

    public static final String TABLE_ADDRESSES ="myAddresses";
    public static final String _ID_ADDRESS = "_id";
    public static final String CITY = "city";
    public static final String ADDRESS="address";
    public static final String NUM_ADDRESS="numAddress";
    public static final String USER_ID ="userId";
    public static final String[] COLUMNS_ADDRESSES = {_ID_ADDRESS, CITY, ADDRESS, NUM_ADDRESS, USER_ID};

    public static final String TABLE_INFO ="myInfo";
    public static final String _ID_INFO = "_id";
    public static final String NAME_PLACE="namePlace";
    public static final String DATE_TIME="date";
    public static final String[] COLUMNS_INFO={_ID_INFO,NAME_PLACE,DATE_TIME};

    public static final String SELECTION_BY_USER_ID = USER_ID + "=?";

    private static final String CREATE_ADDRESSES = "CREATE TABLE " + TABLE_ADDRESSES + "("
            + _ID_ADDRESS + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + CITY + " VARCHAR(100) NOT NULL,"
            + ADDRESS + " VARCHAR(255) NOT NULL,"
            + NUM_ADDRESS + " VARCHAR(10) NOT NULL,"
            + USER_ID + " VARCHAR(255) NOT NULL)";

    private static final String CREATE_INFO="CREATE TABLE " + TABLE_INFO + "("
            + _ID_INFO + " VARCHAR(20) PRIMARY KEY, "
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
        db.execSQL(CREATE_ADDRESSES);
        db.execSQL(CREATE_INFO);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // N/A
    }

    public void deleteDatabase() {
        mContext.deleteDatabase(DB_NAME);
    }

    public void addAddress(SQLiteDatabase db, String address, String numAddress, String city, String userId){
        ContentValues values=new ContentValues();

        values.put(ADDRESS, address);
        values.put(NUM_ADDRESS, numAddress);
        values.put(CITY, city);
        values.put(USER_ID, userId);
        db.insert(TABLE_ADDRESSES,null,values);
    }

    public void addInfo(SQLiteDatabase db, String idPlace, String place, String date){
        ContentValues values=new ContentValues();

        values.put(_ID_INFO,idPlace);
        values.put(NAME_PLACE, place);
        values.put(DATE_TIME, date);
        db.insert(TABLE_INFO,null,values);
    }

    public void deleteInfo(SQLiteDatabase db,String id){
        String sql = "DELETE FROM "+ TABLE_INFO +" WHERE "+_ID_INFO +"= '"+id+"';";
        db.execSQL(sql);
    }
}
