package nomeGruppo.eathome.profile;

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

import nomeGruppo.eathome.R;

public class DialogAddAddress extends AppCompatDialogFragment {
    private EditText editCity;
    private EditText editAddress;
    private EditText editNumberAddress;
    private DialogAddAddress.DialogAddAddressListener listener;
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());

        LayoutInflater inflater=getActivity().getLayoutInflater();
        View view=inflater.inflate(R.layout.dialog_insert_address,null);

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
                listener.applyTexts(cityClient,addressClient,numberAddressClient);

            }
        });

        editAddress=(EditText)view.findViewById(R.id.editAddressClient);
        editCity=(EditText)view.findViewById(R.id.editCityClient);
        editNumberAddress=(EditText)view.findViewById(R.id.editNumberAddressClient);

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
