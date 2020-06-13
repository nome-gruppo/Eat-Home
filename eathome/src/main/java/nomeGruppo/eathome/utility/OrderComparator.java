package nomeGruppo.eathome.utility;

import java.util.Comparator;

import nomeGruppo.eathome.actions.Order;

/**
 * classe per ordinare Order per data
 */

public class OrderComparator implements Comparator<Order> {

    @Override
    public int compare(Order order1, Order order2) {
        if(order1.timeOrder>order2.timeOrder){
            return -1;
        }else if(order1.timeOrder<order2.timeOrder){
            return 1;
        }
        return 0;
    }
}
