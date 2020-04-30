package nomeGruppo.eathome.utility;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import nomeGruppo.eathome.R;
import nomeGruppo.eathome.actors.Place;
import nomeGruppo.eathome.db.StorageConnection;

public class PlaceAdapter extends ArrayAdapter<Place> {

    public PlaceAdapter(Context context, int textViewResourceId,
                         List<Place> place) {
        super(context, textViewResourceId, place);
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.fragment_place_info_homepage_activity, null);
        TextView title = (TextView)convertView.findViewById(R.id.txtNamePlaceFragment);
        TextView type = (TextView)convertView.findViewById(R.id.txtTypePlaceFragment);
        TextView address=(TextView)convertView.findViewById(R.id.txtAddressPlaceFragment);
        ImageView imgPlace=(ImageView)convertView.findViewById(R.id.imgPlaceFragment);
        RatingBar ratingBar=convertView.findViewById(R.id.ratingBarPlaceFragment);
        Place place = getItem(position);
        title.setText(place.namePlace);
        type.setText(place.categories);
        address.setText(place.cityPlace+" "+place.addressPlace+", "+place.addressNumPlace);

        ratingBar.setRating(place.valuation);

        if(!place.takesBookingPlace){
            ImageView mImageView = convertView.findViewById(R.id.list_places_booking_icon);
            mImageView.setVisibility(View.GONE);
        }

        if(!place.takesOrderPlace){
            ImageView mImageView = convertView.findViewById(R.id.list_places_delivery_icon);
            mImageView.setVisibility(View.GONE);
        }

        setImage(imgPlace, place);

        return convertView;
    }

    public void setImage(final ImageView imgPlace, Place place){
        StorageConnection storageConnection=new StorageConnection();//apro la connessione allo Storage di Firebase
        StorageReference storageReference=storageConnection.storageReference(place.idPlace);//l'immagine nello Storage ha lo stesso nome del codice del ristorante

        //metodo di lettura immagine tramite byte
        storageReference.getBytes(3840*3840)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bitmap=BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                        imgPlace.setImageBitmap(bitmap);
                    }

                });
    }
}
