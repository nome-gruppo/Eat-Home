package nomeGruppo.eathome.placeSide;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Locale;

import nomeGruppo.eathome.R;
import nomeGruppo.eathome.actors.Place;
import nomeGruppo.eathome.db.FirebaseConnection;
import nomeGruppo.eathome.foods.Food;


public class MyMenuAdapter extends ArrayAdapter<Food> {
    private final Place place;
    private final List<Food>list;

    MyMenuAdapter(Context context, int textViewResourceId,
                  List<Food> food, Place place) {
        super(context, textViewResourceId, food);
        this.place=place;
        this.list=food;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView,@NonNull ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(inflater != null) {
            convertView = inflater.inflate(R.layout.listitem_menu, parent, false);
            TextView title = convertView.findViewById(R.id.txtNameFood);
            TextView price = convertView.findViewById(R.id.txtPriceFood);
            TextView ingredients = convertView.findViewById(R.id.txtIngredientsFood);
            ImageButton btnDelete = convertView.findViewById(R.id.btnDeleteFood);
            ImageButton btnEdit = convertView.findViewById(R.id.btnEditFood);
            final Food food = getItem(position);

            if (food != null) {
                title.setText(food.nameFood);
                price.setText(getContext().getResources().getString(R.string.euro, food.priceFood));
                ingredients.setText(food.ingredientsFood);
                btnDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openDialog(food, place);
                    }
                });
                btnEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openDialogEditFood(food);
                    }
                });
            }
        }
        return convertView;
    }

    private void openDialog(final Food food,final Place place){ //creo un alert dialogo per chiedere conferma all'utente della cancellazione
        AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.confirmation);
        builder.setMessage(getContext().getResources().getString(R.string.cancelConfirmation)+" "
                +food.nameFood);//prelevo la stringa di richiesta conferma da values.strings
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteFoodFirebase(food,place);//se clicca su ok allora cancello il cibo dalla tabella foods del database
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //se clicca su no non succede nulla e l'alert di chiude
            }
        });
        AlertDialog alert = builder.create();
        alert.show();

    }

    private void deleteFoodFirebase(final Food food, final Place place) {
        final FirebaseConnection firebaseConnection = new FirebaseConnection();
        //ordino i cibi in base al nome e poi seleziono il cibo con lo stesso nome del cibo da rimuovere
        firebaseConnection.getmDatabase().child("Foods").child(place.idPlace).orderByChild("nameFood").equalTo(food.nameFood).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    snapshot.getRef().removeValue();//rimuovo il cibo dal database
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        Intent homePage=new Intent(getContext(), PlaceHomeActivity.class);
        homePage.putExtra(FirebaseConnection.PLACE,place);
        getContext().startActivity(homePage);
    }

    private void openDialogEditFood(final Food food){
        AlertDialog.Builder builder=new AlertDialog.Builder(getContext());

        Activity activity = (Activity) getContext();
        LayoutInflater inflater=activity.getLayoutInflater();
        View view=inflater.inflate(R.layout.dialog_insert_food, (ViewGroup) ((Activity) getContext()).getCurrentFocus(),false);

        final EditText editNameFood = view.findViewById(R.id.editNameFood);
        final EditText editIngredientsFood = view.findViewById(R.id.editIngredientsFood);
        final EditText editPriceFood = view.findViewById(R.id.editPriceFood);
        editNameFood.setText(food.nameFood);
        editIngredientsFood.setText(food.ingredientsFood);
        editPriceFood.setText(String.format(Locale.getDefault(),"%.02f", food.priceFood));
        builder.setView(view).setTitle(getContext().getResources().getString(R.string.edit)).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        }).setPositiveButton(getContext().getResources().getString(R.string.edit), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String nameFood = editNameFood.getText().toString();
                String ingredientsFood = editIngredientsFood.getText().toString();
                float priceFood = Float.parseFloat(editPriceFood.getText().toString());
                Food editFood=new Food();
                editFood.setName(nameFood);
                editFood.setIngredients(ingredientsFood);
                editFood.setPrice(priceFood);
                list.remove(food);
                list.add(editFood);
                notifyDataSetChanged();
                editFoodFirebase(food,editFood);
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void editFoodFirebase(Food oldFood, Food editFood){
        final FirebaseConnection firebaseConnection = new FirebaseConnection();
        //seleziono il cibo con lo stesso nome del cibo da modificare
        firebaseConnection.getmDatabase().child(FirebaseConnection.FOOD_NODE).child(place.idPlace).child(oldFood.idFood).setValue(editFood);

        Intent homePage=new Intent(getContext(), PlaceHomeActivity.class);
        homePage.putExtra(FirebaseConnection.PLACE,place);
        getContext().startActivity(homePage);
    }
}
