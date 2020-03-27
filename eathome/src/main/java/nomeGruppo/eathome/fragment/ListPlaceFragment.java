package nomeGruppo.eathome.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.LinkedList;
import java.util.List;

import nomeGruppo.eathome.R;
import nomeGruppo.eathome.actors.Place;
import nomeGruppo.eathome.utility.PlaceAdapter;

public class ListPlaceFragment extends Fragment {

    private OnFragmentEventListener listener=null;
    private PlaceAdapter placeAdapter;
    private List<Place> listPlace;


    interface OnFragmentEventListener{
        void selectElement(Place place);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.listPlace=new LinkedList<>();
        this.placeAdapter=new PlaceAdapter((Context)listener,R.layout.fragment_place_info_homepage_activity,listPlace);
        return super.onCreateView(inflater,container,savedInstanceState);
    }

}
