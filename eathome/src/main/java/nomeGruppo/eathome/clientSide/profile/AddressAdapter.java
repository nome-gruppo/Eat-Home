package nomeGruppo.eathome.clientSide.profile;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import nomeGruppo.eathome.R;
import nomeGruppo.eathome.actions.Address;
import nomeGruppo.eathome.db.DBOpenHelper;


public class AddressAdapter extends ArrayAdapter<Address> {

    private DBOpenHelper helper;
    private SQLiteDatabase mDB;

    private final String idClient;

    private final ArrayList<Address> list;
    private final MyAddressesActivity callingActivity;


    AddressAdapter(@NonNull Context context, ArrayList<Address> list, String idClient, MyAddressesActivity callingActivity) {
        super(context, R.layout.listitem_my_address, list);
        this.idClient = idClient;
        this.list = list;
        this.callingActivity = callingActivity;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        Holder holder;

        if (inflater != null) {
            convertView = inflater.inflate(R.layout.listitem_my_address, parent, false);

            holder = new Holder();

            holder.addressET = convertView.findViewById(R.id.fragment_address_et);
            holder.editBtn = convertView.findViewById(R.id.fragment_address_btn_edit);
            holder.deleteBtn = convertView.findViewById(R.id.fragment_address_btn_delete);

            convertView.setTag(holder);

        } else {
            holder = (Holder) convertView.getTag();
        }

        final Address addressObj = getItem(position);

        if (addressObj != null) {
            final String address = addressObj.getFullAddress();
            holder.addressET.setText(address);

            holder.editBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    openDialog(addressObj);
                }
            });

            helper = new DBOpenHelper(getContext());
            mDB = helper.getWritableDatabase();
            holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    list.remove(addressObj);
                    notifyDataSetChanged();
                    helper.deleteAddress(mDB, addressObj.getIdAddress(), idClient);

                    if (list.isEmpty()) {
                        callingActivity.getNoAddressTW().setVisibility(View.VISIBLE);
                    }
                }
            });
        }

        return convertView;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Nullable
    @Override
    public Address getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return list.indexOf(getItem(position));
    }


    private void openDialog(final Address addressObj) { //creo un alert dialogo
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        Activity activity = (Activity) getContext();
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_insert_address, (ViewGroup) ((Activity) getContext()).getCurrentFocus(),false);

        final EditText editAddress = view.findViewById(R.id.editAddressClient);
        final EditText editCity = view.findViewById(R.id.editCityClient);
        final EditText editNumberAddress = view.findViewById(R.id.editNumberAddressClient);

        editCity.setText(addressObj.getCity());
        editAddress.setText(addressObj.getStreet());
        editNumberAddress.setText(addressObj.getNumberAddress());

        builder.setView(view).setTitle(getContext().getResources().getString(R.string.enterAddress)).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Address newAddress = new Address(addressObj.getIdAddress(), editAddress.getText().toString(),
                        editNumberAddress.getText().toString(), editCity.getText().toString());
                list.remove(addressObj);
                list.add(newAddress);
                helper.updateAddress(mDB, newAddress, idClient);
                notifyDataSetChanged();
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //se clicca su no non succede nulla e l'alert di chiude
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private static class Holder {
        EditText addressET;
        ImageButton editBtn;
        ImageButton deleteBtn;
    }
}
