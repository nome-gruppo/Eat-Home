package nomeGruppo.eathome.clientSide;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import nomeGruppo.eathome.R;
import nomeGruppo.eathome.utility.City;

public class DialogAddAddress extends AppCompatDialogFragment {

    private DialogAddAddress.DialogAddAddressListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_insert_address, null);

        final EditText editAddress = view.findViewById(R.id.editAddressClient);
        final AutoCompleteTextView editCity = view.findViewById(R.id.editCityClient);
        final EditText editNumberAddress = view.findViewById(R.id.editNumberAddressClient);
        final City city = new City();//classe che contiene l'elenco delle città
        //creo un adapter che conterrà l'elenco delle città per l'autocompletetext
        ArrayAdapter<String> adapterCity = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, city.getListCity());
        editCity.setAdapter(adapterCity);//setto l'adapter in editCity

        builder.setView(view).setTitle(getActivity().getResources().getString(R.string.choose_address))
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).setPositiveButton(getActivity().getResources().getString(R.string.add), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (editCity.getText().toString().trim().length() == 0 || editAddress.getText().toString().trim().length() == 0 ||
                        editNumberAddress.getText().toString().trim().length() == 0) {
                    Toast.makeText(getContext(), getActivity().getResources().getString(R.string.fill_all_fields), Toast.LENGTH_SHORT).show();
                    DialogAddAddress dialogAddAddress = new DialogAddAddress();
                    dialogAddAddress.show(getActivity().getSupportFragmentManager(), "Dialog add address");
                } else {
                    String cityClient = editCity.getText().toString();
                    String addressClient = editAddress.getText().toString();
                    String numberAddressClient = editNumberAddress.getText().toString();

                    listener.applyTexts(cityClient, addressClient, numberAddressClient);
                }
            }
        });

        return builder.create();

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (DialogAddAddress.DialogAddAddressListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "Error");
        }
    }

    public interface DialogAddAddressListener {
        void applyTexts(String city, String address, String numberAddress);
    }

}
