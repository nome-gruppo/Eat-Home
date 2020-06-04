package nomeGruppo.eathome.utility;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import nomeGruppo.eathome.R;
import nomeGruppo.eathome.actions.Order;

public class DialogListFoodOrder extends AppCompatDialogFragment {
    private final Order order;

    public DialogListFoodOrder(Order order){
        this.order=order;
    }

    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        StringBuilder message= new StringBuilder();

        if(getContext() != null) {
            builder.setTitle(getContext().getResources().getString(R.string.order_summary)).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            }).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
        }

        for(String value:order.foodsOrder){
            message.append(value).append("\n");
        }
        if(order.note!=null){
            message.append("\nNote: ").append(order.note);
        }
        builder.setMessage(message.toString());
        return builder.create();
    }

}
