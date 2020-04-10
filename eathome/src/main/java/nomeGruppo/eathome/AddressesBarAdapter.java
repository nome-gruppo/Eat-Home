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

    private static final String TAG = "TAG";
    private Context mContext;
    private TextView item;
    private List<AutocompletePrediction> list;      //lista per controllare che non vengano mostrate repliche

    public AddressesBarAdapter(@NonNull Context context, int resource) {
        super(context, resource);
        this.mContext = context;
        this.list = new ArrayList<>();
    }

    @Override
    public void add(AutocompletePrediction prediction){
        if(!list.contains(prediction)) {
            list.add(prediction);
            super.add(prediction);
        }
    }

    @Nullable
    @Override
    public AutocompletePrediction getItem(int position) {
        return super.getItem(position);
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.dropdown_list_layout, null);
        }
        item = convertView.findViewById(R.id.list_item);
        item.setText(getItem(position).getFullText(null));

        return convertView;
    }

}