package nomegruppo.eathome.actors;

import java.util.Comparator;

public class PlacesByName implements Comparator<Place> {
    @Override
    public int compare(Place place1, Place place2) {
        return place1.namePlace.compareTo(place2.namePlace);
    }
}
