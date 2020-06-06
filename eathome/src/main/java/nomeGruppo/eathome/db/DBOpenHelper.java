package nomeGruppo.eathome.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import nomeGruppo.eathome.actions.Address;

public class DBOpenHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "eathome.db";
    private static final Integer VERSION = 1;
    private final Context mContext;

    public static final String TABLE_ADDRESSES ="myAddresses";
    public static final String ID_ADDRESS = "_id";
    public static final String CITY = "city";
    public static final String ADDRESS="address";
    public static final String NUM_ADDRESS="numAddress";
    public static final String USER_ID_ADDRESS ="userId";
    public static final String[] COLUMNS_ADDRESSES = {ID_ADDRESS, CITY, ADDRESS, NUM_ADDRESS, USER_ID_ADDRESS};

    public static final String SELECTION_BY_USER_ID_ADDRESS = USER_ID_ADDRESS + "= ?";

    public static final String TABLE_INFO ="myInfo";
    public static final String ID_INFO = "_id";
    public static final String NAME_PLACE="namePlace";
    public static final String DATE_TIME="date";
    public static final String USER_ID_INFO="userId";
    public static final String[] COLUMNS_INFO={ID_INFO,NAME_PLACE,DATE_TIME,USER_ID_INFO};

    public static final String SELECTION_BY_USER_ID_INFO = USER_ID_INFO + "= ?";

    private static final String CREATE_ADDRESSES = "CREATE TABLE " + TABLE_ADDRESSES + "("
            + ID_ADDRESS + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + CITY + " VARCHAR(100) NOT NULL,"
            + ADDRESS + " VARCHAR(255) NOT NULL,"
            + NUM_ADDRESS + " VARCHAR(10) NOT NULL,"
            + USER_ID_ADDRESS + " VARCHAR(255) NOT NULL)";

    private static final String CREATE_INFO="CREATE TABLE " + TABLE_INFO + "("
            + ID_INFO + " VARCHAR(20) PRIMARY KEY, "
            + NAME_PLACE + " VARCHAR(50),"
            + DATE_TIME + " DATE,"
            + USER_ID_INFO + " VARCHAR(255) NOT NULL)";

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

    public void addAddress(SQLiteDatabase db, Address address, String userId){
        ContentValues values=new ContentValues();

        values.put(CITY, address.getCity());
        values.put(ADDRESS, address.getStreet());
        values.put(NUM_ADDRESS, address.getNumberAddress());
        values.put(USER_ID_ADDRESS, userId);
        db.insert(TABLE_ADDRESSES,null,values);
    }

    public int getLastIdAddresses(SQLiteDatabase db){
        try (Cursor c = db.query(TABLE_ADDRESSES, null, null, null, null, null, null)) {
            c.moveToLast();
            return c.getInt(c.getColumnIndexOrThrow(ID_ADDRESS));
        }
    }

    public void updateAdd(SQLiteDatabase db, Address address,String idClient){
        String sql="UPDATE "+TABLE_ADDRESSES+" SET "+ADDRESS+ "= '"+address.getStreet()+"', "+NUM_ADDRESS+ "= '"+address.getNumberAddress()+"', "+CITY+"= '"+address.getCity()+"' "+
                " WHERE "+ ID_ADDRESS +"= '"+address.getIdAddress()+"' AND "+USER_ID_ADDRESS +"= '"+idClient+"';";
        db.execSQL(sql);
    }

    public void deleteAdd(SQLiteDatabase db,int idAddress,String idClient){
        String sql = "DELETE FROM "+ TABLE_ADDRESSES +" WHERE "+ ID_ADDRESS +"= '"+idAddress+"' AND "+USER_ID_ADDRESS +"= '"+idClient+"';";
        db.execSQL(sql);
    }

    public void addInfo(SQLiteDatabase db, String idPlace, String place, String date, String userId){
        ContentValues values=new ContentValues();

        values.put(ID_INFO,idPlace);
        values.put(NAME_PLACE, place);
        values.put(DATE_TIME, date);
        values.put(USER_ID_INFO, userId);
        db.insert(TABLE_INFO,null,values);
    }

    public void deleteInfo(SQLiteDatabase db,String id){
        String sql = "DELETE FROM "+ TABLE_INFO +" WHERE "+ ID_INFO +"= '"+id+"' ;";
        db.execSQL(sql);
    }
}
