package nomeGruppo.eathome.db;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import nomeGruppo.eathome.MainActivity;
import nomeGruppo.eathome.PlaceHomepageActivity;
import nomeGruppo.eathome.actors.Client;
import nomeGruppo.eathome.actors.Place;

import static android.content.ContentValues.TAG;

public class FirebaseConnection {

    private static final String TAG = "FirebaseConnection";

    public static final String PLACE_TABLE = "Places";
    public static final String CLIENT_TABLE = "Clients";

    //stringhe utilizzate negli intent
    public static final String LOGGED_FLAG = "Logged";
    public static final String PLACE = "Place";
    public static final String CLIENT = "Client";

    private DatabaseReference mDatabase;
    private Object objectFounded;

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

    public boolean searchUser(String table, String userId){

        mDatabase.child(table).child(userId).addListenerForSingleValueEvent(searchEventListener());

        if(this.objectFounded == null){
            return false;
        }else{
            return true;
        }
    }

    public Object getObjectFounded(){
        return this.objectFounded;
    }

    public String getKey(String table){
        return mDatabase.child(table).push().getKey();
    }

    private ValueEventListener searchEventListener() {
        ValueEventListener valueEventListener = new ValueEventListener() {
            List<Object> objList = null;
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if(dataSnapshot.exists()){
                objectFounded = dataSnapshot.getValue();
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

    /*metodo per ricerca utente nel database Firebase nei nodi Clients e Places
    *
    *NB: per funzionare correttamente il parametro table passato deve essere Firebase.CLIENT_TABLE
     */
    public void searchUserInDb(final String userId, final String table, final ProgressBar progressBar, final Activity activity) throws Resources.NotFoundException {

        mDatabase.child(table).child(userId).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                    progressBar.setVisibility(View.INVISIBLE);

                    if(table.equals(FirebaseConnection.CLIENT_TABLE)){
                        Client client = dataSnapshot.getValue(Client.class);
                        Intent intent = new Intent(activity, MainActivity.class);
                        intent.putExtra(CLIENT, client);
                        intent.putExtra(LOGGED_FLAG, true);
                        activity.startActivity(intent);
                        activity.finish();
                    }else{
                        Place place = dataSnapshot.getValue(Place.class);
                        Intent intent = new Intent(activity, PlaceHomepageActivity.class);
                        intent.putExtra(PLACE, place);
                        intent.putExtra(LOGGED_FLAG, true);
                        activity.startActivity(intent);
                        activity.finish();
                    }
                }else if(!dataSnapshot.exists() && table.equals(FirebaseConnection.CLIENT_TABLE)){
                    searchUserInDb(userId, PLACE_TABLE, progressBar, activity);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "searchUserInDb:onCancelled", databaseError.toException());
                throw new Resources.NotFoundException();
            }
        });
    }
}


