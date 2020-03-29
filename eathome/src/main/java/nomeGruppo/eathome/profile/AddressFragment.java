package nomeGruppo.eathome.profile;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import nomeGruppo.eathome.R;


public class AddressFragment extends Fragment {

    private EditText addressET;
    private ImageButton editBtn;
    private ImageButton deleteBtn;

    public AddressFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_address, container, false);

        addressET = root.findViewById(R.id.fragment_address_et);
        editBtn = root.findViewById(R.id.fragment_address_btn_edit);
        deleteBtn = root.findViewById(R.id.fragment_address_btn_delete);

        addressET.setClickable(false);

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addressET.setClickable(true);
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO richiesta sei sicuro?
                onDetach();

            }
        });

        // Inflate the layout for this fragment
        return root;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
