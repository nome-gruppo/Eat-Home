package nomeGruppo.eathome.actors;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class PlacesByDistance implements Comparator<Place> {

    private static final String SEPARATOR = ", ";

    private Location userLocation;
    private Context context;


    public PlacesByDistance(double latitude, double longitude, Context context) {
        this.userLocation = new Location("");
        this.userLocation.setLatitude(latitude);
        this.userLocation.setLongitude(longitude);
        this.context = context;
    }

    @Override
    public int compare(Place place1, Place place2) {

        int result = 0;
        final String stringAddress1 = new nomeGruppo.eathome.actions.Address(place1.cityPlace, place1.addressPlace, place1.addressNumPlace).getFullAddress();
        final String stringAddress2 = new nomeGruppo.eathome.actions.Address(place2.cityPlace, place2.addressPlace, place2.addressNumPlace).getFullAddress();

        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        Location location1 = new Location("");
        Location location2 = new Location("");

        try{

            List<Address> list1 = geocoder.getFromLocationName(stringAddress1, 1);
            Address address1 = list1.get(0);
            location1.setLatitude(address1.getLatitude());
            location1.setLongitude(address1.getLongitude());


            List<Address> list2 = geocoder.getFromLocationName(stringAddress2, 1);
            Address address2 = list2.get(0);
            location2.setLatitude(address2.getLatitude());
            location2.setLongitude(address2.getLongitude());


        } catch (IOException e) {
            e.printStackTrace();
        }

        //se i valori sono stati correttamente impostati
        if (location1.getLatitude() != 0 && location1.getLongitude() != 0 && location2.getLatitude() != 0 && location2.getLongitude() != 0) {

            if (userLocation.distanceTo(location1) < userLocation.distanceTo(location2)) {
                result = -1;
            } else {
                result = 1;
            }

        }
        return result;
    }
}
