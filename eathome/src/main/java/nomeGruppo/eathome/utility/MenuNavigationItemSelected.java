package nomeGruppo.eathome.utility;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;

import nomeGruppo.eathome.clientSide.ClientBookingInfoActivity;
import nomeGruppo.eathome.clientSide.ClientOrderInfoActivity;
import nomeGruppo.eathome.clientSide.HomepageActivity;
import nomeGruppo.eathome.OtherActivity;
import nomeGruppo.eathome.placeSide.PlaceBookingInfoActivity;
import nomeGruppo.eathome.placeSide.PlaceHomeActivity;
import nomeGruppo.eathome.placeSide.PlaceOrderInfoActivity;
import nomeGruppo.eathome.R;
import nomeGruppo.eathome.actors.Client;
import nomeGruppo.eathome.actors.Place;
import nomeGruppo.eathome.db.FirebaseConnection;

public class MenuNavigationItemSelected {

    public MenuNavigationItemSelected(){

    }

    public boolean menuNavigation(MenuItem item, Client client, Context context){
        final Intent intent;
        Activity activity=(Activity)context;
        switch (item.getItemId()) {
            case R.id.action_home:
                intent = new Intent(context,HomepageActivity.class);
                intent.putExtra(FirebaseConnection.CLIENT, client);
                context.startActivity(intent);
                activity.finish();

                break;
            case R.id.action_orders:

                intent = new Intent(context,ClientOrderInfoActivity.class);
                intent.putExtra(FirebaseConnection.CLIENT, client);
                context.startActivity(intent);
                activity.finish();

                break;
            case R.id.action_bookings:

                intent = new Intent(context, ClientBookingInfoActivity.class);
                intent.putExtra(FirebaseConnection.CLIENT, client);
                context.startActivity(intent);
                activity.finish();

                break;
            case R.id.action_profile:

                intent = new Intent(context, OtherActivity.class);
                intent.putExtra(FirebaseConnection.CLIENT, client);
                context.startActivity(intent);
                activity.finish();

                break;
        }
        return true;
    }

    public boolean menuNavigationPlace(MenuItem item, Place place, Context context){
        final Intent intent;
        Activity activity=(Activity)context;
        switch (item.getItemId()) {
            case R.id.action_home:
                intent = new Intent(context, PlaceHomeActivity.class);
                intent.putExtra(FirebaseConnection.PLACE, place);
                context.startActivity(intent);
                activity.finish();
                break;
            case R.id.action_orders:
                intent = new Intent(context, PlaceOrderInfoActivity.class);
                intent.putExtra(FirebaseConnection.PLACE, place);
                context.startActivity(intent);
                activity.finish();
                break;
            case R.id.action_bookings:
                intent = new Intent(context, PlaceBookingInfoActivity.class);
                intent.putExtra(FirebaseConnection.PLACE, place);
                context.startActivity(intent);
                activity.finish();
                break;
            case R.id.action_profile:
                intent = new Intent(context, OtherActivity.class);
                intent.putExtra(FirebaseConnection.PLACE, place);
                context.startActivity(intent);
                activity.finish();
                break;
        }
        return true;
    }
}
