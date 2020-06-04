package nomeGruppo.eathome;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.libraries.places.api.model.AutocompletePrediction;

import java.util.ArrayList;
import java.util.List;

public class AddressesBarAdapter extends ArrayAdapter<AutocompletePrediction>{

    private final List<AutocompletePrediction> list;      //lista per controllare che non vengano mostrate repliche

    public AddressesBarAdapter(@NonNull Context context, int resource) {
        super(context, resource);
        this.list = new ArrayList<>();
    }

    @Override
    public void add(AutocompletePrediction prediction){

        if(!list.contains(prediction)) {
            list.add(prediction);
            super.add(prediction);
        }
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.dropdown_list_layout, null);

            final TextView item = convertView.findViewById(R.id.list_item);

            if(getItem(position) != null) {
                item.setText(getItem(position).getFullText(null));
            }
        }


        return convertView;
    }

}