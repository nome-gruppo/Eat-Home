package nomeGruppo.eathome.actors;

import java.util.Comparator;

public class PlacesByValuation implements Comparator<Place> {
    @Override
    public int compare(Place place1, Place place2) {
        int result = 0;

        if(place1.valuation < place2.valuation){
            result = -1;
        }else if(place1.valuation > place2.valuation){
            result = 1;
        }

        return result;
    }
}
