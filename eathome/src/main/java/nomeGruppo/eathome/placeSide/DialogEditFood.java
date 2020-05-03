package nomeGruppo.eathome.placeSide;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import nomeGruppo.eathome.R;
import nomeGruppo.eathome.foods.Food;

public class DialogEditFood extends AppCompatDialogFragment {
    private Food food;

    private EditText editNameFood;
    private EditText editIngredientsFood;
    private EditText editPriceFood;

    private DialogEditFood.DialogEditFoodListener listener;

    public DialogEditFood(Food food){
        this.food=food;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());

        LayoutInflater inflater=getActivity().getLayoutInflater();
        View view=inflater.inflate(R.layout.dialog_insert_food,null);

        builder.setView(view).setTitle(getActivity().getResources().getString(R.string.edit)).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        }).setPositiveButton(getActivity().getResources().getString(R.string.edit), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                    String nameFood = editNameFood.getText().toString();
                    String ingredientsFood = editIngredientsFood.getText().toString();
                    float priceFood = Float.parseFloat(editPriceFood.getText().toString());
                    listener.editTexts(nameFood, ingredientsFood, priceFood);
                }
        });

        editNameFood = view.findViewById(R.id.editNameFood);
        editIngredientsFood = view.findViewById(R.id.editIngredientsFood);
        editPriceFood = view.findViewById(R.id.editPriceFood);
        editNameFood.setText(food.nameFood);
        editIngredientsFood.setText(food.ingredientsFood);
        editPriceFood.setText(Float.toString(food.priceFood));

        return builder.create();

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (DialogEditFood.DialogEditFoodListener)context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()+"Error");
        }
    }

    public interface DialogEditFoodListener{
        void editTexts(String nameFood, String ingredientsFood, float priceFood);
    }
}