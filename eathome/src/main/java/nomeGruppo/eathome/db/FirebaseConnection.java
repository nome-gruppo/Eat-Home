package nomeGruppo.eathome.db;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import nomeGruppo.eathome.HomepageActivity;
import nomeGruppo.eathome.PlaceHomeActivity;
import nomeGruppo.eathome.actors.Client;
import nomeGruppo.eathome.actors.Place;

public class FirebaseConnection {

    private static final String TAG = "FirebaseConnection";

    public static final String PLACE_TABLE = "Places"; //todo rinomina tutti i table in node
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

    /**metodo per ricerca utente nel database Firebase nei nodi Clients e Places
     * NB: per funzionare correttamente il parametro node passato deve essere Firebase.CLIENT_TABLE
     *
     * @param userId codice id dell'utente
     * @param node nodo FirebaseConnection.CLIENT_NODE
     * @param progressBar progressbar presente nell'activity chiamante. null se non presente
     * @param activity activity chiamante
     * @throws Resources.NotFoundException
     */
    public void searchUserInDb(final String userId, final String node, final ProgressBar progressBar, final Activity activity) {

        mDatabase.child(node).child(userId).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                    if(progressBar != null) {
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                    //ricerca nel nodo clienti
                    if(node.equals(FirebaseConnection.CLIENT_TABLE)){
                        Client client = dataSnapshot.getValue(Client.class);
                        Intent intent = new Intent(activity, HomepageActivity.class);
                        intent.putExtra(CLIENT, client);
                        intent.putExtra(LOGGED_FLAG, true);
                        activity.startActivity(intent);
                        activity.finish();

                    }else{ //ricerca nel nodo places
                        Place place = dataSnapshot.getValue(Place.class);
                        Intent intent = new Intent(activity, PlaceHomeActivity.class);
                        intent.putExtra(PLACE, place);
                        intent.putExtra(LOGGED_FLAG, true);
                        activity.startActivity(intent);
                        activity.finish();
                    }
                    //se non è stato trovato l'id nel nodo clienti cerca in places
                }else if(!dataSnapshot.exists() && node.equals(FirebaseConnection.CLIENT_TABLE)){
                    searchUserInDb(userId, PLACE_TABLE, progressBar, activity);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "searchUserInDb:onCancelled", databaseError.toException());
            }
        });
    }
}


