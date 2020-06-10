package nomeGruppo.eathome.utility;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import nomeGruppo.eathome.R;
import nomeGruppo.eathome.actors.Place;
import nomeGruppo.eathome.db.StorageConnection;

import static nomeGruppo.eathome.utility.UtilitiesAndControls.PICT_SIZE_MAX;

public class PlaceAdapter extends ArrayAdapter<Place> {

    private final Resources res;

    public PlaceAdapter(Context context, int textViewResourceId,
                         List<Place> place) {
        super(context, textViewResourceId, place);
        this.res = context.getResources();
    }



    @NonNull
    @Override
    public View getView(int position, View convertView,@NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(inflater != null) {
            convertView = inflater.inflate(R.layout.listitem_place_homepage, parent, false);
            final TextView title = convertView.findViewById(R.id.txtNamePlaceFragment);
            final TextView type = convertView.findViewById(R.id.txtTypePlaceFragment);
            final TextView address = convertView.findViewById(R.id.txtAddressPlaceFragment);
            final ImageView imgPlace = convertView.findViewById(R.id.imgPlaceFragment);
            final RatingBar ratingBar = convertView.findViewById(R.id.ratingBarPlaceFragment);

            final Place place = getItem(position);
            if (place != null) {
                title.setText(place.namePlace);
                type.setText(place.categories);
                address.setText(res.getString(R.string.addressPrinted, place.addressPlace, place.addressNumPlace, place.cityPlace));

                ratingBar.setRating(place.valuation);

                if (!place.takesBookingPlace) {
                    ImageView mImageView = convertView.findViewById(R.id.list_places_booking_icon);
                    mImageView.setVisibility(View.GONE);
                }

                if (!place.takesOrderPlace) {
                    ImageView mImageView = convertView.findViewById(R.id.list_places_delivery_icon);
                    mImageView.setVisibility(View.GONE);
                }

                setImage(imgPlace, place);
            }
        }
        return convertView;
    }

    private void setImage(final ImageView imgPlace, Place place){
        StorageConnection storageConnection=new StorageConnection();//apro la connessione allo Storage di Firebase
        StorageReference storageReference=storageConnection.storageReference(place.idPlace);//l'immagine nello Storage ha lo stesso nome del codice del ristorante

        //metodo di lettura immagine tramite byte
        storageReference.getBytes(PICT_SIZE_MAX*PICT_SIZE_MAX)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bitmap=BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                        imgPlace.setImageBitmap(bitmap);
                    }

                });
    }
}
