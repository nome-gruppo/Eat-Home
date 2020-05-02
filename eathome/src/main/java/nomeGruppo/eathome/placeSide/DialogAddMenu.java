package nomeGruppo.eathome.placeSide;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import nomeGruppo.eathome.R;

public class DialogAddMenu extends AppCompatDialogFragment {

    private static final String EURO = "€ ";

    private EditText editNameFood;
    private EditText editIngredientsFood;
    private EditText editPriceFood;
    private DialogAddMenuListener listener;
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());

        LayoutInflater inflater=getActivity().getLayoutInflater();
        View view=inflater.inflate(R.layout.dialog_insert_food,null);

        builder.setView(view).setTitle("Name food").setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        }).setPositiveButton(getActivity().getResources().getString(R.string.add), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(editNameFood.getText().toString().trim().length()==0||editIngredientsFood.getText().toString().trim().length()==0||editPriceFood.getText().toString().trim().length()==0){
                    Toast.makeText(getContext(),getActivity().getResources().getString(R.string.fill_all_fields),Toast.LENGTH_SHORT).show();
                    DialogAddMenu dialogAddMenu=new DialogAddMenu();
                    dialogAddMenu.show(getActivity().getSupportFragmentManager(),"Dialog add menu");
                }else {
                    String nameFood = editNameFood.getText().toString();
                    String ingredientsFood = editIngredientsFood.getText().toString();
                    float priceFood = Float.parseFloat(editPriceFood.getText().toString());
                    listener.applyTexts(nameFood, ingredientsFood, priceFood);
                }
            }
        });

        editNameFood = view.findViewById(R.id.editNameFood);
        editIngredientsFood = view.findViewById(R.id.editIngredientsFood);
        editPriceFood = view.findViewById(R.id.editPriceFood);

        editPriceFood.setText(EURO);
        Selection.setSelection(editPriceFood.getText(), editPriceFood.getText().length());

        editPriceFood.addTextChangedListener(new TextWatcher(){

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().startsWith(EURO)){
                    editPriceFood.setText(EURO);
                    Selection.setSelection(editPriceFood.getText(), editPriceFood.getText().length());
                }
            }
        });
        return builder.create();

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (DialogAddMenuListener)context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()+"Error");
        }
    }

    public interface DialogAddMenuListener{
        void applyTexts(String nameFood, String ingredientsFood, float priceFood);
    }
}
