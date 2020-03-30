package nomeGruppo.eathome.profile;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import nomeGruppo.eathome.R;
import nomeGruppo.eathome.actors.Place;
import nomeGruppo.eathome.foods.Food;


public class AddressAdapter extends ArrayAdapter<String> {

    private EditText addressET;
    private ImageButton editBtn;
    private ImageButton deleteBtn;


    public AddressAdapter(@NonNull Context context, int resource, List<String> list) {
        super(context, resource, list);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.fragment_address, null);

        }

        addressET = convertView.findViewById(R.id.fragment_address_et);
        editBtn = convertView.findViewById(R.id.fragment_address_btn_edit);
        deleteBtn = convertView.findViewById(R.id.fragment_address_btn_delete);

        String address = getItem(position);
        addressET.setText(address);

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               openDialog(addressET.getText().toString());
            }
        });


        return convertView;
    }

    private void openDialog(String address){ //creo un alert dialogo
        AlertDialog.Builder builder=new AlertDialog.Builder(getContext());

        Activity activity = (Activity) getContext();
        LayoutInflater inflater=activity.getLayoutInflater();
        View view=inflater.inflate(R.layout.dialog_insert_address,null);

        builder.setView(view).setTitle(getContext().getResources().getString(R.string.placeAdress)).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //se clicca su no non succede nulla e l'alert di chiude
            }
        });

        EditText editAddress=(EditText)view.findViewById(R.id.editAddressClient);
        EditText editCity=(EditText)view.findViewById(R.id.editCityClient);
        EditText editNumberAddress=(EditText)view.findViewById(R.id.editNumberAddressClient);

        String[] split=address.split(" - ");

        editCity.setText(split[0]);
        editAddress.setText(split[1]);
        editNumberAddress.setText(split[2]);


        AlertDialog alert = builder.create();
        alert.show();
    }
}
