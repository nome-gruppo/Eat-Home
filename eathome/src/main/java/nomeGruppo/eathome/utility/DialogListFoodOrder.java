package nomegruppo.eathome.utility;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import nomegruppo.eathome.R;
import nomegruppo.eathome.actions.Order;

public class DialogListFoodOrder extends AppCompatDialogFragment {
    private Order order;

    public DialogListFoodOrder(Order order){
        this.order=order;
    }

    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        builder.setTitle(getContext().getResources().getString(R.string.order_summary)).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        }).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        String message="";
        for(String value:order.foodsOrder){
            message+=value+"\n";
        }
        builder.setMessage(message);
        return builder.create();
    }
}
