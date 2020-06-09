package nomeGruppo.eathome.clientSide;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import nomeGruppo.eathome.R;
import nomeGruppo.eathome.actions.Address;
import nomeGruppo.eathome.utility.City;

/**dialog per aggiungere un nuovo indirizzo che verrà salvato nel database locale
 *
 */
public class DialogAddAddress extends AppCompatDialogFragment {

    private DialogAddAddress.DialogAddAddressListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_insert_address, (ViewGroup) requireActivity().getCurrentFocus(),false);

        final EditText editAddress = view.findViewById(R.id.editAddressClient);
        final AutoCompleteTextView editCity = view.findViewById(R.id.editCityClient);
        final EditText editNumberAddress = view.findViewById(R.id.editNumberAddressClient);
        final City city = new City();//classe che contiene l'elenco delle città

        //creo un adapter che conterrà l'elenco delle città per l'autocompletetext
        if(getContext() != null) {
            ArrayAdapter<String> adapterCity = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, city.getListCity());
            editCity.setAdapter(adapterCity);//setto l'adapter in editCity
        }

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
                    Toast.makeText(getContext(), requireActivity().getResources().getString(R.string.fill_all_fields), Toast.LENGTH_SHORT).show();
                    DialogAddAddress dialogAddAddress = new DialogAddAddress();
                    dialogAddAddress.show(requireActivity().getSupportFragmentManager(), "Dialog add address");
                } else {

                    final Address mAddress = new Address(editCity.getText().toString(),editAddress.getText().toString(),editNumberAddress.getText().toString());

                    listener.applyTexts(mAddress);
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
        void applyTexts(Address address);
    }

}
