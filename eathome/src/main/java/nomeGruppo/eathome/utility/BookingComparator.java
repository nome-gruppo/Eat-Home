package nomeGruppo.eathome.utility;

import java.util.Comparator;

import nomeGruppo.eathome.actions.Booking;


/**
 * classe per ordinare i Booking per data
 */

public class BookingComparator implements Comparator<Booking> {
    @Override
    public int compare(Booking booking1, Booking booking2) {
        if (booking1.dateBooking > booking2.dateBooking) {
            return -1;
        } else if (booking1.dateBooking < booking2.dateBooking) {
            return 1;
        }
        return 0;
    }
}
