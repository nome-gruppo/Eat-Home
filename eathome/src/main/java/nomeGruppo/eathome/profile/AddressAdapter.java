package nomeGruppo.eathome.profile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import nomeGruppo.eathome.R;


public class AddressAdapter extends ArrayAdapter<String> {

    private EditText addressET;
    private ImageButton editBtn;
    private ImageButton deleteBtn;


    public AddressAdapter(@NonNull Context context, int resource, List<String> list) {
        super(context, resource, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.fragment_address, null);

        }

        addressET = convertView.findViewById(R.id.fragment_address_et);
        editBtn = convertView.findViewById(R.id.fragment_address_btn_edit);
        deleteBtn = convertView.findViewById(R.id.fragment_address_btn_delete);

        String address = getItem(position);
        addressET.setHint(address);

        addressET.setEnabled(false);

        editBtn.setTag(position);



        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addressET.setEnabled(true);
            }
        });


        return convertView;
    }
}
