package nomeGruppo.eathome.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.appcompat.app.AppCompatActivity;

public class InternalDbUtility extends AppCompatActivity {

    final static String TABLE_NAME="login";
    final static String USER_NAME="username";
    final static String PASSWORD="password";

    private SQLiteDatabase mDatabase;
    private DbInternalConnection db;
    private Cursor cursor;

    public InternalDbUtility() {
        mDatabase=null;
        db=null;
        cursor=null;
    }

    public Cursor readLogin(){
        this.cursor.moveToFirst();
        String[]columns={USER_NAME,PASSWORD};
        cursor=mDatabase.query(TABLE_NAME,columns,null,null,null,null,null);
        return cursor;
    }

    public void writeLogin(String username,String password) {
        db = new DbInternalConnection(this);
        mDatabase = db.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(USER_NAME, username);
        values.put(PASSWORD, password);
        mDatabase.insert(TABLE_NAME, null, values);
        values.clear();
    }


}
