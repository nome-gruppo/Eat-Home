package nomeGruppo.eathome.placeSide;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import nomeGruppo.eathome.R;

public class DialogAddMenu extends AppCompatDialogFragment {

    private static final char NEW_LINE = '\n';
    private static final char COMMA = ',';

    private EditText editNameFood;
    private EditText editIngredientsFood;
    private EditText editPriceFood;
    private DialogAddMenuListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.dialog_insert_food, null);

        editNameFood = view.findViewById(R.id.editNameFood);
        editIngredientsFood = view.findViewById(R.id.editIngredientsFood);
        editPriceFood = view.findViewById(R.id.editPriceFood);

        builder.setView(view).setTitle("Name food").setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        }).setPositiveButton(getActivity().getResources().getString(R.string.add), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (editNameFood.getText().toString().trim().length() == 0 || editIngredientsFood.getText().toString().trim().length() == 0 || editPriceFood.getText().toString().trim().length() == 0) {
                    Toast.makeText(getContext(), getActivity().getResources().getString(R.string.fill_all_fields), Toast.LENGTH_SHORT).show();
                    DialogAddMenu dialogAddMenu = new DialogAddMenu();
                    dialogAddMenu.show(getActivity().getSupportFragmentManager(), "Dialog add menu");
                } else {
                    String nameFood = editNameFood.getText().toString();
                    String ingredientsFood = editIngredientsFood.getText().toString();
                    ingredientsFood = ingredientsFood.replace(NEW_LINE, COMMA);
                    float priceFood = Float.parseFloat(editPriceFood.getText().toString());
                    listener.applyTexts(nameFood, ingredientsFood, priceFood);
                }
            }
        });
        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (DialogAddMenuListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "Error");
        }
    }

    public interface DialogAddMenuListener {
        void applyTexts(String nameFood, String ingredientsFood, float priceFood);
    }
}
