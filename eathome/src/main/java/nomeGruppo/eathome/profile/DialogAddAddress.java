package nomegruppo.eathome.profile;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import nomegruppo.eathome.R;

public class DialogAddAddress extends AppCompatDialogFragment {

    private DialogAddAddress.DialogAddAddressListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());

        LayoutInflater inflater=getActivity().getLayoutInflater();
        View view=inflater.inflate(R.layout.dialog_insert_address,null);

        final EditText editAddress=view.findViewById(R.id.editAddressClient);
        final EditText editCity=view.findViewById(R.id.editCityClient);
        final EditText editNumberAddress=view.findViewById(R.id.editNumberAddressClient);

        builder.setView(view).setTitle("Insert address").setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        }).setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String cityClient=editCity.getText().toString();
                String addressClient=editAddress.getText().toString();
                String numberAddressClient=editNumberAddress.getText().toString();

                listener.applyTexts(addressClient,numberAddressClient, cityClient);

            }
        });

        return builder.create();

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener=(DialogAddAddress.DialogAddAddressListener)context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()+"Error");
        }
    }

    public interface DialogAddAddressListener{
        void applyTexts(String city, String address, String numberAddress);
    }

}
