package nomeGruppo.eathome.profile;

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

import java.util.ArrayList;
import java.util.List;

import nomeGruppo.eathome.R;
import nomeGruppo.eathome.db.DBOpenHelper;


public class AddressAdapter extends ArrayAdapter<String> {

    private EditText addressET;
    private ImageButton editBtn;
    private ImageButton deleteBtn;

    private DBOpenHelper helper;
    private SQLiteDatabase mDB;

    private ArrayList<String> list;

    public AddressAdapter(@NonNull Context context, int resource, ArrayList<String> list) {
        super(context, resource, list);
        this.list = list;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listitem_my_address, null);

            addressET = convertView.findViewById(R.id.fragment_address_et);
            editBtn = convertView.findViewById(R.id.fragment_address_btn_edit);
            deleteBtn = convertView.findViewById(R.id.fragment_address_btn_delete);
        }

        helper = new DBOpenHelper(getContext());
        mDB = helper.getWritableDatabase();

        addressET.setText(getItem(position));

        editBtn.setTag(position);
        deleteBtn.setTag(position);

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               openDialog(getItem(position), position);


            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeItem((int) view.getTag());
                helper.deleteAddress(mDB,position);
                notifyDataSetChanged();
            }
        });

        return convertView;
    }

    private void removeItem(int position){
        ArrayList<String> mList = new ArrayList<>(list);
        mList.remove(position);
        list.clear();
        list.addAll(mList);
        notifyDataSetChanged();
    }

    private void openDialog(String address, final int position){ //creo un alert dialogo
        AlertDialog.Builder builder=new AlertDialog.Builder(getContext());

        Activity activity = (Activity) getContext();
        LayoutInflater inflater=activity.getLayoutInflater();
        View view=inflater.inflate(R.layout.dialog_insert_address,null);

        final EditText editAddress=view.findViewById(R.id.editAddressClient);
        final EditText editCity=view.findViewById(R.id.editCityClient);
        final EditText editNumberAddress=view.findViewById(R.id.editNumberAddressClient);

        final String[] split=address.split(", ");

        builder.setView(view).setTitle(getContext().getResources().getString(R.string.placeAdress)).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                list.set(position, getItem(position));
                helper.updateAddress(mDB, position, split[0], split[1], split[2]);
                addressET.setText(split[0] + ", " + split[1] + ", " + split[2]);
                notifyDataSetChanged();
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //se clicca su no non succede nulla e l'alert di chiude
            }
        });

        editCity.setText(split[0]);
        editAddress.setText(split[1]);
        editNumberAddress.setText(split[2]);


        AlertDialog alert = builder.create();
        alert.show();
    }
}
