package nomeGruppo.eathome.clientSide;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.libraries.places.api.model.AutocompletePrediction;

import java.util.ArrayList;
import java.util.List;

import nomeGruppo.eathome.R;

public class AddressesBarAdapter extends ArrayAdapter<AutocompletePrediction> {

    private final List<String> list;      //lista per controllare che non vengano mostrate repliche

    public AddressesBarAdapter(@NonNull Context context, int resource) {
        super(context, resource);
        this.list = new ArrayList<>();
    }

    @Override
    public void add(AutocompletePrediction prediction) {

        String address = prediction.getPrimaryText(null).toString();
        if (!list.contains(address)) {
            list.add(address);
            super.add(prediction);
        }
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater != null) {
            convertView = inflater.inflate(R.layout.dropdown_list_layout, parent, false);

            final TextView item = convertView.findViewById(R.id.list_item);
            final AutocompletePrediction prediction = getItem(position);

            if (prediction != null) {
                item.setText(prediction.getPrimaryText(null));
            }
        }


        return convertView;
    }

}