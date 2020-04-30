package nomegruppo.eathome.utility;

import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;

import nomegruppo.eathome.clientSide.ClientBookingInfoActivity;
import nomegruppo.eathome.clientSide.ClientOrderInfoActivity;
import nomegruppo.eathome.clientSide.HomepageActivity;
import nomegruppo.eathome.OtherActivity;
import nomegruppo.eathome.placeSide.PlaceBookingInfoActivity;
import nomegruppo.eathome.placeSide.PlaceHomeActivity;
import nomegruppo.eathome.PlaceOrderInfoActivity;
import nomegruppo.eathome.R;
import nomegruppo.eathome.actors.Client;
import nomegruppo.eathome.actors.Place;
import nomegruppo.eathome.db.FirebaseConnection;

public class MenuNavigationItemSelected {

    public MenuNavigationItemSelected(){

    }

    public boolean menuNavigation(MenuItem item, Client client, Context context){
        final Intent intent;
        switch (item.getItemId()) {
            case R.id.action_home:
                intent = new Intent(context,HomepageActivity.class);
                intent.putExtra(FirebaseConnection.CLIENT, client);
                context.startActivity(intent);

                break;
            case R.id.action_orders:

                intent = new Intent(context,ClientOrderInfoActivity.class);
                intent.putExtra(FirebaseConnection.CLIENT, client);
                context.startActivity(intent);

                break;
            case R.id.action_bookings:

                intent = new Intent(context, ClientBookingInfoActivity.class);
                intent.putExtra(FirebaseConnection.CLIENT, client);
                context.startActivity(intent);


                break;
            case R.id.action_profile:

                intent = new Intent(context, OtherActivity.class);
                intent.putExtra(FirebaseConnection.CLIENT, client);
                context.startActivity(intent);


                break;
        }
        return true;
    }

    public boolean menuNavigationPlace(MenuItem item, Place place, Context context){
        final Intent intent;
        switch (item.getItemId()) {
            case R.id.action_home:
                intent = new Intent(context, PlaceHomeActivity.class);
                intent.putExtra(FirebaseConnection.PLACE, place);
                context.startActivity(intent);
                break;
            case R.id.action_orders:
                intent = new Intent(context, PlaceOrderInfoActivity.class);
                intent.putExtra(FirebaseConnection.PLACE, place);
                context.startActivity(intent);
                break;
            case R.id.action_bookings:
                intent = new Intent(context, PlaceBookingInfoActivity.class);
                intent.putExtra(FirebaseConnection.PLACE, place);
                context.startActivity(intent);
                break;
            case R.id.action_profile:
                intent = new Intent(context, OtherActivity.class);
                intent.putExtra(FirebaseConnection.PLACE, place);
                context.startActivity(intent);
                break;
        }
        return true;
    }
}
