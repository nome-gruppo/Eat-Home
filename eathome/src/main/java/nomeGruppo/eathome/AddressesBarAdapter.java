package nomeGruppo.eathome;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class AddressesBarAdapter extends ArrayAdapter<String>{

    private static final String TAG = "TAG";
    private Context mContext;
    private TextView item;
    private List<String> list;      //lista per controllare che non vengano mostrate repliche

    public AddressesBarAdapter(@NonNull Context context, int resource) {
        super(context, resource);
        this.mContext = context;
        this.list = new ArrayList<>();
    }

    @Override
    public void add(String string){
        if(!list.contains(string)) {
            list.add(string);
            super.add(string);
        }
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
        item.setText(getItem(position));


        return convertView;
    }

}