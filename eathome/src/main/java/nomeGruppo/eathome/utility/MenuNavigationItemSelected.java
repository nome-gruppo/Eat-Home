package nomeGruppo.eathome.utility;

import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;

import nomeGruppo.eathome.ClientBookingInfoActivity;
import nomeGruppo.eathome.ClientOrderInfoActivity;
import nomeGruppo.eathome.HomepageActivity;
import nomeGruppo.eathome.LoginActivity;
import nomeGruppo.eathome.R;
import nomeGruppo.eathome.actors.Client;
import nomeGruppo.eathome.db.FirebaseConnection;
import nomeGruppo.eathome.profile.ClientProfileActivity;

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

                    intent = new Intent(context, ClientProfileActivity.class);
                    intent.putExtra(FirebaseConnection.CLIENT, client);
                    context.startActivity(intent);


                break;
        }
        return true;
    }
}
