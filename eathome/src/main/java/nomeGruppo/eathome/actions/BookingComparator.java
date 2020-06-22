package nomeGruppo.eathome.actions;

import java.util.Comparator;



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
