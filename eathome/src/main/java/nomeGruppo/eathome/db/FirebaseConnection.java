package nomeGruppo.eathome.db;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import static android.content.ContentValues.TAG;

public class FirebaseConnection {
    private DatabaseReference mDatabase;

    public FirebaseConnection() {
        this.mDatabase = FirebaseDatabase.getInstance("https://eathome-bc890.firebaseio.com/").getReference();
    }

    public void write(String table, String column, String value) {
        mDatabase.child(table).child(column).setValue(value);
    }

    public void write(String table, String column, Object value){
        mDatabase.child(table).child(column).setValue(value);
    }

    public void writeObject(String table, Object obj) {
        mDatabase.child(table).push().setValue(obj);
    }

    public String getKey(String table){
        return mDatabase.child(table).push().getKey();
    }

    public ValueEventListener valueEventListener(final Object obj) {
        ValueEventListener valueEventListener = new ValueEventListener() {
            List<Object> objList = null;
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                Object object = snapshot.getValue(obj.getClass());
                objList.add(object);
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            // Getting Post failed, log a message
            Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            // ...
        }
    };
    return valueEventListener;
}

    public DatabaseReference getmDatabase() {
        return mDatabase;
    }

    public Query queryEqualTo(String table,String column,String value){
         return mDatabase.child(table).orderByChild(column).equalTo(value);
    }

}


