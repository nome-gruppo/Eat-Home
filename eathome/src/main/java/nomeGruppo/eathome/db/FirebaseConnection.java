package nomeGruppo.eathome.db;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.content.ContentValues.TAG;

public class FirebaseConnection {
    private DatabaseReference mDatabase;

    public FirebaseConnection() {
        this.mDatabase = FirebaseDatabase.getInstance("https://eathome-bc890.firebaseio.com/").getReference();
    }

    public void write(String table,String column, String value) {
        mDatabase.child(table).child(column).setValue(value);
    }

    public void writeObject(String table, Object obj){



        mDatabase.child(table).push().setValue(obj);
    }

    public Object read() {
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                Object obj = dataSnapshot.getValue(Object.class);
                // ...
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        return mDatabase.addValueEventListener(postListener);
    }
}


